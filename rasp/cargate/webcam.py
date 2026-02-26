import cv2
import time
import base64

class WebCam:
    def __init__(self, camera_index=0):
        self.camera_index = camera_index
        self.width = 640
        self.height = 480

        self.cap = cv2.VideoCapture(self.camera_index, cv2.CAP_V4L2)

        if not self.cap.isOpened():
            raise RuntimeError(f"USB 카메라 {self.camera_index} 열기 실패")

        self.cap.set(cv2.CAP_PROP_FOURCC,
                     cv2.VideoWriter_fourcc(*'YUYV'))
        self.cap.set(cv2.CAP_PROP_FRAME_WIDTH, self.width)
        self.cap.set(cv2.CAP_PROP_FRAME_HEIGHT, self.height)

        time.sleep(0.2)
        for _ in range(10):
            self.cap.read()

    def capture_image(self) -> str:
        ret, frame = self.cap.read()
        if not ret:
            raise RuntimeError("프레임 읽기 실패")

        if len(frame.shape) == 2 or frame.shape[2] == 2:
            frame = cv2.cvtColor(frame, cv2.COLOR_YUV2BGR_YUYV)

        ret, buffer = cv2.imencode(".jpg", frame)
        if not ret:
            raise RuntimeError("JPEG 인코딩 실패")

        return base64.b64encode(buffer).decode("utf-8")

    def clean(self):
        if self.cap:
            self.cap.release()
            self.cap = None
