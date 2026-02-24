import React, { useState, useEffect } from "react";
import "./FeeSettingModal.css";

export function FeeSettingModal({ isOpen, onClose, initialData, onSave }) {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    baseTime: 30,
    baseCharge: 1000,
    unitMinutes: 10, // API 규격에 있는 단위 시간
    unitCharge: 500, // API 규격에 있는 단위 요금
    peakEnabled: false,
    peakStartAt: "09:00",
    peakEndAt: "18:00",
    peakUnitMinutes: 30,
    peakUnitCharge: 1500,
  });

  // API 응답 데이터를 내부 상태로 매핑
  useEffect(() => {
    if (initialData) {
      setFormData({
        baseTime: initialData.baseTime || 0,
        baseCharge: initialData.baseCharge || 0,
        unitMinutes: initialData.unitMinutes || 0,
        unitCharge: initialData.unitCharge || 0,
        peakEnabled: initialData.peakEnabled || false,
        peakStartAt: initialData.peakStartAt || "09:00",
        peakEndAt: initialData.peakEndAt || "18:00",
        peakUnitMinutes: initialData.peakUnitMinutes || 0,
        peakUnitCharge: initialData.peakUnitCharge || 0,
      });
    }
    // 모달이 닫힐 때 수정 모드 초기화
    if (!isOpen) setIsEditing(false);
  }, [initialData, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "number" ? Number(value) : value,
    }));
  };

  const handleTogglePeak = () => {
    if (!isEditing) return; // 수정 버튼을 눌러야만 토글 가능
    setFormData((prev) => ({ ...prev, peakEnabled: !prev.peakEnabled }));
  };

  const handleAction = () => {
    if (!isEditing) {
      setIsEditing(true);
    } else {
      onSave(formData); // 부모 컴포넌트에서 chargeSettingUpdate 호출
      setIsEditing(false);
    }
  };

  return (
    <div className="fd-modal-overlay">
      <div className="fd-modal-content">
        <div className="fd-modal-header">
          <h2>주차 요금 설정</h2>
          <button className="fd-modal-close" onClick={onClose}>
            &times;
          </button>
        </div>
        <p className="fd-modal-sub">미등록 차량의 주차 요금을 설정합니다 (30분 이내는 무료)</p>

        <div className="fd-modal-section">
          <h3>기본 요금 설정</h3>
          <div className="fd-input-row">
            <div className="fd-input-group">
              <label>기본 시간 (분)</label>
              <input
                type="number"
                name="baseTime"
                value={formData.baseTime}
                onChange={handleChange}
                disabled={!isEditing}
              />
            </div>
            <div className="fd-input-group">
              <label>기본 요금 (원)</label>
              <input
                type="number"
                name="baseCharge"
                value={formData.baseCharge}
                onChange={handleChange}
                disabled={!isEditing}
              />
            </div>
          </div>
          <p className="fd-current-setting">
            현재 설정: {formData.baseTime}분 / {formData.baseCharge.toLocaleString()}원 (이후{" "}
            {formData.unitMinutes}분당 {formData.unitCharge.toLocaleString()}원)
          </p>
        </div>

        {/* 피크시간 섹션: 수정 모드일 때만 토글 활성화 */}
        <div className={`fd-modal-section peak-section ${formData.peakEnabled ? "active" : ""}`}>
          <div className="fd-toggle-row">
            <h3>피크시간 요금 부여</h3>
            <div
              className={`fd-switch ${formData.peakEnabled ? "on" : ""} ${!isEditing ? "disabled" : ""}`}
              onClick={handleTogglePeak}
            >
              <div className="fd-handle"></div>
            </div>
          </div>

          {/* 피크시간 활성화 + 수정 가능 시에만 입력창 제어 */}
          {formData.peakEnabled && (
            <div className="fd-peak-detail">
              <div className="fd-input-row">
                <div className="fd-input-group">
                  <label>시작 시간</label>
                  <input
                    type="time"
                    name="peakStartAt"
                    value={formData.peakStartAt || "09:00"}
                    onChange={handleChange}
                    disabled={!isEditing}
                  />
                </div>
                <div className="fd-input-group">
                  <label>종료 시간</label>
                  <input
                    type="time"
                    name="peakEndAt"
                    value={formData.peakEndAt || "18:00"}
                    onChange={handleChange}
                    disabled={!isEditing}
                  />
                </div>
              </div>
              <div className="fd-input-row">
                <div className="fd-input-group">
                  <label>단위 시간 (분)</label>
                  <input
                    type="number"
                    name="peakUnitMinutes"
                    value={formData.peakUnitMinutes}
                    onChange={handleChange}
                    disabled={!isEditing}
                  />
                </div>
                <div className="fd-input-group">
                  <label>단위 요금 (원)</label>
                  <input
                    type="number"
                    name="peakUnitCharge"
                    value={formData.peakUnitCharge}
                    onChange={handleChange}
                    disabled={!isEditing}
                  />
                </div>
              </div>
              <p className="fd-current-setting">
                피크 설정: {formData.peakStartAt} ~ {formData.peakEndAt}, {formData.peakUnitMinutes}
                분당 {formData.peakUnitCharge.toLocaleString()}원
              </p>
            </div>
          )}
        </div>

        <div className="fd-modal-footer">
          <button className="fd-btn-cancel" onClick={onClose}>
            취소
          </button>
          <button className="fd-btn-submit" onClick={handleAction}>
            {isEditing ? "저장" : "수정"}
          </button>
        </div>
      </div>
    </div>
  );
}
