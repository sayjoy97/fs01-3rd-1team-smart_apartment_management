import React, { useState } from "react";
import { carRegister } from "../../api/cargateAPI";

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

      const res = await carRegister(payload);

      console.log("등록 성공:", res);

      alert("차량 등록 완료");

      // 부모 갱신 콜백
      onSuccess?.();

      // 초기화
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
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40"
      onClick={onClose}
    >
      <div className="bg-white rounded-lg w-full max-w-md p-6" onClick={(e) => e.stopPropagation()}>
        <h2 className="text-lg font-semibold mb-2">차량 등록</h2>

        <div className="space-y-4">
          {/* 차량번호 */}
          <input
            type="text"
            placeholder="차량번호"
            value={plateNumber}
            onChange={(e) => setPlateNumber(e.target.value)}
            className="w-full px-3 py-2 border rounded"
          />

          {/* 차량유형 */}
          <select
            value={vehicleType}
            onChange={(e) => setVehicleType(e.target.value)}
            className="w-full px-3 py-2 border rounded"
          >
            <option value="REGISTERED">세대 차량</option>
            <option value="ADMIN_APPROVED">관리자 승인 차량</option>
          </select>

          {/* REGISTERED */}
          {vehicleType === "REGISTERED" && (
            <>
              <input
                type="text"
                placeholder="세대정보를 입력하세요 (예: 101동 102호)"
                value={houseInfo}
                onChange={(e) => setHouseInfo(e.target.value)}
                className="w-full px-3 py-2 border rounded"
              />
              <input
                type="text"
                placeholder="차량 소유자"
                value={vehicleOwner}
                onChange={(e) => setVehicleOwner(e.target.value)}
                className="w-full px-3 py-2 border rounded"
              />
            </>
          )}

          {/* ADMIN_APPROVED */}
          {vehicleType === "ADMIN_APPROVED" && (
            <>
              <input
                type="text"
                placeholder="승인 사유"
                value={approvalReason}
                onChange={(e) => setApprovalReason(e.target.value)}
                className="w-full px-3 py-2 border rounded"
              />
              <input
                type="date"
                value={startAt}
                onChange={(e) => setStartAt(e.target.value)}
                className="w-full px-3 py-2 border rounded"
              />
              <input
                type="date"
                value={endAt}
                onChange={(e) => setEndAt(e.target.value)}
                className="w-full px-3 py-2 border rounded"
              />
            </>
          )}
        </div>

        <div className="mt-6 flex justify-end gap-2">
          <button onClick={onClose} className="px-4 py-2 border rounded">
            취소
          </button>
          <button
            onClick={handleAdd}
            disabled={loading}
            className="px-4 py-2 bg-blue-600 text-white rounded disabled:opacity-50"
          >
            {loading ? "등록중..." : "등록"}
          </button>
        </div>
      </div>
    </div>
  );
}
