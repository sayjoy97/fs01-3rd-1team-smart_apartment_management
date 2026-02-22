import React, { useEffect, useState } from "react";
import { getCargateRecordList, last7TypeCountList } from "./../../api/cargateAPI";
import { getSimpleCharge } from "../../api/parkingFeeAPI";
import "./Cargate.css";
import { AddVehicleModal } from "./AddVehicleModal";
import { LogDetailModal } from "./LogDetailModal";

export function CargatePage() {
  const [currentPage, setCurrentPage] = useState(1);
  const [typeCountMap, setTypeCountMap] = useState({});
  const [records, setRecords] = useState([]);
  const [simpleCharge, setSimpleCharge] = useState(null);
  const [totalPages, setTotalPages] = useState(1);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  // 상세 모달
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedEventId, setSelectedEventId] = useState(null);

  const handleRowClick = (cargateEventId) => {
    setSelectedEventId(cargateEventId);
    setIsDetailModalOpen(true);
  };

  const fetchRecords = () => {
    getCargateRecordList({ size: 10, page: currentPage })
      .then((res) => {
        setRecords(res.data?.content || []);
        setTotalPages(res.data?.totalPages || 1);
      })
      .catch((err) => console.error("출입기록 실패", err));
  };

  useEffect(() => {
    last7TypeCountList()
      .then((res) => setTypeCountMap(res.data || {}))
      .catch((err) => console.error("7일 통계 실패", err));

    fetchRecords();

    getSimpleCharge()
      .then((res) => setSimpleCharge(res.data || null))
      .catch((err) => console.error("간단 요금 정보 호출 실패", err));
  }, [currentPage]);

  return (
    <div className="cargate-grid">
      {/* 왼쪽 */}
      <div className="left-column">
        <div className="stats-card graph-card">
          <h3>최근 7일 차량 출입 현황</h3>
          <p className="stats-sub">유형별 입차 및 출차 기록</p>
          <div className="graph-placeholder">그래프 영역</div>
        </div>

        <div className="stats-card charge-card">
          <h3>간단 요금 정보</h3>
          {simpleCharge ? (
            <div className="charge-box">
              <p>오늘 누적: {simpleCharge.todayRate}원</p>
              <p>이번달 누적: {simpleCharge.thisMonthRate}원</p>
            </div>
          ) : (
            <p>요금 정보 없음</p>
          )}
        </div>
      </div>

      {/* 오른쪽 */}
      <div className="records-card right-column">
        <div className="record-header">
          <h3>차량 출입 기록</h3>
          <button className="btn-add" onClick={() => setIsAddModalOpen(true)}>
            차량 등록
          </button>
        </div>

        <table className="record-table">
          <thead>
            <tr>
              <th>차량번호</th>
              <th>상태</th>
              <th>유형</th>
              <th>시간</th>
            </tr>
          </thead>
          <tbody>
            {records.map((r) => (
              <tr
                key={r.cargateEventId}
                className="record-row"
                onClick={() => handleRowClick(r.cargateEventId)}
              >
                <td>{r.plateNumber}</td>
                <td>
                  <span className={`status ${r.parkingStatus}`}>
                    {r.parkingStatus === "ENTRY" ? "입차" : "출차"}
                  </span>
                </td>
                <td>
                  <span className={`type ${r.vehicleType}`}>
                    {r.vehicleType === "REGISTERED"
                      ? "등록"
                      : r.vehicleType === "ADMIN_APPROVED"
                        ? "관리자 승인"
                        : r.vehicleType === "UNREGISTERED"
                          ? "미등록"
                          : r.vehicleType}
                  </span>
                </td>
                <td>{r.eventAt?.replace("T", " ").slice(0, 16)}</td>
              </tr>
            ))}
          </tbody>
        </table>

        {/* 페이지네이션 */}
        {records.length > 0 && (
          <div className="pagination">
            <button
              onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
              disabled={currentPage === 1}
            >
              ◀
            </button>

            {Array.from({ length: totalPages }, (_, i) => i + 1).map((p) => (
              <button
                key={p}
                className={p === currentPage ? "active" : ""}
                onClick={() => setCurrentPage(p)}
              >
                {p}
              </button>
            ))}

            <button
              onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
              disabled={currentPage === totalPages}
            >
              ▶
            </button>
          </div>
        )}

        <AddVehicleModal
          isOpen={isAddModalOpen}
          onClose={() => setIsAddModalOpen(false)}
          onSuccess={fetchRecords}
        />

        <LogDetailModal
          open={isDetailModalOpen}
          setOpen={setIsDetailModalOpen}
          cargateEventId={selectedEventId}
        />
      </div>
    </div>
  );
}
