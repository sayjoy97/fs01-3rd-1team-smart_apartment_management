import { useState } from "react";
import { changeStatusDoor } from "../../../api/entranceDoorAPI";

// 출입문 제어 모달 컴포넌트
export default function EntranceControlModal({ entrance, imageSrc, onConfirm, onClose, publish }) {
  const [loading, setLoading] = useState(false);

  const handleStatusChange = async () => {
    const action = entrance.status === "CLOSED" ? "열기" : "잠금";

    if (window.confirm(`${entrance.houseDong}동 문을 ${action} 하시겠습니까?`)) {
      const command = entrance.status === "CLOSED" ? "OPEN" : "CLOSED";
      const topic = `jjld/entrance/${entrance.houseDong}/door/control`;

      publish(topic, command);

      onConfirm(entrance.doorId, command);
    }
  };

  if (!entrance) return null;
  return (
    <div className="modal-bg" onClick={onClose}>
      <div className="modal">
        <h3>출입문 제어</h3>

        <p>{entrance.houseDong}동 공동현관문</p>
        <div className="cctv">
          {imageSrc ? (
            <img src={imageSrc} alt="camera" className="cctv-view" />
          ) : (
            <div className="loading-placeholder">
              {entrance.houseDong}동 cctv가 아직 설치중입니다.
            </div>
          )}
        </div>

        <button className="btn-door" onClick={handleStatusChange} disabled={loading}>
          {entrance.status === "CLOSED" ? "출입문 열기" : "출입문 닫기"}
        </button>

        <button className="btn" onClick={onClose}>
          닫기
        </button>
      </div>
    </div>
  );
}
