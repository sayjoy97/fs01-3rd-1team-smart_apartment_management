import json
import time
import serial
import paho.mqtt.client as mqtt

SERIAL_PORT = "/dev/tty.usbmodem1051DB3579FC2"
BAUD = 115200

MQTT_HOST = "localhost"
MQTT_PORT = 1883

TOPIC_CONTROL_SUB = "jjld/energy/+/control"
ACK_TOPIC_FMT = "jjld/energy/{}/CONTROL_ACK"

FAN_DEVICE_ID = 2
HEATER_DEVICE_ID = 3
RES_DEVICE_ID = 4

ser = None

def on_message(client, userdata, msg):
    """백엔드 -> 브릿지(control) -> 아두이노"""
    try:
        payload = msg.payload.decode(errors="ignore").strip()
        data = json.loads(payload)

        device_id = data.get("deviceId")
        operate = data.get("operate")

        if device_id is None or operate is None:
            print("Invalid control payload:", payload)
            return

        # deviceId 기준으로 어떤 장치인지 확정 (너 DB 기준)
        if int(device_id) == FAN_DEVICE_ID:
            cmd = "FAN_ON" if operate else "FAN_OFF"
        elif int(device_id) == HEATER_DEVICE_ID:
            cmd = "HEATER_ON" if operate else "HEATER_OFF"
        elif int(device_id) == RES_DEVICE_ID:
            cmd = "RES_ON" if operate else "RES_OFF"
        else:
            print("Unknown deviceId for control:", device_id)
            return

        print("CONTROL RECEIVED:", msg.topic, cmd)
        if ser:
            ser.write((cmd + "\n").encode())

    except Exception as e:
        print("CONTROL parse error:", e)


def main():
    global ser

    # 1) Serial open
    ser = serial.Serial(SERIAL_PORT, BAUD, timeout=1)

    # 2) MQTT connect + subscribe
    m = mqtt.Client(client_id="mac-arduino-bridge-energy")
    m.on_message = on_message
    m.connect(MQTT_HOST, MQTT_PORT, 60)
    m.subscribe(TOPIC_CONTROL_SUB)
    m.loop_start()

    # 3) Arduino -> MQTT measurement publish
    while True:
        line = ser.readline().decode(errors="ignore").strip()
        if not line:
            continue

        try:
            data = json.loads(line)
        except Exception:
            continue
        
        # ACK 처리 블록
        if data.get("type") == "ack":
            device_id = data.get("deviceId")
            operate = data.get("operate")

            if device_id is None or operate is None:
                print("Invalid ACK payload from Arduino:", data)
                continue

            ack_topic = ACK_TOPIC_FMT.format(int(device_id))

            ack_payload = {
                "deviceId": int(device_id),
                "operate": bool(operate),
                "success": True
            }

            m.publish(ack_topic, json.dumps(ack_payload), qos=0, retain=False)
            print("ACK PUB:", ack_topic, ack_payload)
            continue

        # 🔥 1️⃣ 어떤 장치 측정인지 확인
        measure_id = data.get("measureDeviceId")

        if measure_id not in [FAN_DEVICE_ID, RES_DEVICE_ID]:
            print("Invalid measureDeviceId:", measure_id)
            continue

        # 🔥 2️⃣ 토픽 결정
        topic = f"jjld/energy/{measure_id}/measurement"

        # 🔥 3️⃣ 백엔드 DTO와 맞는 payload 구성
        payload = {
            "voltage": data.get("voltage"),
            "current": data.get("current"),
            "power": data.get("power"),
            "energyKwh": data.get("energyKwh")
        }

        m.publish(topic, json.dumps(payload), qos=0, retain=False)
        print("PUB:", topic, payload)

        time.sleep(5.0)


if __name__ == "__main__":
    main()