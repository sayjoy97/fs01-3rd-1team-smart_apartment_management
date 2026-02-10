import React, { useEffect, useState } from "react";

const HouseholdDetailModal = ({ data, onSave, onClose }) => {
  if (!data) return null;

  console.log("세대 상세: ", data);

  const [formData, setFormData] = useState({
    householderName: data.householderName || "",
    householderPhone: data.householderPhone || "",
    householderEmail: data.householderEmail || "",
    entrancePass: data.entrancePass || "",
    moveInAt: data.moveInAt || "",
    householdSize: 0,
    cardUid: [],
  });

  const handleSaveClick = () => {
    onSave(data.houseId, formData);
  };

  // input 변경
  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      {/* 모달 박스 */}
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <h3>세대 정보 관리</h3>
        <p>
          {data.houseDong}동 {data.houseHo}호
        </p>
        <p>
          세대주 이름
          <input
            name="householderName"
            placeholder="세대주 이름 입력"
            value={formData.householderName}
            onChange={handleChange}
          />
        </p>
        <p>
          연락처
          <input
            name="householderPhone"
            placeholder="연락처 입력 (-) 없이"
            value={formData.householderPhone}
            onChange={handleChange}
          />
        </p>
        <p>
          공동현관 비밀번호
          <span style={{ fontSize: "12px" }}>* 공동현관 출입을 위한 비밀번호입니다</span>
          <input
            name="entrancePass"
            placeholder="4자리 숫자 입력 (예: 1234)"
            value={formData.entrancePass}
            onChange={handleChange}
          />
        </p>
        <p>
          입주일
          <input
            name="moveInAt"
            placeholder="연도-월-일"
            value={formData.moveInAt}
            onChange={handleChange}
          />
        </p>
        <p>
          세대원 수
          <input
            name="householdSize"
            placeholder="세대원 수를 입력"
            value={formData.householdSize}
            onChange={handleChange}
          />
        </p>
        <p>
          RFID 출입카드
          <input
            name="cardUid"
            placeholder="카드 리더기"
            value={formData.cardUid}
            onChange={handleChange}
          />
        </p>
        <button onClick={onClose}>닫기</button>
        <button onClick={onSave}>저장</button>
      </div>
    </div>
  );
};

export default HouseholdDetailModal;
