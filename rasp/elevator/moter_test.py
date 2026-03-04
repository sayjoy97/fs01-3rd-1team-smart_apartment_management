import RPi.GPIO as GPIO
import time

# 모터 미세 조절 용 파일
motor_pins = [23, 24, 25, 8]

GPIO.setmode(GPIO.BCM)

for pin in motor_pins:
    GPIO.setup(pin, GPIO.OUT)
    GPIO.output(pin, False)

# 28BYJ-48 스텝 모터의 8단계 제어 시퀀스
seq = [
    [1,0,0,1],
    [1,0,0,0],
    [1,1,0,0],
    [0,1,0,0],
    [0,1,1,0],
    [0,0,1,0],
    [0,0,1,1],
    [0,0,0,1]
]

step_count = len(seq)

def move_steps(steps, delay=0.0012):
    """
    steps: 움직일 스텝 수 (양수면 정방향, 음수면 역방향)
    delay: 속도 조절 (너무 빠르면 모터가 헛돌아요)
    """
    direction = 1 if steps > 0 else -1
    steps = abs(steps)
    
    for i in range(steps):
        for step in range(step_count):
            # 정방향 혹은 역방향 시퀀스 적용
            actual_step = seq[step] if direction == 1 else seq[step_count-1-step]
            for pin in range(4):
                GPIO.output(motor_pins[pin], actual_step[pin])
            time.sleep(delay)

try:
    print("🚀 모터 테스트 시작")
    step = -40
    move_steps(step)

except KeyboardInterrupt:
    print("🛑 테스트 중단")

finally:
    # 핀 초기화 (모터 전원 차단)
    GPIO.cleanup()
    print("✅ 테스트 종료 및 GPIO 정리 완료")