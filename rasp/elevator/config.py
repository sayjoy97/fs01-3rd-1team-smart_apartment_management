# 하드웨어 및 통신 설정
SERIAL_PORT = '/dev/ttyACM0'
MQTT_BROKER = "URL"
MY_DONG = "101"
MY_HOGI = "1"

# 토픽 설정
TOPIC_SUB = f"jjld/command/elevator/#"
TOPIC_PUB_STATUS = f"jjld/elevator/{MY_DONG}/{MY_HOGI}/status"
TOPIC_PUB_EVENT = f"jjld/elevator/{MY_DONG}/{MY_HOGI}/event"