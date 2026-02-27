import { useEffect, useState } from "react";
import "./LogDetailModal.css";
import {
  getUpdateLogData,
  getRegisCarDetail,
  getApproCarDetail,
  getLogDetail,
} from "../../api/cargateAPI";

const BASE_IMAGE_URL = "http://localhost:9600"; // 이미지 서버 주소

export function LogDetailModal({ open, setOpen, cargateEventId }) {
  const [loading, setLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);

  const [imagePath, setImagePath] = useState(null);

  const [formData, setFormData] = useState({
    plateNumber: "",
    vehicleType: "REGISTERED",
    houseInfo: null,
    vehicleOwner: "",
    approvalReason: "",
    startAt: "",
    endAt: "",
  });

  // ==============================
  // 상세 조회
  // ==============================
  useEffect(() => {
    if (!open || !cargateEventId) return;

    const fetchDetail = async () => {
      try {
        setLoading(true);

        const res = await getLogDetail({
          cargate_event_log_id: cargateEventId,
        });
        console.log("cargateEventId:", cargateEventId);

        if (!res?.success) return;

        const data = res.data;

        // 이미지 경로 저장 (백엔드 필드명에 맞게 수정 가능)
        const path = data.image_path || null;

        setImagePath(path);

        if (path) {
          console.log(`${BASE_IMAGE_URL}/${encodeURI(imagePath)}`);
        }

        let extraData = null;

        // REGISTERED
        if (data.vehicleType === "REGISTERED" && data.vehicleId) {
          const regRes = await getRegisCarDetail({
            vehicle_id: data.vehicleId,
          });
          if (regRes?.success) extraData = regRes.data;
        }

        // ADMIN_APPROVED
        if (data.vehicleType === "ADMIN_APPROVED" && data.vehicleId) {
          const appRes = await getApproCarDetail({
            vehicle_id: data.vehicleId,
          });
          if (appRes?.success) extraData = appRes.data;
        }

        // houseDong + houseHo 조합
        const houseInfoText =
          extraData?.houseDong && extraData?.houseHo
            ? `${extraData.houseDong}동 ${extraData.houseHo}호`
            : null;

        setFormData({
          plateNumber: data.plateNumber || "",
          vehicleType: data.vehicleType || "REGISTERED",

          houseInfo: houseInfoText,
          vehicleOwner: extraData?.vehicleOwner || "",

          approvalReason: extraData?.approvalReason || "",

          startAt: data.entryAt?.slice(0, 16) || "",
          endAt: data.exitAt?.slice(0, 16) || "",
        });
      } catch (err) {
        console.error("상세 조회 실패", err);
      } finally {
        setLoading(false);
      }
    };

    fetchDetail();
  }, [open, cargateEventId]);

  // 차량 유형 변경
  const handleTypeChange = (type) => {
    setFormData({
      ...formData,
      vehicleType: type,
      houseInfo: null,
      vehicleOwner: "",
      approvalReason: "",
    });
  };

  // 저장
  const handleSave = async () => {
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
        {/* ================= 왼쪽 이미지 (4:3 고정) ================= */}
        <div className="ldm-image-container">
          <div className="ldm-image-wrapper">
            {imagePath ? (
              <img src={`${BASE_IMAGE_URL}/${encodeURI(imagePath)}`} alt="차량 이미지" />
            ) : (
              <div className="ldm-no-image">이미지 없음</div>
            )}
          </div>
        </div>

        {/* ================= 오른쪽 정보 (컴팩트 레이아웃) ================= */}
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

              <div className="ldm-field">
                <label>입차시간</label>
                <input
                  type="datetime-local"
                  value={formData.startAt}
                  disabled={!isEditMode}
                  onChange={(e) => setFormData({ ...formData, startAt: e.target.value })}
                />
              </div>

              <div className="ldm-field">
                <label>출차시간</label>
                <input
                  type="datetime-local"
                  value={formData.endAt}
                  disabled={!isEditMode}
                  onChange={(e) => setFormData({ ...formData, endAt: e.target.value })}
                />
              </div>

              {formData.vehicleType === "REGISTERED" && (
                <>
                  <div className="ldm-field">
                    <label>세대 정보</label>
                    <input value={formData.houseInfo || "-"} disabled />
                  </div>
                  <div className="ldm-field">
                    <label>소유주</label>
                    <input
                      value={formData.vehicleOwner}
                      disabled={!isEditMode}
                      onChange={(e) => setFormData({ ...formData, vehicleOwner: e.target.value })}
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
                  />
                </div>
              )}

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
