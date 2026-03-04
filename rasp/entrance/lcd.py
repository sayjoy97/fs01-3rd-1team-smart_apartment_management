import time
import serial
from mylcd import MyLcd

class NovaTester:
    def __init__(self):
        self.lcd = MyLcd()
        self.input_buffer = ""
        self.is_processing = False  # 10초 대기 중 입력을 막기 위한 변수
        
        # 시리얼 포트 설정 (아두이노와 연결)
        try:
            self.ser = serial.Serial('/dev/ttyACM0', 9600, timeout=0.1)
            self.ser.reset_input_buffer()
            print("[성공] 아두이노와 시리얼 연결 완료")
        except Exception as e:
            print(f"[오류] 시리얼 연결 실패: {e}")
            self.ser = None

    def show_main(self):
        """기본 대기 화면"""
        self.lcd.display("Hello Nova 1", "Waiting...")

    def run(self):
        if not self.ser: return

        self.show_main()
        print("--- Nova 시스템 가동 중 ---")

        while True:
            if self.ser.in_waiting > 0:
                # 아두이노에서 보낸 "KEY:1" 형태의 문자열 읽기
                line = self.ser.readline().decode('utf-8', errors='ignore').strip()
                
                if line.startswith("KEY:") and not self.is_processing:
                    key = line.split(":")[1]
                    
                    if key == '*': # 리셋 키
                        self.input_buffer = ""
                        self.show_main()
                        continue

                    # 번호 입력 및 마스킹 표시
                    self.input_buffer += key
                    self.lcd.display("Input Nova:", "*" * len(self.input_buffer))
                    print(f"현재 입력값: {self.input_buffer}")

                    # 4자리가 입력되면 이벤트 발생
                    if len(self.input_buffer) >= 4:
                        self.is_processing = True
                        print("[이벤트] 인증 성공! 문을 엽니다.")
                        
                        # 1. 아두이노에 문 열기 명령 전송 (아두이노가 2.2초간 동작함)
                        self.ser.write(b'OPEN\n')
                        
                        # 2. LCD 메시지 전환
                        self.lcd.clear()
                        time.sleep(0.1)
                        self.lcd.display("Thank you Nova", "Good Luck")
                        
                        # 3. 요청하신 10초 대기 (이 기간 동안 문이 열린 상태 유지)
                        time.sleep(10)
                        
                        # 4. 10초 후 문 닫기 명령 전송 (아두이노가 2.0초간 동작함)
                        print("[시스템] 10초 경과, 문을 닫습니다.")
                        self.ser.write(b'CLOSE\n')
                        
                        # 5. 문이 완전히 닫힐 때까지 잠시 대기 (모터 노이즈 방지)
                        time.sleep(2.5)
                        
                        # 6. 초기화 및 홈 화면 복귀
                        self.input_buffer = ""
                        self.is_processing = False
                        self.ser.reset_input_buffer()
                        self.show_main()
                        print("[시스템] 대기 상태 복구")

            time.sleep(0.01)

if __name__ == "__main__":
    tester = NovaTester()
    try:
        tester.run()
    except KeyboardInterrupt:
        print("\n테스트 종료")