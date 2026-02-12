import mqtt from "mqtt";
import { useCallback, useEffect, useState } from "react";

const BROKER_URL = "ws://192.168.14.26:9001";

const useMqtt = () => {
  const [connectStatus, setConnectStatus] = useState("connecting");
  const [client, setClient] = useState(null);

  // 실시간 cctv 스트리밍
  const [imageSrc, setImageState] = useState("");

  useEffect(() => {
    const mqttClient = mqtt.connect(BROKER_URL, {
      clientId: `react_client_${Math.random().toString(16).substring(2, 8)}`,
      keepalive: 60,
      protocolld: "MQTT",
      clean: true,
      reconnectPeriod: 1000,
      connectTimeout: 30 * 1000,
    });

    // 연결 성공
    mqttClient.on("connect", () => {
      setConnectStatus("connected");
    });

    mqttClient.on("message", (topic, message) => {
      const payload = message.toString();

      if (topic === "jjld/entrance/door/gate_command/cam") {
        setImageState(`data:image/jpeg;base64,${payload}`);
        return;
      }
    });

    // 에러 처리
    mqttClient.on("error", (err) => {
      mqttClient.end();
    });

    setClient(mqttClient);

    // 페이지 이탈 시 모든 카메라 정리
    return () => {
      if (mqttClient) {
        mqttClient.publish("jjld/entrance/door/gate_command/cam", "stop");

        mqttClient.end();
        setConnectStatus("connecting");
      }
    };
  }, []);

  // publish 함수
  const publish = useCallback(
    (topic, message) => {
      if (client) {
        client.publish(topic, message);
      } else {
        console.error("Mqtt전송실패");
      }
    },
    [client],
  );

  return {
    connectStatus,
    imageSrc,
    publish,
  };
};

export default useMqtt;
