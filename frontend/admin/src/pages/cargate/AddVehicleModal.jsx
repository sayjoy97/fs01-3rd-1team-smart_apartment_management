import React, { useState } from "react";
import { carRegister } from "../../api/cargateAPI";
import "./AddVehicleModal.css";

export function AddVehicleModal({ isOpen, onClose, onSuccess }) {
  const [plateNumber, setPlateNumber] = useState("");
  const [vehicleType, setVehicleType] = useState("REGISTERED");

  const [houseInfo, setHouseInfo] = useState("");
  const [vehicleOwner, setVehicleOwner] = useState("");

  const [approvalReason, setApprovalReason] = useState("");
  const [startAt, setStartAt] = useState("");
  const [endAt, setEndAt] = useState("");

  const [loading, setLoading] = useState(false);

  const handleAdd = async () => {
    if (!plateNumber) return;

    const payload = {
      plateNumber,
      vehicleType,
      houseInfo: vehicleType === "REGISTERED" ? houseInfo : "",
      vehicleOwner: vehicleType === "REGISTERED" ? vehicleOwner : "",
      approvalReason: vehicleType === "ADMIN_APPROVED" ? approvalReason : "",
      startAt: vehicleType === "ADMIN_APPROVED" ? startAt : "",
      endAt: vehicleType === "ADMIN_APPROVED" ? endAt : "",
    };

    try {
      setLoading(true);
      await carRegister(payload);
      alert("차량 등록 완료");
      onSuccess?.();

      // 상태 초기화
      setPlateNumber("");
      setVehicleType("REGISTERED");
      setHouseInfo("");
      setVehicleOwner("");
      setApprovalReason("");
      setStartAt("");
      setEndAt("");
      onClose();
    } catch (err) {
      console.error(err);
      alert("등록 실패");
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="avm-overlay" onClick={onClose}>
      <div className="avm-modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="avm-modal-header">
          <div className="avm-header-text">
            <h2>방문차량 등록</h2>
            <p>새로운 방문차량을 등록합니다</p>
          </div>
          <button className="avm-close-x" onClick={onClose}>
            &times;
          </button>
        </div>

        <div className="avm-modal-body">
          <div className="avm-input-group">
            <label>차량번호</label>
            <input
              type="text"
              placeholder="예: 12가3456"
              value={plateNumber}
              onChange={(e) => setPlateNumber(e.target.value)}
              className="avm-input"
            />
          </div>

          <div className="avm-input-group">
            <label>차량유형</label>
            <select
              value={vehicleType}
              onChange={(e) => setVehicleType(e.target.value)}
              className="avm-select"
            >
              <option value="REGISTERED">세대 차량</option>
              <option value="ADMIN_APPROVED">관리자 승인 차량</option>
            </select>
          </div>

          {vehicleType === "REGISTERED" && (
            <>
              <div className="avm-input-group">
                <label>세대정보 (동호수)</label>
                <input
                  type="text"
                  placeholder="101동 301호"
                  value={houseInfo}
                  onChange={(e) => setHouseInfo(e.target.value)}
                  className="avm-input"
                />
              </div>
              <div className="avm-input-group">
                <label>차량 소유자</label>
                <input
                  type="text"
                  placeholder="홍길동"
                  value={vehicleOwner}
                  onChange={(e) => setVehicleOwner(e.target.value)}
                  className="avm-input"
                />
              </div>
            </>
          )}

          {vehicleType === "ADMIN_APPROVED" && (
            <>
              <div className="avm-input-group">
                <label>승인 사유</label>
                <input
                  type="text"
                  placeholder="사유를 입력하세요"
                  value={approvalReason}
                  onChange={(e) => setApprovalReason(e.target.value)}
                  className="avm-input"
                />
              </div>
              <div className="avm-date-row">
                <div className="avm-input-group">
                  <label>시작일</label>
                  <input
                    type="date"
                    value={startAt}
                    onChange={(e) => setStartAt(e.target.value)}
                    className="avm-input"
                  />
                </div>
                <div className="avm-input-group">
                  <label>종료일</label>
                  <input
                    type="date"
                    value={endAt}
                    onChange={(e) => setEndAt(e.target.value)}
                    className="avm-input"
                  />
                </div>
              </div>
            </>
          )}
        </div>

        <div className="avm-modal-footer">
          <button onClick={onClose} className="avm-btn-cancel">
            취소
          </button>
          <button onClick={handleAdd} disabled={loading} className="avm-btn-submit">
            {loading ? "등록 중..." : "등록하기"}
          </button>
        </div>
      </div>
    </div>
  );
}
