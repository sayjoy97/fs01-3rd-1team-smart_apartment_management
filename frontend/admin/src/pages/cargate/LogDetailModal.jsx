import { useEffect, useState } from "react";
import "./LogDetailModal.css";
import {
  getLogDetail,
  getUpdateLogData,
  getRegisCarDetail,
  getApproCarDetail,
} from "../../api/cargateAPI";

export function LogDetailModal({ open, setOpen, cargateEventId }) {
  const [loading, setLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);

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
  // 상세 조회 + 유형별 추가 조회
  // ==============================
  useEffect(() => {
    if (!open || !cargateEventId) return;

    const fetchDetail = async () => {
      try {
        setLoading(true);

        // 1️⃣ 출입기록 상세 조회
        const res = await getLogDetail({
          cargate_event_log_id: cargateEventId,
        });

        if (!res?.success) return;

        const data = res.data;

        let extraData = null;

        // 2️⃣ 차량 유형에 따른 추가 조회
        if (data.vehicleType === "REGISTERED" && data.vehicleId) {
          const regRes = await getRegisCarDetail({
            vehicle_id: data.vehicleId,
          });

          if (regRes?.success) {
            extraData = regRes.data;
          }
        }

        if (data.vehicleType === "ADMIN_APPROVED" && data.vehicleId) {
          const appRes = await getApproCarDetail({
            vehicle_id: data.vehicleId,
          });

          if (appRes?.success) {
            extraData = appRes.data;
          }
        }

        // 3️⃣ formData 세팅
        setFormData({
          plateNumber: data.plateNumber || "",
          vehicleType: data.vehicleType || "REGISTERED",

          // REGISTERED 채움
          houseInfo: extraData?.houseInfo || null,
          vehicleOwner: extraData?.vehicleOwner || "",

          // ADMIN_APPROVED 채움
          approvalReason: extraData?.approvalReason || "",

          // 공통 시간
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

  // ==============================
  // 차량 유형 변경
  // ==============================
  const handleTypeChange = (type) => {
    setFormData({
      ...formData,
      vehicleType: type,
      houseInfo: null,
      vehicleOwner: "",
      approvalReason: "",
    });
  };

  // ==============================
  // 저장
  // ==============================
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

  // ==============================
  // 닫기
  // ==============================
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
    <div className="modal-backdrop" onClick={handleClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <h2>차량 상세</h2>

        {loading ? (
          <p>로딩중...</p>
        ) : (
          <>
            {/* 번호판 */}
            <div className="modal-field">
              <label>번호판</label>
              <input
                value={formData.plateNumber}
                disabled={!isEditMode}
                onChange={(e) => setFormData({ ...formData, plateNumber: e.target.value })}
              />
            </div>

            {/* 차량 유형 */}
            <div className="modal-field">
              <label>차량 유형</label>
              <select
                value={formData.vehicleType}
                disabled={!isEditMode}
                onChange={(e) => handleTypeChange(e.target.value)}
              >
                <option value="REGISTERED">세대차량</option>
                <option value="UNREGISTERED">미등록차량</option>
                <option value="ADMIN_APPROVED">관리자 승인차량</option>
              </select>
            </div>

            {/* 공통 시간 */}
            <div className="modal-field">
              <label>입차시간</label>
              <input
                type="datetime-local"
                value={formData.startAt}
                disabled={!isEditMode}
                onChange={(e) => setFormData({ ...formData, startAt: e.target.value })}
              />
            </div>

            <div className="modal-field">
              <label>출차시간</label>
              <input
                type="datetime-local"
                value={formData.endAt}
                disabled={!isEditMode}
                onChange={(e) => setFormData({ ...formData, endAt: e.target.value })}
              />
            </div>

            {/* REGISTERED */}
            {formData.vehicleType === "REGISTERED" && (
              <>
                <div className="modal-field">
                  <label>세대 정보</label>
                  <input
                    value={formData.houseInfo || ""}
                    disabled={!isEditMode}
                    onChange={(e) => setFormData({ ...formData, houseInfo: e.target.value })}
                  />
                </div>

                <div className="modal-field">
                  <label>차량 소유주</label>
                  <input
                    value={formData.vehicleOwner}
                    disabled={!isEditMode}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        vehicleOwner: e.target.value,
                      })
                    }
                  />
                </div>
              </>
            )}

            {/* ADMIN_APPROVED */}
            {formData.vehicleType === "ADMIN_APPROVED" && (
              <div className="modal-field">
                <label>승인 사유</label>
                <input
                  value={formData.approvalReason}
                  disabled={!isEditMode}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      approvalReason: e.target.value,
                    })
                  }
                />
              </div>
            )}

            <div className="modal-buttons">
              {!isEditMode ? (
                <button onClick={() => setIsEditMode(true)}>수정</button>
              ) : (
                <button className="primary" onClick={handleSave}>
                  저장
                </button>
              )}
              <button onClick={handleClose}>닫기</button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
