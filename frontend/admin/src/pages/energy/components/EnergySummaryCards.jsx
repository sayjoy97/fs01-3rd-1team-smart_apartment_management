// src/pages/energy/components/EnergySummaryCards.jsx
import "./EnergySummaryCards.css";

export default function EnergySummaryCards({ dashboard }) {
  const monthlyUsage = dashboard?.monthlyUsageKwh ?? 0;
  const checkRequiredCount = dashboard?.checkRequiredCount ?? 0;
  const checkingCount = dashboard?.checkingCount ?? 0;
  const possibleSavingCost = dashboard?.possibleSavingCost ?? 0;

  return (
    <div className="energy-summary">
      <div className="card">
        <h3>이번 달 사용량</h3>
        <p>{monthlyUsage.toLocaleString()} kWh</p>
      </div>

      <div className="card">
        <h3>점검 필요</h3>
        <p>{checkRequiredCount} 대</p>
      </div>

      <div className="card">
        <h3>점검 중</h3>
        <p>{checkingCount} 대</p>
      </div>

      <div className="card">
        <h3>예상 절감액</h3>
        <p>₩{possibleSavingCost.toLocaleString()}</p>
      </div>
    </div>
  );
}
