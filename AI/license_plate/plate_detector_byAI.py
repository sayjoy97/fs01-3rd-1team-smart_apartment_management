import os
import re
import time
from datetime import datetime
import cv2
import easyocr
import numpy as np
import torch
from ultralytics import YOLO


class LicensePlateRecognizer:
    # 1. 임계값 대폭 조정 (모델/종이 번호판 특성 고려)
    YOLO_CONF_THRESHOLD = 0.20  # 장난감/종이 번호판을 위해 낮춤
    OCR_CONF_THRESHOLD = 0.10
    FINAL_CONF_THRESHOLD = 0.15

    # 한국 번호판 패턴 (숫자2~3 + 한글 + 숫자4)
    PLATE_PATTERN = re.compile(r"(\d{2,3})([가-힣]{1,2})(\d{4})")

    def __init__(self):
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        print(f"[INFO] Device: {self.device}")

        # 모델 로드
        try:
            self.model = YOLO("Koushim_yolov8-license-plate-detection/best.pt")
            self.model.to(self.device)
        except Exception as e:
            print(f"[ERROR] 모델 로딩 실패: {e}")

        # EasyOCR 설정 - paragraph=True 옵션이 흩어진 글자를 모으는 데 유리함
        self.reader = easyocr.Reader(['ko', 'en'], gpu=torch.cuda.is_available())
        self.img_path = os.environ.get("AI_IMAGE_PATH", "./output")
        if not os.path.exists(self.img_path): os.makedirs(self.img_path)

    def preprocess_simple(self, img):
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        # 단순히 크기만 키워도 인식률이 확 올라갑니다.
        resized = cv2.resize(gray, None, fx=2, fy=2, interpolation=cv2.INTER_CUBIC)
        return resized

    def extract_text(self, img_input):
        # detail=0, paragraph=True로 설정하여 문장 단위로 읽어오기
        results = self.reader.readtext(img_input, detail=0, paragraph=True)
        joined_text = "".join(results).replace(" ", "")

        # 숫자와 한글만 남기기 (특수문자 제거)
        clean_text = re.sub(r'[^0-9가-힣]', '', joined_text)
        print(f"[DEBUG OCR] 추출된 텍스트: {clean_text}")

        match = self.PLATE_PATTERN.search(clean_text)
        if match:
            return f"{match.group(1)}{match.group(2)} {match.group(3)}"
        return None

    def detect_and_recognize(self, image_input, gate):
        total_start = time.perf_counter()
        img = cv2.imread(image_input) if isinstance(image_input, str) else image_input
        if img is None: return {"success": False, "message": "이미지 로드 실패"}

        # --- STEP 1: YOLO 탐지 ---
        results = self.model(img, conf=self.YOLO_CONF_THRESHOLD, verbose=False)

        found_plate = False
        for box in results[0].boxes:
            found_plate = True
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            # 약간의 마진 추가f
            crop = img[max(0, y1 - 10):y2 + 10, max(0, x1 - 10):x2 + 10]

            processed_crop = self.preprocess_simple(crop)
            plate_no = self.extract_text(processed_crop)

            if plate_no:
                return self.success_return(img, plate_no, total_start, gate)

        # --- STEP 2: YOLO가 실패했을 경우 (Fallback) ---
        # 사진에 번호판이 크게 찍히는 경우, YOLO 없이 전체 이미지에서 OCR 시도
        if not found_plate:
            print("[WARN] YOLO 탐지 실패. 전체 이미지 OCR 시도...")
            full_img_processed = self.preprocess_simple(img)
            plate_no = self.extract_text(full_img_processed)
            if plate_no:
                return self.success_return(img, plate_no, total_start, gate)

        return {"success": False, "message": "번호판 패턴 인식 실패"}

    def success_return(self, img, plate, start_time, gate):
        # 소요 시간 계산 (ms 단위)
        total_time = int((time.perf_counter() - start_time) * 1000)

        # 현재 시간 포맷팅
        now_dt = datetime.now()
        date_str = now_dt.strftime('%Y%m%d')
        time_str = now_dt.strftime('%H%M%S')

        # 공백 제거된 번호판 번호
        pure_plate = plate.replace(' ', '')

        # 요청하신 형식: 년월일_시분초_게이트명_소요시간_차번호_차번호.jpg
        filename = f"{date_str}_{time_str}_{gate}_{total_time}_{pure_plate}.jpg"

        # 이미지 저장
        save_full_path = os.path.join(self.img_path, filename)
        cv2.imwrite(save_full_path, img)

        print(f"[INFO] 파일 저장 완료: {filename}")

        return {
            "success": True,
            "file_name": filename,
            "plate": plate,
            "duration": total_time
        }