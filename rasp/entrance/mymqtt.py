import paho.mqtt.client as client
import paho.mqtt.publish as publisher
from threading import Thread, Timer
import time
import serial
import RPi.GPIO as GPIO
from mylcd import MyLcd
from mysensor import MySensor
from mycamera import MyCamera

class MqttWorker:
    def __init__(self):
        self.MY_DONG = "101"
        self.BROKER_IP = "192.168.14.99" 
        
        # 클래스 인스턴스화
        self.lcd = MyLcd()
        self.sensor = MySensor()
        self.camera = MyCamera()
        
        # MQTT 설정
        self.client = client.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        
        # 시리얼 설정
        try:
            self.ser = serial.Serial('/dev/ttyACM0', 9600, timeout=0.1)
        except:
            self.ser = None
            print("Serial port /dev/ttyACM0 not found.")

        self.input_text = ""
        self.input_lock = False
        self.is_streaming = False

    def show_home(self):
            """초기 대기 화면으로 복구 및 입력 잠금 해제"""
            self.input_text = ""
            self.input_lock = False  
            if self.ser:
                self.ser.reset_input_buffer() 
            self.lcd.display(f"HappyDong-{self.MY_DONG}", "Enter PW/Card")

    def door_sequence(self):
        print("\n[시스템] 문 열림 시작 (2.2초 구동)")
        
        self.lcd.clear()
        time.sleep(0.1) 
        
        # 아두이노에 OPEN 전송
        if self.ser: 
            self.ser.write(b'OPEN\n')
        
        time.sleep(2.7) 
        
        self.lcd.display("ACCESS GRANTED", "WELCOME")
        time.sleep(1.0)

        # 거리 감지 및 막대 그래프 
        start_wait = time.time()
        detected = False
        while time.time() - start_wait < 3.0:
            dist = self.sensor.get_distance()
            bar_count = int(min(dist, 100) // 5) 
            visual_bar = "#" * bar_count + "-" * (20 - bar_count)
            print(f"거리: {dist:5.1f} cm | [{visual_bar}]", end='\r')

            if dist < 10: 
                detected = True
                break
            time.sleep(0.1)
        
        # 문 닫기 
        if detected:
            self.lcd.display("PASSING...", "KEEP OPEN")
            while self.sensor.get_distance() < 12: time.sleep(0.2)
            time.sleep(1.0)
            
            if self.ser: self.ser.write(b'CLOSE\n')
            time.sleep(2.5) 
            self.lcd.display("DOOR CLOSED", "Good Bye")
        else:
            self.lcd.display("NO ONE DETECTED", "CLOSING...")
            time.sleep(0.5)
            if self.ser: self.ser.write(b'CLOSE\n')
            time.sleep(2.5) 
            self.lcd.display("AUTO CLOSED", "Check Sensor")

        time.sleep(2.0)
        self.show_home()

    def serial_monitor(self):
        if not self.ser: return
        self.ser.reset_input_buffer()
        while True:
            if self.ser.in_waiting > 0:
                line = self.ser.readline().decode('utf-8', errors='ignore').strip()
                if line.startswith("KEY:"):
                    key = line.split(":")[1]
                    
                    if key == "RESET": # 아두이노에서 보낸 리셋
                        self.input_text = ""
                        self.show_home()
                        continue

                    if not self.input_lock:
                        if key == '*': self.input_text = ""
                        else: self.input_text += key
                        self.lcd.display_masked(self.input_text)
            time.sleep(0.01)

    def on_connect(self, client, userdata, flags, rc):
        print(f"MQTT Connected (rc: {rc})")
        client.subscribe("jjld/entrance/#")
        self.show_home()

    def on_message(self, client, userdata, message):
        topic = message.topic
        path = topic.split('/')
        payload = message.payload.decode("utf-8").strip()

        # 인증 결과 처리 (RESULT)
        if "RESULT" in topic:
            self.input_lock = True
            if payload == "OK":
                self.lcd.clear() # 모터 돌기 전 화면 정리
                Thread(target=self.door_sequence, daemon=True).start()
            else:
                # 실패 시
                time.sleep(1.2)
                self.lcd.display("ACCESS DENIED", "TRY AGAIN")
                Timer(2.0, self.show_home).start()
            return 

        # 3. 원격 제어 및 기타 명령 처리
        # 토픽 구조 jjld/entrance/101/dev_type/... 
        if len(path) < 4: 
            return

        target_dong = path[2]
        dev_type = path[3]

        if target_dong != self.MY_DONG: 
            return

        if dev_type == "cam":
            if payload == "start": self.start_streaming()
            elif payload == "stop": self.stop_streaming()
        elif dev_type == "door":
            if payload == "OPEN" and self.ser: 
                self.ser.write(b'OPEN\n')
            elif payload == "CLOSED" and self.ser: 
                self.ser.write(b'CLOSE\n')

    def start_streaming(self):
        if not self.is_streaming:
            MyCamera.is_running = True
            self.is_streaming = True
            Thread(target=self.send_camera_frame, daemon=True).start()

    def stop_streaming(self):
        self.is_streaming = False
        MyCamera.is_running = False

    def send_camera_frame(self):
        while self.is_streaming and MyCamera.is_running:
            frame = self.camera.getStreaming()
            if frame:
                topic = f"jjld/entrance/{self.MY_DONG}/cam/frame"
                publisher.single(topic, frame, hostname=self.BROKER_IP)
            time.sleep(0.05)

    def mymqtt_connect(self):
        Thread(target=self.serial_monitor, daemon=True).start()
        self.client.connect(self.BROKER_IP, 1883, 60)
        self.client.loop_forever()

if __name__ == "__main__":
    try:
        worker = MqttWorker()
        worker.mymqtt_connect()
    except KeyboardInterrupt:
        GPIO.cleanup()