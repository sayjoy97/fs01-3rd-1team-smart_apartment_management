from pirc522 import RFID
from threading import Thread
import time

class HouseRFID:
    def __init__(self):
        self.rfid = RFID(pin_irq=None)
        self.is_running = False 
        self.thread = None 
        
    def start(self, publish_func):
        if not self.is_running:
            self.is_running = True 
            self.thread = Thread(target=self._loop, args=(publish_func,), daemon=True)
            self.thread.start()
            print("RFID 시작")
            
    def stop(self):
        if self.is_running:
            self.is_running = False
            print("RFID 중지")
            
    def _loop(self, publish_func):
        while self.is_running:
            error, tag_type = self.rfid.request()
            if not error:
                error, uid = self.rfid.anticoll()
                if not error:
                    uid_str = "-".join([str(i) for i in uid])
                    print("카드 uid: ", uid_str)
                    print("카드 길이: ", len(uid_str))
                    publish_func(uid_str)
                    time.sleep(2)
            time.sleep(0.1)