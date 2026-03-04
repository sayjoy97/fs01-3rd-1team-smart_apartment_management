import { useEffect, useState } from "react";
import "./LogDetailModal.css";
import {
  getUpdateLogData,
  getRegisCarDetail,
  getApproCarDetail,
  getLogDetail,
} from "../../api/cargateAPI";

const BASE_IMAGE_URL = "http://localhost:9600";

export function LogDetailModal({ open, setOpen, cargateEventId }) {
  const [loading, setLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [imagePath, setImagePath] = useState(null);

  const [formData, setFormData] = useState({
    plateNumber: "",
    vehicleType: "REGISTERED",
    houseInfo: "",
    vehicleOwner: "",
    approvalReason: "",
    startAt: "",
    endAt: "",
    stayMinutes: 0,
    calculatedFee: 0,
  });

  const formatStayTime = (minutes) => {
    if (!minutes || minutes <= 0) return "0시간 0분";
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return `${h}시간 ${m}분`;
  };

  useEffect(() => {
    if (!open || !cargateEventId) return;

    const fetchDetail = async () => {
      try {
        setLoading(true);
        const res = await getLogDetail({ cargate_event_log_id: cargateEventId });
        if (!res?.success) return;

        const data = res.data;
        const path = data.image_path || null;
        setImagePath(path);

        let extraData = null;
        if (data.vehicleType === "REGISTERED" && data.vehicleId) {
          const regRes = await getRegisCarDetail({ vehicle_id: data.vehicleId });
          if (regRes?.success) extraData = regRes.data;
        }

        if (data.vehicleType === "ADMIN_APPROVED" && data.vehicleId) {
          const appRes = await getApproCarDetail({ vehicle_id: data.vehicleId });
          if (appRes?.success) extraData = appRes.data;
        }

        setFormData({
          plateNumber: data.plateNumber || "",
          vehicleType: data.vehicleType || "REGISTERED",
          houseInfo: extraData?.houseInfo || "",
          vehicleOwner: extraData?.vehicleOwner || "",
          approvalReason: extraData?.approvalReason || "",
          startAt: data.entryAt?.slice(0, 16) || "",
          endAt: data.exitAt?.slice(0, 16) || "",
          stayMinutes: data.stayMinutes || 0,
          calculatedFee: data.calculatedFee || 0,
        });
      } catch (err) {
        console.error("상세 조회 실패", err);
      } finally {
        setLoading(false);
      }
    };

    fetchDetail();
  }, [open, cargateEventId]);

  const handleTypeChange = (type) => {
    setFormData({
      ...formData,
      vehicleType: type,
      houseInfo: "",
      vehicleOwner: "",
      approvalReason: "",
    });
  };

  const handleSave = async () => {
    if (!formData.plateNumber.trim()) {
      alert("번호판 정보를 입력해주세요.");
      return;
    }

    if (formData.vehicleType === "REGISTERED") {
      if (!formData.houseInfo.trim()) {
        alert("세대 정보를 입력해주세요 (예: 101동 101호).");
        return;
      }
      if (!formData.vehicleOwner.trim()) {
        alert("소유주 정보를 입력해주세요.");
        return;
      }
    } else if (formData.vehicleType === "ADMIN_APPROVED") {
      if (!formData.approvalReason.trim()) {
        alert("승인 사유를 입력해주세요.");
        return;
      }
    }

    try {
      const payload = {
        plateNumber: formData.plateNumber,
        vehicleType: formData.vehicleType,
        houseInfo: formData.houseInfo,
        vehicleOwner: formData.vehicleOwner,
        approvalReason: formData.approvalReason,
        startAt: formData.startAt,
        endAt: formData.endAt,
      };

      const res = await getUpdateLogData({ cargate_event_log_id: cargateEventId }, payload);

      if (res?.success) {
        alert("수정 완료");
        setIsEditMode(false);
        setOpen(false);
      }
    } catch (err) {
      console.error("저장 실패", err);
      alert("저장 중 오류가 발생했습니다.");
    }
  };

  const handleClose = () => {
    if (isEditMode) {
      const ok = window.confirm("수정을 취소하겠습니까?");
      if (!ok) return;
      setIsEditMode(false);
    }
    setOpen(false);
  };

  if (!open) return null;

  return (
    <div className="ldm-backdrop" onClick={handleClose}>
      <div className="ldm-card-horizontal" onClick={(e) => e.stopPropagation()}>
        <div className="ldm-image-container">
          <div className="ldm-image-wrapper">
            {imagePath ? (
              <img src={`${BASE_IMAGE_URL}/${encodeURI(imagePath)}`} alt="차량 이미지" />
            ) : (
              <div className="ldm-no-image">이미지 없음</div>
            )}
          </div>
        </div>

        <div className="ldm-content">
          <h2 className="ldm-title">차량 상세 정보</h2>

          {loading ? (
            <p className="ldm-loading">로딩중...</p>
          ) : (
            <div className="ldm-form">
              <div className="ldm-field">
                <label>번호판</label>
                <input
                  value={formData.plateNumber}
                  disabled={!isEditMode}
                  onChange={(e) => setFormData({ ...formData, plateNumber: e.target.value })}
                  placeholder="예: 12가 3456"
                />
              </div>

              <div className="ldm-field">
                <label>차량 유형</label>
                <select
                  value={formData.vehicleType}
                  disabled={!isEditMode}
                  onChange={(e) => handleTypeChange(e.target.value)}>
                  <option value="REGISTERED">세대차량</option>
                  <option value="UNREGISTERED">미등록차량</option>
                  <option value="ADMIN_APPROVED">관리자 승인차량</option>
                </select>
              </div>

              {/* 입차/출차 시간 필드: 항상 disabled로 설정 */}
              <div className="ldm-field">
                <label>입차시간</label>
                <input
                  type="datetime-local"
                  value={formData.startAt}
                  disabled // 수정 모드에서도 수정 불가
                />
              </div>

              <div className="ldm-field">
                <label>출차시간</label>
                <input
                  type="datetime-local"
                  value={formData.endAt}
                  disabled // 수정 모드에서도 수정 불가
                />
              </div>

              {formData.vehicleType === "REGISTERED" && (
                <>
                  <div className="ldm-field">
                    <label>세대 정보</label>
                    <input
                      value={formData.houseInfo}
                      disabled={!isEditMode}
                      onChange={(e) => setFormData({ ...formData, houseInfo: e.target.value })}
                      placeholder="예: 101동 101호"
                    />
                  </div>
                  <div className="ldm-field">
                    <label>소유주</label>
                    <input
                      value={formData.vehicleOwner}
                      disabled={!isEditMode}
                      onChange={(e) => setFormData({ ...formData, vehicleOwner: e.target.value })}
                      placeholder="이름 입력"
                    />
                  </div>
                </>
              )}

              {formData.vehicleType === "ADMIN_APPROVED" && (
                <div className="ldm-field">
                  <label>승인 사유</label>
                  <input
                    value={formData.approvalReason}
                    disabled={!isEditMode}
                    onChange={(e) => setFormData({ ...formData, approvalReason: e.target.value })}
                    placeholder="승인 사유 입력"
                  />
                </div>
              )}

              <div className="ldm-footer-info">
                <div className="ldm-field">
                  <label>체류 시간</label>
                  <input
                    value={formatStayTime(formData.stayMinutes)}
                    disabled
                    style={{ cursor: "default" }}
                  />
                </div>

                {formData.vehicleType === "UNREGISTERED" && (
                  <div className="ldm-field">
                    <label>정산 금액</label>
                    <input
                      className="fee-highlight"
                      value={`${formData.calculatedFee?.toLocaleString()}원`}
                      disabled
                      style={{ cursor: "default" }}
                    />
                  </div>
                )}
              </div>

              <div className="ldm-buttons">
                {!isEditMode ? (
                  <button className="ldm-btn-edit" onClick={() => setIsEditMode(true)}>
                    수정
                  </button>
                ) : (
                  <button className="ldm-btn-primary" onClick={handleSave}>
                    저장
                  </button>
                )}
                <button className="ldm-btn-close" onClick={handleClose}>
                  닫기
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
