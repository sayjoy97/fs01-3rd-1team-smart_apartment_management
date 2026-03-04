import mymqtt
import time
import RPi.GPIO as GPIO

if __name__ == "__main__":
    worker = None
    try:
        # 1. 클래스 생성 및 실행
        worker = mymqtt.MqttWorker()
        worker.mymqtt_connect()
        
        print("🚀 엘리베이터 시스템 가동 중... (종료하려면 Ctrl+C)")
        
        # 2. 메인 쓰레드가 죽지 않게 무한 루프로 대기
        while True:
            time.sleep(1) # CPU 점유율을 낮추기 위해 1초씩 쉬어줌
            
    except KeyboardInterrupt:
        print("\n정지 명령 수신")
    
    except Exception as e:
        print(f"오류 발생: {e}")
    
    finally:
        # 3. 종료 시 처리
        if worker:
            worker.running = False
        print("리소스 정리 중...")
        GPIO.cleanup()
        print("종료 완료")