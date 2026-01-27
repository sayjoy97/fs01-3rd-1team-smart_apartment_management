import cv2
import torch
import easyocr
from ultralytics import YOLO
import time
import os
import re

# 1. 기준값 설정
YOLO_CONF_THRESHOLD = 0.50
OCR_CONF_THRESHOLD = 0.60
FINAL_CONF_THRESHOLD = 0.35

PLATE_PATTERN = re.compile(r"\d{2,3}[가-힣]\d{4}")


# 2. GPU 설정
device = "cuda" if torch.cuda.is_available() else "cpu"
print(f"[INFO] Device: {device}")


# 3. 모델 로드
model = YOLO("Koushim_yolov8-license-plate-detection/best.pt")
model.to(device)

reader = easyocr.Reader(
    ['ko', 'en'],
    gpu=torch.cuda.is_available()
)


# 4. 유틸 함수
def normalize_text(text):
    return (
        text.replace("O", "0")
            .replace("I", "1")
            .replace("Z", "2")
    )

# OCR 결과에서 번호판을 문자열하고 평균 confidence 구함
def extract_plate_with_conf(ocr_results):
    texts, confs = [], []

    for text, conf in ocr_results:
        texts.append(text)
        confs.append(conf)

    if not texts:
        return None, 0.0

    joined = normalize_text("".join(texts).replace(" ", ""))
    match = PLATE_PATTERN.search(joined)

    if not match:
        return None, 0.0

    plate = match.group()
    avg_conf = sum(confs) / len(confs)

    if len(plate) not in (7, 8):
        return None, avg_conf

    return plate, avg_conf

# 5. 메인 로직
def detect_and_recognize(image_path):
    total_start = time.perf_counter()

    img = cv2.imread(image_path)
    if img is None:
        raise ValueError("이미지를 불러올 수 없습니다.")

    h, w, _ = img.shape
    os.makedirs("output/plates", exist_ok=True)

    # ---------- YOLO ----------
    yolo_start = time.perf_counter()
    results = model(img, conf=YOLO_CONF_THRESHOLD)
    yolo_time = time.perf_counter() - yolo_start

    final_plates = []

    # ---------- OCR ----------
    ocr_start = time.perf_counter()

    for i, box in enumerate(results[0].boxes):
        yolo_conf = float(box.conf[0])
        x1, y1, x2, y2 = map(int, box.xyxy[0])

        pad = 5
        x1, y1 = max(0, x1 - pad), max(0, y1 - pad)
        x2, y2 = min(w, x2 + pad), min(h, y2 + pad)

        plate_img = img[y1:y2, x1:x2]
        cv2.imwrite(f"output/plate_{i}.jpg", plate_img)

        ocr_raw = reader.readtext(
            plate_img,
            detail=1,
            paragraph=False
        )

        plate, ocr_conf = extract_plate_with_conf(
            [(r[1], r[2]) for r in ocr_raw]
        )

        final_conf = yolo_conf * ocr_conf

        if (
            plate
            and ocr_conf >= OCR_CONF_THRESHOLD
            and final_conf >= FINAL_CONF_THRESHOLD
        ):
            final_plates.append(plate)
            label = f"{plate} ({final_conf:.2f})"
        else:
            label = "인식 실패"

        cv2.rectangle(img, (x1, y1), (x2, y2), (0, 255, 0), 2)
        cv2.putText(
            img, label, (x1, y1 - 10),
            cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2
        )

    ocr_time = time.perf_counter() - ocr_start
    total_time = time.perf_counter() - total_start

    cv2.imwrite("output/result/result.jpg", img)

    return final_plates, yolo_time, ocr_time, total_time


# 6. 실행
if __name__ == "__main__":
    plates, yolo_t, ocr_t, total_t = detect_and_recognize("images/test1.jpg")

    print("\n===== 최종 번호판 =====")
    for p in plates:
        print(p)

    print("\n===== 실행 시간 =====")
    print(f"YOLO : {yolo_t*1000:.2f} ms")
    print(f"OCR  : {ocr_t*1000:.2f} ms")
    print(f"TOTAL: {total_t*1000:.2f} ms")
