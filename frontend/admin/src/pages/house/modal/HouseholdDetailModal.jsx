import React, { useEffect, useState } from "react";
import "./HouseholdDetailModal.css";

const HouseholdDetailModal = ({ data, onSave, rfidUid, onClose, errorMsg }) => {
  const [cardInput, setCardInput] = useState("");

  const [formData, setFormData] = useState({
    houseId: data.houseId,
    householderName: data.householderName || "",
    householderPhone: data.householderPhone || "",
    householderEmail: data.householderEmail || "",
    entrancePass: data.entrancePass || "",
    moveInAt: data.moveInAt || "",
    householdSize: data.householdSize || 0,
    cardUid: data.cardUid || [],
  });

  // 연락처 자동 하이픈
  const handlePhoneChange = (e) => {
    let value = e.target.value.replace(/\D/g, "");
    if (value.length > 3 && value.length <= 7) {
      value = value.replace(/(\d{3})(\d+)/, "$1-$2");
    } else if (value.length > 7) {
      value = value.replace(/(\d{3})(\d{4})(\d+)/, "$1-$2-$3");
    }
    setFormData((prev) => ({ ...prev, householderPhone: value }));
  };

  // 카드 태그 등록
  useEffect(() => {
    if (rfidUid) {
      setCardInput(rfidUid);
    }
  }, [rfidUid]);

  //저장
  const handleSaveClick = () => {
    if (formData.householderEmail) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(formData.householderEmail)) {
        alert("이메일 형식이 올바르지 않습니다");
        return;
      }
    }

    // 입력 값 체크
    const values = [
      formData.householderName,
      formData.householderEmail,
      formData.householderPhone,
      formData.entrancePass,
      formData.householdSize,
    ];

    const isAnyFilled = values.some((v) => v && v.toString().trim() !== "");

    if (isAnyFilled) {
      const isAllFilled = values.every((v) => v && v.toString().trim() !== "");
      if (!isAllFilled) {
        alert("하나라도 입력된 칸이 있으면 모든 칸을 입력해야 합니다.");
        return;
      }
    }

    if (!isAnyFilled) {
      const confirmSave = window.confirm(
        "모든 입력 칸이 비어있으므로, 공실 처리됩니다. 저장하시겠습니까?",
      );
      if (!confirmSave) return;
    }

    const formattedFromData = {
      ...formData,
      householderPhone: formData.householderPhone.replace(/-/g, ""),
    };

    onSave(data.houseId, formattedFromData);
    alert("세대 등록이 완료되었습니다.");
  };

  // 초기화 버튼
  const handleResetClick = () => {
    setFormData({
      householderName: "",
      householderEmail: "",
      householderPhone: "",
      entrancePass: "",
      moveInAt: "",
      householdSize: "",
      cardUid: [],
    });
    setCardInput("");
  };

  const handleCloseClick = () => {
    setCardInput("");
    onClose();
  };

  const addCard = () => {
    if (!cardInput.trim()) return;

    if (formData.cardUid.includes(cardInput)) {
      alert("이미 등록된 카드입니다.");
      return;
    }
    setFormData((prev) => ({
      ...prev,
      cardUid: [...prev.cardUid, cardInput],
    }));

    setCardInput("");
  };

  const removeCard = (index) => {
    setFormData((prev) => ({
      ...prev,
      cardUid: prev.cardUid.filter((_, i) => i !== index),
    }));
  };

  // input 변경
  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: name === "householdSize" ? Number(value) : value,
    }));
  };

  if (!data) return null;

  return (
    <div className="modal-overlay">
      {/* 모달 박스 */}
      <div className="modal-box house-modal" onClick={(e) => e.stopPropagation()}>
        <h3>
          세대 정보 관리 {data.houseDong}동 {data.houseHo}호
        </h3>
        <div className="row">
          <div className="column">
            <p>
              <label>세대주 이름</label>
              <input
                name="householderName"
                type="text"
                placeholder="세대주 이름 입력"
                value={formData.householderName}
                onChange={handleChange}
              />
            </p>
            <p>
              <label>이메일</label>
              <input
                type="email"
                name="householderEmail"
                placeholder="이메일 입력"
                value={formData.householderEmail}
                onChange={handleChange}
              />
            </p>
            <p>
              <label>연락처</label>
              <input
                type="text"
                name="householderPhone"
                placeholder="연락처 입력 (-) 없이"
                value={formData.householderPhone}
                onChange={handlePhoneChange}
                maxLength={13}
              />
            </p>
            <p>
              <label>공동현관 비밀번호</label>
              <span style={{ fontSize: "12px" }}>* 공동현관 출입을 위한 비밀번호입니다</span>
              <input
                type="password"
                name="entrancePass"
                placeholder="4자리 숫자 입력 (예: 1234)"
                value={formData.entrancePass.slice(0, 4)}
                onChange={handleChange}
                maxLength={4}
              />
            </p>
            <p>
              <label>입주일</label>
              <input
                type="date"
                name="moveInAt"
                placeholder="연도-월-일"
                value={formData.moveInAt}
                onChange={handleChange}
              />
            </p>
            <p>
              <label>세대원 수</label>
              <input
                type="number"
                name="householdSize"
                placeholder="세대원 수를 입력"
                value={formData.householdSize}
                onChange={handleChange}
              />
            </p>
          </div>

          <div className="column">
            <label>RFID 출입카드</label>
            <div className="card-box">
              <input
                type="text"
                placeholder="카드 리더기 UID"
                value={cardInput}
                onChange={(e) => setCardInput(e.target.value)}
              />
              <button
                type="button"
                onClick={addCard}
                style={{
                  backgroundColor: "var(--blue-primary)",
                  borderRadius: "6px",
                  marginTop: "6px",
                  color: "white",
                }}
              >
                카드 추가
              </button>
              <div className="card-list">
                {formData.cardUid.map((uid, idx) => (
                  <div key={idx} className="list">
                    <span>{uid}</span>
                    <button onClick={() => removeCard(idx)}>삭제</button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        {errorMsg && <div style={{ color: "red", marginTop: "12px" }}>{errorMsg}</div>}

        <div className="modal-footer">
          <button onClick={handleCloseClick}>닫기</button>
          <button onClick={handleResetClick}>초기화</button>
          <button onClick={handleSaveClick}>저장</button>
        </div>
      </div>
    </div>
  );
};

export default HouseholdDetailModal;
