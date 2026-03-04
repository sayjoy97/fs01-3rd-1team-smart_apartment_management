import time
from RPLCD.i2c import CharLCD

class MyLcd:
    _device = None

    def __init__(self):
        if MyLcd._device is None:
            MyLcd._device = CharLCD(i2c_expander='PCF8574', address=0x27, port=1, cols=16, rows=2)
            time.sleep(0.1)
        self.lcd = MyLcd._device

    def display(self, line1, line2=""):
        try:
            time.sleep(0.05)
            
            # LCD 초기화
            self.lcd.clear()
            time.sleep(0.02)
            
            l1 = line1[:16].ljust(16)
            l2 = line2[:16].ljust(16)
            
            # 첫 번째 줄 출력
            self.lcd.cursor_pos = (0, 0)
            self.lcd.write_string(l1)
            
            # 데이터 전송 간격 확보 
            time.sleep(0.02) 
            
            # 두 번째 줄 출력
            self.lcd.cursor_pos = (1, 0)
            self.lcd.write_string(l2)
            
        except Exception as e:
            print(f"LCD 에러 발생: {e}")
            self._device = None 
            self.__init__()

    def display_masked(self, text):
        """비밀번호 마스킹 출력"""
        if '#' in text:
            parts = text.split('#', 1)
            masked = parts[0] + "#" + ("*" * len(parts[1]))
        else:
            masked = text
        self.display("Input:", masked)

    def clear(self):
        self.lcd.clear()