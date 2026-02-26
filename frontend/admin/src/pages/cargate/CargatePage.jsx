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
  const [searchTerm, setSearchTerm] = useState("");
  const [simpleCharge, setSimpleCharge] = useState(null);
  const [totalPages, setTotalPages] = useState(1);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const navigate = useNavigate();

  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedEventId, setSelectedEventId] = useState(null);

  const handleRowClick = (cargateEventId) => {
    if (!cargateEventId) return;
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
    last7TypeCountList().then((res) => setTypeCountMap(res.data || {}));
    fetchRecords();
    getSimpleCharge().then((res) => setSimpleCharge(res.data || null));
  }, [currentPage]);

  const chartData = Object.entries(typeCountMap)
    .sort(([a], [b]) => new Date(a) - new Date(b))
    .map(([date, counts]) => ({
      date: date.slice(5).replace("-", "/"),
      REGISTERED: counts?.REGISTERED ?? 0,
      ADMIN_APPROVED: counts?.ADMIN_APPROVED ?? 0,
      UNREGISTERED: counts?.UNREGISTERED ?? 0,
    }));

  // 테이블 행 렌더링 (무조건 10개 행 유지)
  const renderTableRows = () => {
    const ROWS_PER_PAGE = 10;
    const filteredRecords = records.filter((r) => r.plateNumber.includes(searchTerm));
    const displayRows = [];

    // 데이터 행 추가
    filteredRecords.forEach((r) => {
      displayRows.push(
        <tr
          key={r.cargateEventId}
          className="cg-tr-hover"
          onClick={() => handleRowClick(r.cargateEventId)}
        >
          <td>{r.plateNumber}</td>
          <td>
            <span className={`cg-badge type-${r.vehicleType}`}>
              {r.vehicleType === "REGISTERED"
                ? "등록"
                : r.vehicleType === "UNREGISTERED"
                  ? "미등록"
                  : "세대 방문"}
            </span>
          </td>
          <td>
            <span className={`cg-badge status-${r.parkingStatus}`}>
              {r.parkingStatus === "ENTRY" ? "입차" : "출차"}
            </span>
          </td>
          <td>{r.eventAt?.replace("T", " ").slice(0, 16)}</td>
        </tr>,
      );
    });

    // 빈 행 채우기 (데이터가 10개 미만일 때)
    for (let i = displayRows.length; i < ROWS_PER_PAGE; i++) {
      displayRows.push(
        <tr key={`empty-${i}`} className="cg-tr-empty">
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        </tr>,
      );
    }
    return displayRows;
  };

  // 페이지네이션 렌더링 함수 추가
  const renderPagination = () => {
    const pageLimit = 5; // 한 번에 보여줄 페이지 번호 개수
    // 현재 페이지가 속한 그룹의 시작 페이지 계산 (1~5, 6~10...)
    const startPage = Math.floor((currentPage - 1) / pageLimit) * pageLimit + 1;
    const endPage = Math.min(startPage + pageLimit - 1, totalPages);

    const pages = [];

    // [<<] 처음으로 버튼
    pages.push(
      <button
        key="first"
        className="cg-page-nav"
        onClick={() => setCurrentPage(1)}
        disabled={currentPage === 1}
      >
        «
      </button>,
    );

    // [<] 이전 버튼
    pages.push(
      <button
        key="prev"
        className="cg-page-nav"
        onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
        disabled={currentPage === 1}
      >
        ‹
      </button>,
    );

    // 숫자 페이지 버튼 (최대 5개)
    for (let i = startPage; i <= endPage; i++) {
      pages.push(
        <button
          key={i}
          className={i === currentPage ? "active" : ""}
          onClick={() => setCurrentPage(i)}
        >
          {i}
        </button>,
      );
    }

    // [>] 다음 버튼
    pages.push(
      <button
        key="next"
        className="cg-page-nav"
        onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
        disabled={currentPage === totalPages}
      >
        ›
      </button>,
    );

    // [>>] 마지막으로 버튼
    pages.push(
      <button
        key="last"
        className="cg-page-nav"
        onClick={() => setCurrentPage(totalPages)}
        disabled={currentPage === totalPages}
      >
        »
      </button>,
    );

    return pages;
  };

  return (
    <div className="cg-main-wrapper">
      <div className="cg-container">
        {/* 왼쪽 섹션 (1.4비율) */}
        <div className="cg-left-col">
          <div className="cg-card cg-chart-card">
            <div className="cg-card-header">
              <h3>최근 7일 차량 출입 현황</h3>
              <p>유형별 입차 및 출차 기록</p>
            </div>
            <div className="cg-chart-body">
              <ResponsiveContainer width="100%" height={380}>
                <BarChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                  <XAxis
                    dataKey="date"
                    axisLine={false}
                    tickLine={false}
                    tick={{ fontSize: 12, fill: "#94a3b8" }}
                  />
                  <YAxis
                    axisLine={false}
                    tickLine={false}
                    tick={{ fontSize: 12, fill: "#94a3b8" }}
                  />
                  <RechartsTooltip cursor={{ fill: "#f8fafc" }} />
                  <Legend iconType="circle" verticalAlign="bottom" height={36} />
                  <Bar
                    dataKey="REGISTERED"
                    name="등록차량"
                    fill="#22c55e"
                    radius={[4, 4, 0, 0]}
                    barSize={15}
                  />
                  <Bar
                    dataKey="UNREGISTERED"
                    name="미등록차량"
                    fill="#ef4444"
                    radius={[4, 4, 0, 0]}
                    barSize={15}
                  />
                  <Bar
                    dataKey="ADMIN_APPROVED"
                    name="관리자 승인차량"
                    fill="#3b82f6"
                    radius={[4, 4, 0, 0]}
                    barSize={15}
                  />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="cg-card cg-fee-card">
            <div className="cg-card-header-row">
              <div className="cg-header-text">
                <h3>요금 현황</h3>
                <p>미등록 차량 주차 요금</p>
              </div>
              <button className="cg-btn-outline" onClick={() => navigate("/cargate/feeDetail")}>
                전체보기
              </button>
            </div>
            <div className="cg-fee-grid">
              <div className="cg-fee-item blue">
                <span className="cg-fee-label">💰 오늘 누적</span>
                <span className="cg-fee-val">
                  {simpleCharge?.todayRate?.toLocaleString() || 0}원
                </span>
              </div>
              <div className="cg-fee-item green">
                <span className="cg-fee-label">📈 이번달 누적</span>
                <span className="cg-fee-val">
                  {simpleCharge?.thisMonthRate?.toLocaleString() || 0}원
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* 오른쪽 섹션 (1비율) */}
        <div className="cg-right-col">
          <div className="cg-card cg-list-card">
            <div className="cg-card-header-row">
              <div className="cg-header-text">
                <h3>방문차량 목록</h3>
                <p>등록된 방문차량 내역</p>
              </div>
              <button className="cg-btn-primary" onClick={() => setIsAddModalOpen(true)}>
                + 차량 등록
              </button>
            </div>

            <div className="cg-search-wrapper">
              <div className="cg-search-inner">
                <span className="cg-search-icon">🔍</span>
                <input
                  type="text"
                  className="cg-search-input"
                  placeholder="차량번호로 검색..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>

            <div className="cg-table-container">
              <table className="cg-table">
                <thead>
                  <tr>
                    <th>차량번호</th>
                    <th>유형</th>
                    <th>상태</th>
                    <th>요청시간</th>
                  </tr>
                </thead>
                <tbody>{renderTableRows()}</tbody>
              </table>
            </div>

            <div className="cg-pagination">{renderPagination()}</div>
          </div>
        </div>
      </div>

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
  );
}
