import RPi.GPIO as GPIO
import time

class MotorController:
    def __init__(self, motor_pins, total_steps, step_delay):
        self.motor_pins = motor_pins
        # 28BYJ-48 스텝 순서 (8단계 모드)
        self.step_seq = [
            [1,0,0,1], [1,0,0,0], [1,1,0,0], [0,1,0,0],
            [0,1,1,0], [0,0,1,0], [0,0,1,1], [0,0,0,1]
        ]
        GPIO.setmode(GPIO.BCM)
        for pin in motor_pins:
            GPIO.setup(pin, GPIO.OUT)
            GPIO.output(pin, False)
            
        self.pins = motor_pins
        self.total_steps = total_steps
        self.step_delay = step_delay
        
    def move(self, type, direction):
        # direction: 1 (닫기/시계방향), -1 (열기/반시계방향)
        # direction: 1 (하강/시계방향), -1 (상승/반시계방향)
        if type == "DOOR":
            print(f"🚪 문 {'여는 중...' if direction == -1 else '닫는 중...'}")
        elif type == "ELEVATOR":
            print(f"📍 엘리베이터 {'상승 중...' if direction == 1 else '하강 중...'}")
        
        for _ in range(self.total_steps):
            for step in range(8):
                for pin in range(4):
                    # 방향에 따라 시퀀스 인덱스 결정
                    idx = step if direction == 1 else 7 - step
                    GPIO.output(self.pins[pin], self.step_seq[idx][pin])
                time.sleep(self.step_delay)
        
        # 동작 완료 후 모터 전원 차단 (열 발생 방지)
        self.stop()

    def stop(self):
        for pin in self.pins:
            GPIO.output(pin, False)