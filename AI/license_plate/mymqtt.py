import base64
import os
from threading import Thread

import cv2
import numpy as np
import paho.mqtt.client as mqtt

from plate_detector_byAI import LicensePlateRecognizer


# MQTT 작업자 클래스
class MqttWorker:
    # 생성자에서 mqtt통신할 수 있는 객체생성, 필요한 다양한 객체생성, 콜백함수등록
    def __init__(self):
        self.client = mqtt.Client(clean_session=True)
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message

        self.AImodel = LicensePlateRecognizer()

        self.broker = os.environ.get('AI_BROKER')

    # mqtt 메세지로 받은 base64 형식을 cv2 디코딩 작업
    def base64_to_cv2(self, base64_str):
        # base64 디코딩
        img_bytes = base64.b64decode(base64_str)

        # numpy 배열로 변환
        img_array = np.frombuffer(img_bytes, dtype=np.uint8)

        # OpenCV 이미지로 디코딩
        img = cv2.imdecode(img_array, cv2.IMREAD_COLOR)
        return img

    def publish(self, topic, msg):
        self.client.publish(topic, msg)

    # broker 연결 후 실행될 콜백 - rc가 0이면 성공접속, 1이면 실패
    def on_connect(self, client, userdata, flags, rc):
        print("connect...:::" + str(rc))
        if rc == 0:  # 연결성공 -> 구독신청
            client.subscribe("jjld/cargate/entry/#")  # 구독신청
            client.subscribe("jjld/cargate/exit/#")
            print("구독성공 : jjld/cargate/entry/#")
            print("구독성공 : jjld/cargate/exit/#")

        else:
            print("연결실패")

    # 메시지가 수신되면 자동으로 호출되는 메소드
    def on_message(self, client, userdata, message):
        myval = message.payload.decode("utf-8")
        topic = message.topic

        if topic == "jjld/cargate/entry/car_image" or topic == "jjld/cargate/exit/car_image":
            # base64로 받은 메세지를 이미지로 디코딩하는 작업
            img = self.base64_to_cv2(myval)

            # 이미지 잘 불러와지는지 테스트
            cv2.imwrite("image.jpg", img)

            gate = topic.split("/")[2]

            # 디코딩한 이미지를 ai모델로 넘겨 번호판을 텍스트로 추출
            result = self.AImodel.detect_and_recognize(img, gate)


            # 추출 결과
            if result["success"]:

                file_name = result["file_name"]

                parts = file_name.split("_")
                plate_number = parts[3].split(".")[0]

                print("처리 날짜: " + parts[0], end=", ")
                print("소요시간 : " + parts[2] + "ms", end=", ")
                print("추출한 번호판 텍스트 : " + plate_number)

                self.publish(f"jjld/cargate/{gate}/process_result", file_name)

            else:
                self.publish(f"jjld/cargate/{gate}/gate_command", "reload")
                print(result["message"])


    # mqtt서버연결을 하는 메소드 - 사용자정의
    def mqtt_connect(self):
        try:
            print("브로커 연결 시작하기")
            self.client.connect(self.broker, 1884, 60)

            mymqtt_obj = Thread(target=self.client.loop_forever)
            mymqtt_obj.start()

            mymqtt_obj.join()
        except KeyboardInterrupt:
            pass
        finally:
            print("종료")