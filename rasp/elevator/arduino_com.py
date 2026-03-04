import serial
import time

class ArduinoCom:
    def __init__(self, port, baudrate):
        try:
            self.ser = serial.Serial(port, baudrate, timeout=1)
            time.sleep(2)
            self.ser.reset_input_buffer()
            print(f"✅ Arduino Connected: {port}")
        except Exception as e:
            print(f"❌ Arduino Connection Error: {e}")
            self.ser = None

    def send(self, message):
        if self.ser:
            self.ser.write(f"{message}\n".encode('utf-8'))
            return True
        return False

    def receive(self):
        if self.ser and self.ser.in_waiting > 0:
            return self.ser.readline().decode('utf-8').strip()
        return None