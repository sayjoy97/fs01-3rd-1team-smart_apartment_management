import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getTotalChargeInfo,
  getDailyList,
  getMonthlyList,
  getYearlyList,
  getChargeSettingInfo,
  chargeSettingUpdate,
} from "../../api/parkingFeeAPI";

import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
} from "recharts";

import "./FeeDetailPage.css";
import { FeeSettingModal } from "./FeeSettingModal";

export function FeeDetailPage() {
  const navigate = useNavigate();

  // 데이터 상태
  const [total, setTotal] = useState({});
  const [daily, setDaily] = useState([]);
  const [monthly, setMonthly] = useState([]);
  const [yearly, setYearly] = useState([]);

  // UI 상태
  const [mode, setMode] = useState("day"); // day | month | year
  const [openSetting, setOpenSetting] = useState(false); // 요금 설정 모달 등에 사용 가능

  const [setting, setSetting] = useState(null); // 초기 세팅 데이터

  // 데이터 호출
  useEffect(() => {
    getTotalChargeInfo().then((res) => setTotal(res.data));
    getDailyList().then((res) => setDaily(res.data));
    getMonthlyList().then((res) => setMonthly(res.data));
    getYearlyList().then((res) => setYearly(res.data));
    getChargeSettingInfo().then((res) => {
      // API 응답 구조가 { success: true, data: {...} } 이므로 res.data 전달
      if (res && res.data) setSetting(res.data);
    });
  }, []);

  // 모드별 그래프 설정값 정의 (제공된 데이터 키값 반영)
  const getChartConfig = () => {
    switch (mode) {
      case "month":
        return { xKey: "month", yKey: "monthlySum", label: "월별 누적 금액" };
      case "year":
        return { xKey: "year", yKey: "yearTotalSum", label: "연간 누적 금액" };
      case "day":
      default:
        return { xKey: "date", yKey: "amount", label: "일별 누적 금액" };
    }
  };

  const { xKey, yKey, label } = getChartConfig();
  const graphData = mode === "day" ? daily : mode === "month" ? monthly : yearly;

  // 2. 수정 저장 처리
  const handleUpdateSetting = async (updatedData) => {
    try {
      const res = await chargeSettingUpdate(updatedData);
      if (res && res.status === 200) {
        alert("요금 설정이 저장되었습니다.");
        setSetting(updatedData); // 화면 데이터 갱신
        setOpenSetting(false); // 모달 닫기
      }
    } catch (error) {
      alert("저장 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="fd-container">
      {/* 상단 바: 양 끝 정렬 적용 */}
      <div className="fd-top-bar">
        <button className="fd-back-btn" onClick={() => navigate("/cargate")}>
          ← 뒤로가기
        </button>
        <button className="fd-setting-btn" onClick={() => setOpenSetting(true)}>
          ⚙ 요금 설정
        </button>
      </div>

      {/* KPI 카드 그리드 */}
      <div className="fd-kpi-grid">
        <KPIItem title="금일 누적금액" value={total?.todayCount} color="blue" />
        <KPIItem title="월 누적금액" value={total?.thisMonthCount} color="green" />
        <KPIItem title="연 누적금액" value={total?.thisYearCount} color="purple" />
        <KPIItem title="월평균 금액" value={total?.monthAverageCount} color="orange" />
        <KPIItem title="일일 최고금액" value={total?.dayTopCount} color="red" />
        <KPIItem
          title="월평균 방문차량"
          value={`${total?.unRegisAverageCount || 0}대`}
          color="indigo"
        />
      </div>

      {/* 그래프 섹션 */}
      <div className="fd-chart-card">
        <div className="fd-chart-header">
          <div className="fd-chart-title">
            <h3>{label}</h3>
            <p>{mode === "day" ? "최근 30일간 누적 주차 요금 추이" : "기간별 요금 통계"}</p>
          </div>

          <div className="fd-toggle-group">
            <button className={mode === "day" ? "active" : ""} onClick={() => setMode("day")}>
              일별
            </button>
            <button className={mode === "month" ? "active" : ""} onClick={() => setMode("month")}>
              월별
            </button>
            <button className={mode === "year" ? "active" : ""} onClick={() => setMode("year")}>
              연간
            </button>
          </div>
        </div>

        <div className="fd-chart-content">
          <ResponsiveContainer width="100%" height={350}>
            <LineChart data={graphData}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
              <XAxis
                dataKey={xKey}
                tick={{ fontSize: 12, fill: "#888" }}
                axisLine={{ stroke: "#ddd" }}
              />
              <YAxis
                tick={{ fontSize: 12, fill: "#888" }}
                axisLine={false}
                tickFormatter={(value) => (value === 0 ? "0" : `${value / 1000}k`)}
              />
              <Tooltip formatter={(v) => v.toLocaleString() + "원"} />
              <Line
                type="monotone"
                dataKey={yKey}
                stroke="#4f46e5"
                strokeWidth={3}
                dot={{ r: 4, fill: "#4f46e5" }}
                activeDot={{ r: 6 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* 모달 추가 */}
      <FeeSettingModal
        isOpen={openSetting}
        onClose={() => setOpenSetting(false)}
        initialData={setting}
        onSave={handleUpdateSetting}
      />
    </div>
  );
}

// 독립적인 KPI 아이템 컴포넌트
function KPIItem({ title, value, color }) {
  return (
    <div className="fd-kpi-card">
      <div className="fd-kpi-label">{title}</div>
      <div className={`fd-kpi-value fd-text-${color}`}>
        {typeof value === "number" ? value.toLocaleString() + "원" : value || "0원"}
      </div>
    </div>
  );
}
