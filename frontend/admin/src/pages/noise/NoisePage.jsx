// src/pages/noise/NoisePage.jsx

import "./NoisePage.css";
import { useNoiseState } from "./components/useNoiseState";

export default function NoisePage() {
  const { paginatedEvents, currentPage, setCurrentPage, viewMode, setViewMode } = useNoiseState();

  return (
    <div className="noise-page">
      <header className="noise-header">
        <h1>층간소음 관리</h1>
        <p>센서 기반 층간소음 감지 및 관리자 승인 시스템</p>
      </header>

      <section className="noise-controls">
        <button onClick={() => setViewMode("all")}>전체</button>
        <button onClick={() => setViewMode("day")}>주간</button>
        <button onClick={() => setViewMode("night")}>야간</button>
      </section>

      <section className="noise-list">
        {paginatedEvents.map((event) => (
          <div key={event.id} className="noise-item">
            <strong>{event.building}</strong> {event.floor}층 · {event.intensity}dB
          </div>
        ))}
      </section>

      <footer className="noise-pagination">
        <button onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}>이전</button>
        <span>{currentPage}</span>
        <button onClick={() => setCurrentPage((p) => p + 1)}>다음</button>
      </footer>
    </div>
  );
}
