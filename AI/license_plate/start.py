import mymqtt

if __name__ == "__main__":
    try:
        mqtt = mymqtt.MqttWorker()
        mqtt.mqtt_connect()
        print("MQTT connected")

    except KeyboardInterrupt:
        pass

    finally:
        print("종료")