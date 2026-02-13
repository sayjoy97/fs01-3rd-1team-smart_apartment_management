// src/pages/energy/components/EnergySummaryCards.jsx
import "./EnergySummaryCards.css";

function EnergySummaryCards({ dashboard }) {
  if (!dashboard) return null;

  return (
    <div className="energy-summary">
      <div className="card">
        <h3>이번 달 사용량</h3>
        <p>{dashboard.monthUsage} kWh</p>
      </div>

      <div className="card">
        <h3>오늘 점검 필요</h3>
        <p>{dashboard.todayCheckRequiredCount}건</p>
      </div>

      <div className="card">
        <h3>점검 대기 설비</h3>
        <p>{dashboard.checkRequiredDeviceCount}대</p>
      </div>
    </div>
  );
}

export default EnergySummaryCards;
