import RPi.GPIO as GPIO
import time

class MySensor:
    _is_setup = False
    
    def __init__(self, trig=23, echo=24):
        self.trig = trig
        self.echo = echo
        if not MySensor._is_setup:
            GPIO.setmode(GPIO.BCM)
            GPIO.setup(self.trig, GPIO.OUT)
            GPIO.setup(self.echo, GPIO.IN)
            MySensor._is_setup = True

    def get_distance(self):
        GPIO.output(self.trig, False)
        time.sleep(0.01)
        GPIO.output(self.trig, True)
        time.sleep(0.00001)
        GPIO.output(self.trig, False)
        
        ps, pe = time.time(), time.time()
        while GPIO.input(self.echo) == 0:
            ps = time.time()
        while GPIO.input(self.echo) == 1:
            pe = time.time()
            
        duration = pe - ps
        distance = duration * 17150
        return round(distance, 2)