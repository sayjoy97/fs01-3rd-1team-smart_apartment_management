import json
import time
import serial
import requests
import paho.mqtt.client as mqtt   # ← 이 줄 반드시 있어야 함

SERIAL_PORT = "/dev/tty.usbmodem1051DB3745982"  # 너의 noise 아두이노 포트로 바꿔
BAUD = 115200

MQTT_HOST = "localhost"
MQTT_PORT = 1883

TOPIC_FMT = "jjld/noise/{}/event"   # sensorId 들어갈 자리

def main():
    ser = serial.Serial(SERIAL_PORT, BAUD, timeout=1)

    m = mqtt.Client(client_id="mac-arduino-bridge-noise")
    m.connect(MQTT_HOST, MQTT_PORT, 60)
    m.loop_start()

    print("Noise MQTT bridge started:", SERIAL_PORT)

    while True:
        line = ser.readline().decode(errors="ignore").strip()
        if not line:
            continue

        try:
            data = json.loads(line)
        except Exception:
            continue

        if data.get("type") != "noise":
            continue

        sensor_id = data.get("sensorId")
        level = data.get("soundLevel")

        if sensor_id is None or level is None:
            continue

        topic = TOPIC_FMT.format(int(sensor_id))
        payload = {"soundLevel": int(level)}

        m.publish(topic, json.dumps(payload), qos=0, retain=False)
        print("PUB:", topic, payload)

        time.sleep(0.05)

if __name__ == "__main__":
    main()