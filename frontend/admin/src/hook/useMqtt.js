import mqtt from "mqtt";
import { useCallback, useEffect, useState } from "react";

const TOPIC_NAME = "jjld/entrance/door/#";

const useMqtt = (brokerUrl) => {
  const [connectStatus, setConnectStatus] = useState("connecting");
  const [client, setClient] = useState(null);
  const [rfidUid, setRfidUid] = useState("");

  // 실시간 cctv 스트리밍
  const [imageSrc, setImageState] = useState("");

  useEffect(() => {
    const mqttClient = mqtt.connect(brokerUrl, {
      clientId: `react_client_${Math.random().toString(16).substring(2, 8)}`,
      keepalive: 60,
      protocolId: "MQTT",
      clean: true,
      reconnectPeriod: 1000,
      connectTimeout: 30 * 1000,
    });

    // 연결 성공
    mqttClient.on("connect", () => {
      setConnectStatus("connected");
      mqttClient.subscribe("jjld/entrance/#");
      mqttClient.subscribe("jjld/house/#");
    });

    mqttClient.on("message", (topic, message) => {
      // jjld/entrance/101/cam/frame
      const msg = message.toString();
      const path = topic.split("/");

      const location = path[1];
      const unit = path[2];
      const deviceType = path[3];

      if (deviceType === "cam" && path[4] === "frame") {
        setImageState(`data:image/jpeg;base64,${msg}`);
      }

      if (deviceType === "door") {
        console.log(`${unit}동 도어 상태: `, msg);
      }

      if (deviceType === "card" && path[4] === "result") {
        console.log("통과");
        setRfidUid(msg);
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
        mqttClient.end(true);
        setConnectStatus("connecting");
      }
    };
  }, [brokerUrl]);

  // publish 함수
  const publish = useCallback(
    (topic, message) => {
      if (client) {
        console.log("브로커로 mqtt통신 함");

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
    rfidUid,
    setImageState,
    publish,
  };
};

export default useMqtt;
