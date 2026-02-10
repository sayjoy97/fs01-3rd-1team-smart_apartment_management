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
    YOLO_CONF_THRESHOLD = 0.45
    OCR_CONF_THRESHOLD = 0.45
    FINAL_CONF_THRESHOLD = 0.30

    PLATE_PATTERN = re.compile(r"\d{2,3}[가-힣]\d{4}")

    def __init__(self):
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        print(f"[INFO] Device: {self.device}")

        self.model = YOLO("Koushim_yolov8-license-plate-detection/best.pt")
        self.model.to(self.device)

        self.reader = easyocr.Reader(
            ['ko', 'en'],
            gpu=torch.cuda.is_available()
        )

        self.img_path = os.environ.get("AI_IMAGE_PATH")

    # ---------------------------
    # 텍스트 정규화
    # ---------------------------
    @staticmethod
    def normalize_text(text: str) -> str:
        return (
            text.replace("O", "0")
                .replace("I", "1")
                .replace("Z", "2")
        )

    @staticmethod
    def format_plate_with_space(plate: str) -> str:
        match = re.match(r"(\d{2,3}[가-힣])(\d{4})", plate)
        return f"{match.group(1)} {match.group(2)}" if match else plate


    # 기울기 보정
    def deskew_plate(self, gray_img):
        edges = cv2.Canny(gray_img, 50, 150)
        lines = cv2.HoughLines(edges, 1, np.pi / 180, 80)

        if lines is None:
            return gray_img

        angles = []
        for rho, theta in lines[:, 0]:
            angle = (theta - np.pi / 2) * 180 / np.pi
            angles.append(angle)

        median_angle = np.median(angles)

        h, w = gray_img.shape
        M = cv2.getRotationMatrix2D((w // 2, h // 2), median_angle, 1.0)
        rotated = cv2.warpAffine(
            gray_img, M, (w, h),
            flags=cv2.INTER_CUBIC,
            borderMode=cv2.BORDER_REPLICATE
        )
        return rotated


    # OCR 전처리
    def preprocess_plate_for_ocr(self, plate_img):
        gray = cv2.cvtColor(plate_img, cv2.COLOR_BGR2GRAY)

        # 확대
        gray = cv2.resize(
            gray, None,
            fx=2.5, fy=2.5,
            interpolation=cv2.INTER_CUBIC
        )

        # 기울기 보정
        gray = self.deskew_plate(gray)

        # 대비 향상
        clahe = cv2.createCLAHE(2.0, (8, 8))
        gray = clahe.apply(gray)

        # 이진화
        binary = cv2.adaptiveThreshold(
            gray, 255,
            cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
            cv2.THRESH_BINARY,
            31, 10
        )

        return binary


    # OCR 결과 처리
    def extract_plate_with_conf(self, ocr_results):
        texts, confs = [], []

        for text, conf in ocr_results:
            clean = self.normalize_text(text.replace(" ", ""))
            if re.search(r"[0-9가-힣]", clean):
                texts.append(clean)
                confs.append(conf)

        if not texts:
            return None, 0.0

        joined = "".join(texts)
        match = self.PLATE_PATTERN.search(joined)
        if not match:
            return None, 0.0

        return match.group(), sum(confs) / len(confs)

    # OCR 추출 실패시 리턴
    def notify_failure(self, reason):
        print(f"[FAILURE] {reason}")

        return {"success": False, "message": reason}

    # OCR 추출 성공시 이미지 저장
    def save_plate_image(self, img, plate, total_time, gate):
        now = datetime.now().strftime("%Y%m%d %H%M%S")
        filename = f"{now}_{gate}_{int(total_time * 1000)}_{plate}.jpg"
        path = os.path.join(self.img_path, filename)
        cv2.imwrite(path, img)
        print(f"[INFO] Saved: {path}")
        return filename


    # 메인 로직
    def detect_and_recognize(self, image_input, gate):
        total_start = time.perf_counter()

        img = cv2.imread(image_input) if isinstance(image_input, str) else image_input
        if img is None:
            return self.notify_failure("이미지 로드 실패")

        h, w, _ = img.shape
        results = self.model(img, conf=self.YOLO_CONF_THRESHOLD)

        for box in results[0].boxes:
            yolo_conf = float(box.conf[0])
            x1, y1, x2, y2 = map(int, box.xyxy[0])

            pad = int((x2 - x1) * 0.2)
            x1, y1 = max(0, x1 - pad), max(0, y1 - pad)
            x2, y2 = min(w, x2 + pad), min(h, y2 + pad)

            plate_img = img[y1:y2, x1:x2]

            ocr_input = self.preprocess_plate_for_ocr(plate_img)

            ocr_raw = self.reader.readtext(
                ocr_input,
                detail=1,
                paragraph=False,
                low_text=0.4,
                contrast_ths=0.4
            )

            plate, ocr_conf = self.extract_plate_with_conf(
                [(r[1], r[2]) for r in ocr_raw]
            )

            final_conf = yolo_conf * ocr_conf

            if plate and final_conf >= self.FINAL_CONF_THRESHOLD:
                plate = self.format_plate_with_space(plate)
                total_time = time.perf_counter() - total_start
                file_name = self.save_plate_image(img, plate, total_time, gate)
                return {"success": True, "file_name": file_name}

        return self.notify_failure("번호판 인식 실패")
