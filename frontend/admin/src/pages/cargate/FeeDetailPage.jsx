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

export function FeeDetailPage() {
  const navigate = useNavigate();

  // =========================
  // 데이터
  // =========================
  const [total, setTotal] = useState({});
  const [daily, setDaily] = useState([]);
  const [monthly, setMonthly] = useState([]);
  const [yearly, setYearly] = useState([]);

  const [setting, setSetting] = useState({});
  const [form, setForm] = useState({});

  // =========================
  // UI 상태
  // =========================
  const [mode, setMode] = useState("day"); // day | month | year
  const [openSetting, setOpenSetting] = useState(false);
  const [editMode, setEditMode] = useState(false);

  // =========================
  // 최초 로딩
  // =========================
  useEffect(() => {
    getTotalChargeInfo()
      .then((res) => setTotal(res.data))
      .catch((err) => console.error("통합정보 요청실패: ", err));

    getDailyList()
      .then((res) => setDaily(res.data))
      .catch((err) => console.error("일별 정보 요청실패: ", err));

    getMonthlyList()
      .then((res) => setMonthly(res.data))
      .catch((err) => console.error("월별 정보 요청실패: ", err));

    getYearlyList()
      .then((res) => {
        setSetting(res.data);
        setForm(res.data);
      })
      .catch((err) => console.error("연간 정보 요청실패: ", err));

    getChargeSettingInfo()
      .then((res) => setTotal(res.data))
      .catch((err) => console.error("요금설정 정보 요청실패: ", err));
  }, []);

  // =========================
  // 설정 수정
  // =========================
  const onChange = (key, val) => {
    setForm((prev) => ({ ...prev, [key]: val }));
  };

  const handleSave = async () => {
    await chargeSettingUpdate(form);
    setSetting(form);
    setEditMode(false);
    setOpenSetting(false);
  };

  // =========================
  // 그래프 데이터
  // =========================
  const graphData = mode === "day" ? daily : mode === "month" ? monthly : yearly;

  const showSummary = mode !== "day";

  const sum = graphData.reduce((a, b) => a + (b.chargeAmount || 0), 0);
  const avg = graphData.length ? Math.floor(sum / graphData.length) : 0;

  // =========================
  // 렌더
  // =========================
  return (
    <div className="page">
      {/* 상단 */}
      <div className="top-bar">
        <button className="back-btn" onClick={() => navigate("/cargate")}>
          ← 뒤로가기
        </button>

        <button className="setting-btn" onClick={() => setOpenSetting(true)}>
          ⚙ 요금 설정
        </button>
      </div>

      {/* KPI 카드 */}
      <div className="kpi-grid-v2">
        <KPI2 title="금일 누적금액" value={total?.todayCount} color="blue" />
        <KPI2 title="월 누적금액" value={total?.thisMonthCount} color="green" />
        <KPI2 title="연 누적금액" value={total?.thisYearCount} color="purple" />
        <KPI2 title="월평균 금액" value={total?.monthAverageCount} color="orange" />
        <KPI2 title="일일 최고금액" value={total?.dayTopCount} color="red" />
        <KPI2 title="월평균 방문차량" value={`${total?.unRegisAverageCount}대`} color="indigo" />
      </div>

      {/* 그래프 카드 */}
      <div className="chart-card">
        <div className="chart-header">
          <h3>주차 요금 통계</h3>

          <div className="toggle">
            <button className={mode === "day" ? "active" : ""} onClick={() => setMode("day")}>
              일별
            </button>
            <button className={mode === "month" ? "active" : ""} onClick={() => setMode("month")}>
              월별
            </button>
            <button className={mode === "year" ? "active" : ""} onClick={() => setMode("year")}>
              연별
            </button>
          </div>
        </div>

        {/* 월/연일 때만 요약 표시 */}
        {showSummary && (
          <div className="summary-box">
            <div>
              <span>누적 금액</span>
              <b>{sum.toLocaleString()}원</b>
            </div>
            <div>
              <span>평균 금액</span>
              <b>{avg.toLocaleString()}원</b>
            </div>
          </div>
        )}

        {/* 그래프 */}
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={graphData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey={mode === "day" ? "day" : mode === "month" ? "month" : "year"} />
            <YAxis />
            <Tooltip formatter={(v) => v.toLocaleString() + "원"} />
            <Line
              type="monotone"
              dataKey="chargeAmount"
              stroke="#2563eb"
              strokeWidth={3}
              dot={false}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>

      {/* 설정 모달 */}
      {openSetting && (
        <FeeSettingModal
          setting={form}
          onClose={() => {
            setOpenSetting(false);
            setEditMode(false);
          }}
          editMode={editMode}
          setEditMode={setEditMode}
          onChange={onChange}
          onSave={handleSave}
        />
      )}
    </div>
  );
}

// =========================
// KPI 카드
// =========================
function KPI2({ title, value, color }) {
  return (
    <div className={`kpi-card ${color}`}>
      <div className="kpi-title">{title}</div>
      <div className="kpi-value">
        {typeof value === "number" ? value.toLocaleString() + "원" : value}
      </div>
    </div>
  );
}

// =========================
// 설정 모달
// =========================
function FeeSettingModal({ setting, onClose, editMode, setEditMode, onChange, onSave }) {
  const [peakOn, setPeakOn] = useState(setting?.peakEnabled ?? false);

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h2>주차 요금 설정</h2>
          <span className="close" onClick={onClose}>
            ✕
          </span>
        </div>

        <div className="modal-footer">
          {editMode ? (
            <>
              <button className="cancel" onClick={() => setEditMode(false)}>
                취소
              </button>
              <button className="save" onClick={onSave}>
                저장
              </button>
            </>
          ) : (
            <button className="edit" onClick={() => setEditMode(true)}>
              수정
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
