import React, { useEffect, useState } from "react";
import useMqtt from "../../../hook/useMqtt";

// 출입문 제어 모달 컴포넌트
export default function EntranceControlModal({ entrance, imageSrc, onConfirm, onClose }) {
  const { connectStatus, publish } = useMqtt();

  useEffect(() => {
    if (connectStatus === "connected") {
      publish("jjld/entrance/door/gate_command/cam", "start");
    }
    return () => {
      if (connectStatus === "connected") {
        publish("jjld/entrance/door/gate_command/cam", "stop");
      }
    };
  }, [connectStatus, publish]);

  if (!entrance) return null;
  return (
    <div className="modal-bg">
      <div className="modal">
        <h3>출입문 제어</h3>

        <p>
          {entrance.building} {entrance.location}
        </p>
        <div className="cctv">
          <img src={imageSrc || null} alt="camera" className="cctv-view" />
        </div>

        <button
          className="btn danger"
          onClick={() => {
            publish("jjld/entrance/door/gate_command/gate", `${entrance.dong}-open`);

            onConfirm(entrance.id);
          }}
        >
          상태변경
        </button>

        <button className="btn" onClick={onClose}>
          취소
        </button>
      </div>
    </div>
  );
}
