import paho.mqtt.client as client
from threading import Thread
from webcam import WebCam
import time
from houserfid import HouseRFID
import paho.mqtt.publish as publisher

BROKER_URL = "223.171.136.185"

class MqttWorker:
    def __init__(self):
        self.MY_DONG = "000"
        
        self.client = client.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        
        self.rfid = HouseRFID()

    
    # 실제 촬영 작업 (스레드에서 실행)
    def capture_image(self, camera_index, location):
        try:
            cam = WebCam(camera_index)

            time.sleep(0.2)

            image_b64 = cam.capture_image()

            self.client.publish(
                f"jjld/cargate/{location}/car_image",
                image_b64
            )

            print(f"[{location}] 이미지 MQTT 전송 완료")

            cam.clean()

        except Exception as e:
            print(f"[{location}] 이미지 촬영/전송 실패:", e)
            
            
    # 세대 관리 RFID
    def publish_rfid(self, uid):
        topic = f"jjld/house/{self.MY_DONG}/card/result"
        publisher.single(topic, uid, hostname=BROKER_URL, port=1884)

    
    # MQTT 연결 콜백
    def on_connect(self, client, userdata, flags, rc):
        print("connect...:::", rc)
        if rc == 0:
            client.subscribe("jjld/cargate/+/gate_sub")
            client.subscribe(f"jjld/house/{self.MY_DONG}/card/#")
        else:
            print("연결실패")

    
    # 메시지 수신 콜백
    def on_message(self, client, userdata, message):
        payload = message.payload.decode("utf-8")
        topic = message.topic

        parts = topic.split("/")  # ['jjld','cargate','entry','gate_sub']
        
        if parts[1] == "cargate":

            if payload == "detect":

                if parts[2] == "entry":
                    print("입차 감지")
                    Thread(
                        target=self.capture_image,
                        args=(0, "entry"),
                        daemon=True
                    ).start()

                elif parts[2] == "exit":
                    print("출차 감지")
                    Thread(
                        target=self.capture_image,
                        args=(1, "exit"),
                        daemon=True
                    ).start()
            
        elif parts[1] == "house" and parts[3] == "card":
            if payload == "start":
                self.rfid.start(self.publish_rfid)
                
            elif payload == "stop":
                self.rfid.stop()


    
    # MQTT 연결 시작
    def mymqtt_connect(self):
        try:
            print("브로커 연결 시작")
            self.client.connect(BROKER_URL, 1884, 60)
            self.client.loop_forever()
        except KeyboardInterrupt:
            pass
        finally:
            print("종료")
