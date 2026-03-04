import serial
import paho.mqtt.client as mqtt
import time
import json
import heapq
import config
from threading import Thread, Event
from motor_control import MotorController

class MqttWorker:
    def __init__(self):
        # 1. 상태 및 제어 변수 초기화
        self.current_floor = 1
        self.direction = "STAY"
        self.door_status = "CLOSED"
        self.system_status = "IDLE"
        self.current_target = None
        
        self.up_heap = []
        self.down_heap = []
        self.running = True
        
        # 2. 제어 객체
        self.door = MotorController([17, 18, 27, 22], 1500, 0.0012)
        self.lift = MotorController([23, 24, 25, 8], 320, 0.0012)
        self.stop_wait_event = Event()

        # 3. MQTT 및 시리얼 설정
        self.client = mqtt.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        
        try:
            self.ser = serial.Serial(config.SERIAL_PORT, 9600, timeout=1)
            
            self.ser.reset_input_buffer()
            self.ser.reset_output_buffer()
            time.sleep(2) # 아두이노 초기화 대기
            self.send_to_arduino(f"RESET:A:0")
            self.send_to_arduino(f"S:{self.system_status}:0")
            time.sleep(1)
            self.send_to_arduino(f"{self.current_floor}:{self.direction}:{self.door_status}")
            print("✅ 시리얼 연결 성공")
        except Exception as e:
            print(f"❌ 시리얼 연결 실패: {e}")
            self.ser = None

    def on_connect(self, client, userdata, flags, rc):
        print(f"🌐 MQTT 연결 성공 (코드: {rc})")
        client.subscribe(config.TOPIC_SUB)

    def on_message(self, client, userdata, msg):
        """외부 명령 수신: ERROR/IDLE 상태 변경 또는 원격 호출"""
        try:
            data = json.loads(msg.payload.decode())
            
            path = msg.topic.split('/') 
            type = path[5] if len(path) > 5 else "unknown"
            print(f"📥 [MQTT Command] {data}")

            # A. 시스템 상태 제어
            if type == "status":
                new_status = data["status"] if isinstance(data, dict) else data
                if new_status in ["ERROR", "REPAIR", "IDLE"]:
                    self.handle_system_state(new_status)
                return

            # B. 이동 명령 (IDLE 시)
            if self.system_status == "IDLE":
                floor = data.get('floor')
                btn_status = data.get('status')
                if floor is not None:
                    self.process_command(int(floor), str(btn_status))
        except Exception as e:
            print(f"⚠️ MQTT 메시지 처리 에러: {e}")

    def handle_system_state(self, new_state):
        self.system_status = new_state
        if self.system_status in ["ERROR", "REPAIR"]:
            print(f"🚨 시스템 상태 변경: {self.system_status}")
            self.up_heap, self.down_heap = [], []
            self.direction = "STAY"
            self.send_to_arduino(f"S:{self.system_status}:0")
        else:
            print("✅ 정상 모드 복구")
            self.send_to_arduino(f"S:{self.system_status}:0")
            time.sleep(1)
            self.send_to_arduino(f"{self.current_floor}:STAY:CLOSE")

    # 명령 처리: 힙에 추가/제거 및 현재 층이면 문 제어
    def process_command(self, target_floor, status):
        if status == '1':
            if target_floor > self.current_floor:
                if target_floor not in self.up_heap: heapq.heappush(self.up_heap, target_floor)
            elif target_floor < self.current_floor:
                if -target_floor not in self.down_heap: heapq.heappush(self.down_heap, -target_floor)
            else:
                self.door_process() # 현재 층이면 문 열기
                return
            print(f"📥 호출 추가: {target_floor}F")
        elif status == '0':
            print(f"❌ 호출 취소: {target_floor}F")

            if target_floor in self.up_heap:
                self.up_heap.remove(target_floor)
                heapq.heapify(self.up_heap)

            if -target_floor in self.down_heap:
                self.down_heap.remove(-target_floor)
                heapq.heapify(self.down_heap)

            if self.current_target == target_floor:
                print("🚫 현재 이동 목적지 취소됨")
                self.current_target = None
        payload = f"MOVE_START:{target_floor}:{target_floor}층에서 호출" + ("했습니다." if status == '1' else "을 취소했습니다.")
        self.client.publish(config.TOPIC_PUB_EVENT, payload)
    
    def get_next_target(self):
        if self.direction == "UP":
            if self.up_heap:
                return heapq.heappop(self.up_heap)
            elif self.down_heap:
                self.direction = "DOWN"
                return -heapq.heappop(self.down_heap)
        elif self.direction == "DOWN":
            if self.down_heap:
                return -heapq.heappop(self.down_heap)
            elif self.up_heap:
                self.direction = "UP"
                return heapq.heappop(self.up_heap)
        else:  # STAY
            if self.up_heap:
                self.direction = "UP"
                return heapq.heappop(self.up_heap)
            elif self.down_heap:
                self.direction = "DOWN"
                return -heapq.heappop(self.down_heap)

        return None

    def remove_from_heap(self, floor):
        if floor in self.up_heap:
            self.up_heap.remove(floor)
            heapq.heapify(self.up_heap)
        if -floor in self.down_heap:
            self.down_heap.remove(-floor)
            heapq.heapify(self.down_heap)

    def send_to_arduino(self, msg):
        if self.ser:
            self.ser.write(f"{msg}\n".encode())

    # 문 제어 시퀀스 (실시간 취소 반영)
    def door_process(self):
        self.door_status = "OPENING"
        self.send_to_arduino(f"{self.current_floor}:{self.direction}:OPEN")
        
        payload = f"DOOR_OPEN:{self.current_floor}:{self.current_floor}층에서 문이 열립니다."
        self.client.publish(config.TOPIC_PUB_EVENT, payload)
        
        self.door.move("DOOR", -1) # 문 열기
        # time.sleep(0.5)
        
        self.door_status = "OPEN"
        # wait()는 set()이 호출되면 즉시 True를 반환, 아니면 timeout까지 대기
        is_interrupted = self.stop_wait_event.wait(timeout=3.0) 
        
        self.door_status = "CLOSING"
        self.send_to_arduino(f"{self.current_floor}:{self.direction}:CLOSE")
        payload = f"DOOR_CLOSE:{self.current_floor}:{self.current_floor}층에서 문이 닫힙니다."
        self.client.publish(config.TOPIC_PUB_EVENT, payload)
        
        self.door.move("DOOR", 1) # 문 닫기
        # time.sleep(0.5)
        
        self.door_status = "CLOSED"
        self.stop_wait_event.clear()

    def move_elevator(self, target):

        self.current_target = target

        print(f"🚩 목적지 {target}F 이동 시작")

        while self.current_floor != target:
            if self.system_status != "IDLE":
                print("🛑 시스템 중단")
                self.current_target = None
                return
            if self.current_target is None:
                print("🚫 목적지 취소됨")
                return

            step = 1 if target > self.current_floor else -1

            self.direction = "UP" if step == 1 else "DOWN"

            self.send_to_arduino(
                f"{self.current_floor}:{self.direction}:MOVING"
            )

            print(f"📍 이동중: {self.current_floor}F → {self.current_floor+step}F")

            self.lift.move("ELEVATOR", step)

            self.current_floor += step

            # 이동 중 새로운 목적지 확인
            next_target = self.get_next_target_in_path()

            if next_target is not None:
                print(f"🔄 경로 중 새로운 목적지 발견: {next_target}F")
                # 기존 target 다시 heap에 넣기
                if target > self.current_floor:
                    heapq.heappush(self.up_heap, target)
                elif target < self.current_floor:
                    heapq.heappush(self.down_heap, -target)

                target = next_target
                self.current_target = target

        # 도착 처리
        print(f"✅ {self.current_floor}F 도착")

        self.current_target = None

        self.direction = "STAY"

        payload = f"ARRIVE:{self.current_floor}:{self.current_floor}층 도착"
        self.client.publish(config.TOPIC_PUB_EVENT, payload)

        self.send_to_arduino(
            f"{self.current_floor}:STAY:OPEN"
        )

        self.door_process()

        self.send_to_arduino(f"RESET:C:{self.current_floor}")
        self.send_to_arduino(f"RESET:F:{self.current_floor}")
    
    def get_next_target_in_path(self):
        if self.direction == "UP":
            if self.up_heap and self.up_heap[0] <= self.current_target:
                return heapq.heappop(self.up_heap)
        elif self.direction == "DOWN":
            if self.down_heap and -self.down_heap[0] >= self.current_target:
                return -heapq.heappop(self.down_heap)

        return None

    # 메인 스케줄러 루프
    def elevator_motor_loop(self):
        while self.running:
            if self.system_status != "IDLE":
                time.sleep(0.5)
                continue
            if self.current_target is None:
                next_target = self.get_next_target()
                if next_target is not None:
                    payload = (
                        f"MOVE_START:{next_target}:{next_target}층 이동 시작"
                    )
                    self.client.publish(
                        config.TOPIC_PUB_EVENT,
                        payload
                    )
                    self.move_elevator(next_target)

            time.sleep(0.1)

    # 클라이언트 연결 및 쓰레드 가동
    def mymqtt_connect(self):
        self.client.connect(config.MQTT_BROKER, 1883, 60)
        self.client.loop_start()
        
        # 이동 로직과 시리얼 수신을 별도 쓰레드로 가동
        Thread(target=self.elevator_motor_loop, daemon=True).start()
        Thread(target=self.serial_receive_loop, daemon=True).start()

    # 아두이노 신호 수신 루프
    def serial_receive_loop(self):

        print("🚀 시리얼 수신 쓰레드 시작")

        start_time = time.time()

        while self.running:
            if not self.ser or not self.ser.is_open:
                time.sleep(1)
                continue
            try:
                # 초기 2초 garbage 제거
                if time.time() - start_time < 2:
                    self.ser.reset_input_buffer()
                    time.sleep(0.01)
                    continue
                if self.ser.in_waiting > 0:
                    line = self.ser.readline().decode(errors='ignore').strip()
                    if not line:
                        continue
                    # 유효한 데이터만 처리
                    if not line.startswith(("C:", "F:")):
                        continue
                    print(f"📡 시리얼 수신: {line}")
                    self.handle_arduino_data(line)
            except Exception as e:
                print("Serial error:", e)

            time.sleep(0.01)

    # 아두이노 데이터 분석 및 처리
    def handle_arduino_data(self, line):
        try:
            parts = line.split(':')
            if len(parts) < 3: return
            msg_type = parts[0].strip()
            val = int(parts[1].strip())
            status = parts[2].strip()
            
            target_floor = -1
            if msg_type == 'C':
                mapping = {0:1, 1:2, 2:3, 3:2, 4:3, 5:4}
                target_floor = mapping.get(val, -1)
            elif msg_type == 'F':
                target_floor = val
                print(f"📡 IR 리모컨 수신: {target_floor}층, 상태: {status}")
            
            if self.system_status == "IDLE":
                self.process_command(target_floor, status)
            
            # MQTT로 상태 보고
            payload = f"{self.current_floor}:{self.direction}:{self.door_status}:{self.system_status}"
            self.client.publish(config.TOPIC_PUB_STATUS, payload)
        except Exception as e:
            print(f"⚠️ 시리얼 데이터 파싱 에러: {e} (원본: {line})")