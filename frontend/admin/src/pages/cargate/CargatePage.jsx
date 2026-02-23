import React, { useEffect, useState } from "react";
import { getCargateRecordList, last7TypeCountList } from "./../../api/cargateAPI";
import { getSimpleCharge } from "../../api/parkingFeeAPI";
import "./CargatePage.css";
import { AddVehicleModal } from "./AddVehicleModal";
import { LogDetailModal } from "./LogDetailModal";
import {
  ResponsiveContainer,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip as RechartsTooltip,
  Legend,
  BarChart,
  Bar,
} from "recharts";
import { useNavigate } from "react-router-dom";

export function CargatePage() {
  const [currentPage, setCurrentPage] = useState(1);
  const [typeCountMap, setTypeCountMap] = useState({});
  const [records, setRecords] = useState([]);
  const [simpleCharge, setSimpleCharge] = useState(null);
  const [totalPages, setTotalPages] = useState(1);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  const navigate = useNavigate();

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

  // 7일 통계용 차트 데이터 변환
  const chartData = Object.entries(typeCountMap)
    .sort(([a], [b]) => new Date(a) - new Date(b)) // 날짜순 정렬
    .map(([date, counts]) => ({
      date: date.slice(5).replace("-", "/"), // 02-17 → 02/17
      REGISTERED: counts?.REGISTERED ?? 0,
      ADMIN_APPROVED: counts?.ADMIN_APPROVED ?? 0,
      UNREGISTERED: counts?.UNREGISTERED ?? 0,
    }));

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
          <div className="graph-placeholder">
            <div
              style={{
                width: "100%",
                height: 320,
                background: "#fff",
                borderRadius: "12px",
                padding: "10px 10px 10px 0px",
              }}>
              <ResponsiveContainer>
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <RechartsTooltip />
                  <Legend />

                  <Bar dataKey="REGISTERED" name="등록차량" fill="#22c55e" />
                  <Bar dataKey="ADMIN_APPROVED" name="관리자승인" fill="#ef4444" />
                  <Bar dataKey="UNREGISTERED" name="기타차량" fill="#3b82f6" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        <div className="stats-card fee-card">
          <div className="fee-header">
            <div>
              <h3>요금 현황</h3>
              <p className="stats-sub">미등록 차량 주차 요금</p>
            </div>

            <button className="btn-fee-all" onClick={() => navigate("/cargate/feeDetail")}>
              전체보기
            </button>
          </div>

          {simpleCharge ? (
            <div className="fee-box-grid">
              <div className="fee-box blue">
                <p className="fee-label">오늘 누적</p>
                <p className="fee-value">{simpleCharge.todayRate?.toLocaleString()}원</p>
              </div>

              <div className="fee-box green">
                <p className="fee-label">이번달 누적</p>
                <p className="fee-value">{simpleCharge.thisMonthRate?.toLocaleString()}원</p>
              </div>
            </div>
          ) : (
            <p className="no-fee">요금 정보 없음</p>
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
                onClick={() => handleRowClick(r.cargateEventId)}>
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
              disabled={currentPage === 1}>
              ◀
            </button>

            {Array.from({ length: totalPages }, (_, i) => i + 1).map((p) => (
              <button
                key={p}
                className={p === currentPage ? "active" : ""}
                onClick={() => setCurrentPage(p)}>
                {p}
              </button>
            ))}

            <button
              onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
              disabled={currentPage === totalPages}>
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
