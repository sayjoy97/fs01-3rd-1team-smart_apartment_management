import cv2
import torch
import easyocr
from ultralytics import YOLO
import time
import os
import re
from datetime import datetime

class LicensePlateRecognizer:
    # 기준값과 패턴 설정
    YOLO_CONF_THRESHOLD = 0.50 # YOLO 탐지 신뢰도 기준
    OCR_CONF_THRESHOLD = 0.60  # OCR 인식 신뢰도 기준
    FINAL_CONF_THRESHOLD = 0.35 # 최종 confidence 기준 (YOLO * OCR)
    PLATE_PATTERN = re.compile(r"\d{2,3}[가-힣]\d{4}") # 한국 번호판 패턴 (숫자2~3 + 한글 + 숫자4)

    def __init__(self):
        # GPU 설정
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        print(f"[INFO] Device: {self.device}")

        # 모델 로드
        # yolo
        self.model = YOLO("Koushim_yolov8-license-plate-detection/best.pt")
        self.model.to(self.device)

        # easyocr
        self.reader = easyocr.Reader(['ko', 'en'], gpu=torch.cuda.is_available())

        # 결과 저장 폴더 생성
        os.makedirs("output/result", exist_ok=True)

    # 텍스트 정규화
    # OCR에서 O/I/Z를 0/1/2로 변환
    @staticmethod
    def normalize_text(text: str) -> str:
        return text.replace("O", "0").replace("I", "1").replace("Z", "2")

    # 한국 번호판 공백 추가
    @staticmethod
    def format_plate_with_space(plate: str) -> str:
        match = re.match(r"(\d{2,3}[가-힣])(\d{4})", plate)
        if match:
            # 앞부분(숫자+한글) + 공백 + 뒤 숫자4자리
            return f"{match.group(1)} {match.group(2)}"
        else:
            return plate

    # OCR 결과에서 번호판과 평균 confidence 추출
    def extract_plate_with_conf(self, ocr_results):
        texts, confs = [], []

        # OCR 결과 순회
        for text, conf in ocr_results:
            texts.append(text)
            confs.append(conf)

        if not texts:
            return None, 0.0

        # 공백 제거 및 O/I/Z 정규화
        joined = self.normalize_text("".join(texts).replace(" ", ""))
        match = self.PLATE_PATTERN.search(joined)

        if not match:
            return None, 0.0

        plate = match.group()
        avg_conf = sum(confs) / len(confs)

        # 번호판 길이 확인 (7~8자리)
        if len(plate) not in (7, 8):
            return None, avg_conf

        return plate, avg_conf

    # 실패 시 메시지 처리
    def notify_failure(self, reason="recognition failure"):
        print(f"[FAILURE] {reason}")
        return {"success": False, "message": reason}

    # 인식 성공 시 이미지 저장
    def save_plate_image(self, img, plate, total_time):
        now = datetime.now().strftime("%Y%m%d %H%M%S")
        filename = f"{now}_{int(total_time * 1000)}_{plate}.jpg"
        path = os.path.join("output/result", filename)
        cv2.imwrite(path, img)
        print(f"[INFO] Saved image: {path}")

        return filename

    # 메인 인식 로직
    def detect_and_recognize(self, image_input):
        total_start = time.perf_counter()

        # 이미지 불러오기
        if isinstance(image_input, str):
            img = cv2.imread(image_input)
            if img is None:
                raise ValueError("이미지를 불러올 수 없습니다.")

        # 이미 OpenCV 이미지일 경우
        else:
            img = image_input

        h, w, _ = img.shape

        # YOLO 번호판 탐지
        results = self.model(img, conf=self.YOLO_CONF_THRESHOLD)

        final_plates = []

        # 탐지된 박스에 OCR 수행
        for i, box in enumerate(results[0].boxes):
            yolo_conf = float(box.conf[0])
            x1, y1, x2, y2 = map(int, box.xyxy[0])

            # 패딩 추가 -> 주변 여유 공간 확보
            pad = 5
            x1, y1 = max(0, x1 - pad), max(0, y1 - pad)
            x2, y2 = min(w, x2 + pad), min(h, y2 + pad)

            plate_img = img[y1:y2, x1:x2]

            # OCR 수행
            ocr_raw = self.reader.readtext(plate_img, detail=1, paragraph=False)
            plate, ocr_conf = self.extract_plate_with_conf([(r[1], r[2]) for r in ocr_raw])

            # 최종 confidence = YOLO * OCR
            final_conf = yolo_conf * ocr_conf

            # 인식 성공 시
            if plate and ocr_conf >= self.OCR_CONF_THRESHOLD and final_conf >= self.FINAL_CONF_THRESHOLD:

                # 번호판 사이 공백넣기
                plate = self.format_plate_with_space(plate)

                final_plates.append(plate)
                label = f"{plate} ({final_conf:.2f})"

                total_time = time.perf_counter() - total_start

                # 인식 성공 시만 이미지 저장, 파일명 : 저장시간_걸린시간_차량번호.jpg
                file_name = self.save_plate_image(img, plate, total_time)

            else:
                # 인식 실패 라벨
                label = "인식 실패"

            cv2.rectangle(img, (x1, y1), (x2, y2), (0, 255, 0), 2)
            cv2.putText(img, label, (x1, y1 - 10),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2)

        # 번호판이 감지되지 않았을 경우
        if not final_plates:
            return self.notify_failure("번호판을 감지하지 못했습니다.")

        # 인식 성공 시
        return {"success": True, "file_name": file_name}
