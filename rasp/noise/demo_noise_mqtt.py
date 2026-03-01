# send_sw420_vibration.py
import json
import time
import paho.mqtt.client as mqtt

MQTT_HOST = "localhost"
MQTT_PORT = 1883
SENSOR_ID = 3  # SW_420
TOPIC = f"jjld/noise/{SENSOR_ID}/event"

def main():
    m = mqtt.Client(client_id="demo-sw420-vibration")
    m.connect(MQTT_HOST, MQTT_PORT, 60)
    m.loop_start()

    # "진동"은 소리 크게 안 잡히는 상황 가정: 72dB (정책 넘기기용 최소치)
    # 만약 서버가 무조건 soundLevel만 보고 IMPACT면, 이 값도 IMPACT로 찍힐 수 있음.
    payload = {"soundLevel": 72}

    m.publish(TOPIC, json.dumps(payload), qos=0, retain=False)
    print("PUB:", TOPIC, payload)

    time.sleep(0.3)
    m.loop_stop()
    m.disconnect()

if __name__ == "__main__":
    main()