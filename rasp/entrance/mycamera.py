import threading
import time
import io
import base64
from picamera2 import Picamera2

class MyCamera:
    frame = None
    thread = None
    is_running = False
    _device = None 

    def __init__(self):
        if MyCamera._device is None:
            MyCamera._device = Picamera2()
          

    def getStreaming(self):
        if not MyCamera.is_running:
            return None

        if MyCamera.thread is None or not MyCamera.thread.is_alive():
            MyCamera.is_running = True
            MyCamera.frame = None
            MyCamera.thread = threading.Thread(target=self.streaming)
            MyCamera.thread.daemon = True
            MyCamera.thread.start()

            start_time = time.time()
            while MyCamera.frame is None and MyCamera.is_running:
                time.sleep(0.1)
                if time.time() - start_time > 5:
                    break
        
        return MyCamera.frame

    @classmethod
    def streaming(cls):
        device = cls._device 
        try:
            config = device.create_video_configuration(main={"format":"RGB888", "size":(320, 240)})
            device.configure(config)
            device.start()

            stream = io.BytesIO()
            while cls.is_running:
                device.capture_file(stream, format="jpeg")
                stream.seek(0)
                cls.frame = base64.b64encode(stream.read()).decode("utf-8")
                
                stream.seek(0)
                stream.truncate()
                time.sleep(0.05)
                
        except Exception as e:
            print(f"Streaming Error: {e}")
        finally:
            try:
                device.stop() 
            except:
                pass
            cls.thread = None
            cls.frame = None