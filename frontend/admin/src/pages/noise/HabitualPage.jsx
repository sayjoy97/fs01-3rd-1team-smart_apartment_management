// src/pages/noise/HabitualPage.jsx
import "./NoisePage.css";

import { useEffect } from "react";
import { Button } from "../../components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";
import { Badge } from "../../components/ui/badge";

import { AlertTriangle, CheckCircle2, Activity, Eye } from "lucide-react";

import HabitualDetailModal from "./components/HabitualDetailModal";
import useHabitualPage from "./components/useHabitualPage";

function statusBadge(status) {
  if (status === "MONITORING") {
    return { cls: "bg-red-100 text-red-700", label: "모니터링 중" };
  }
  if (status === "CLOSED") {
    return { cls: "bg-gray-100 text-gray-700", label: "종료" };
  }
  return { cls: "bg-gray-100 text-gray-700", label: status ?? "-" };
}

export default function HabitualPage({ onBack }) {
  const {
    // counts
    counts,
    countsLoading,
    loadCounts,

    // list
    zones,
    listLoading,
    statusFilter,
    setStatusFilter,
    page,
    totalPages,
    totalElements,
    setPage,
    loadZones,

    // detail
    detailOpen,
    detailLoading,
    detail,
    openDetail,
    closeDetail,

    // actions
    endMonitoring,
    sendNotification,
  } = useHabitualPage();

  useEffect(() => {
    loadCounts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    loadZones();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [statusFilter, page]);

  const monitoringCount = counts?.monitoringCount ?? 0;
  const closedCount = counts?.closedCount ?? 0;

  return (
    <div className="noise-page">
      <div className="noise-header-row">
        <div>
          <h1 className="noise-title">상습 구간 관리</h1>
          <p className="noise-subtitle">반복적인 층간소음 발생 구간 모니터링</p>
        </div>

        <div className="noise-header-actions">
          <Button variant="outline" onClick={onBack}>
            돌아가기
          </Button>
        </div>
      </div>

      {/* ===== 상단 카드 ===== */}
      <div className="dashboard-grid">
        <Card className="border-2 border-red-400/60">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-gray-600">모니터링 중</CardTitle>
          </CardHeader>
          <CardContent>
            {countsLoading ? (
              <div className="text-sm text-gray-500">로딩 중...</div>
            ) : (
              <div className="flex items-center justify-between">
                <div className="text-2xl font-semibold">{monitoringCount} 구간</div>
                <div className="p-2 rounded-lg bg-red-500">
                  <AlertTriangle className="size-5 text-white" />
                </div>
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-gray-600">종료된 구간</CardTitle>
          </CardHeader>
          <CardContent>
            {countsLoading ? (
              <div className="text-sm text-gray-500">로딩 중...</div>
            ) : (
              <div className="flex items-center justify-between">
                <div className="text-2xl font-semibold">{closedCount} 구간</div>
                <div className="p-2 rounded-lg bg-gray-500">
                  <CheckCircle2 className="size-5 text-white" />
                </div>
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-gray-600">전체 등록 구간</CardTitle>
          </CardHeader>
          <CardContent>
            {countsLoading ? (
              <div className="text-sm text-gray-500">로딩 중...</div>
            ) : (
              <div className="flex items-center justify-between">
                <div className="text-2xl font-semibold">{totalElements} 구간</div>
                <div className="p-2 rounded-lg bg-blue-500">
                  <Activity className="size-5 text-white" />
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      {/* ===== 목록 ===== */}
      <Card>
        <div className="list-topbar">
          <div className="pillbar">
            <button
              type="button"
              className={`pill ${statusFilter === "ALL" ? "active" : ""}`}
              onClick={() => {
                setStatusFilter("ALL");
                setPage(0);
              }}
            >
              전체 <span className="pill-count">({totalElements})</span>
            </button>

            <button
              type="button"
              className={`pill ${statusFilter === "MONITORING" ? "active" : ""}`}
              onClick={() => {
                setStatusFilter("MONITORING");
                setPage(0);
              }}
            >
              <AlertTriangle className="pill-icon" />
              모니터링 중 <span className="pill-count">({monitoringCount})</span>
            </button>

            <button
              type="button"
              className={`pill ${statusFilter === "CLOSED" ? "active" : ""}`}
              onClick={() => {
                setStatusFilter("CLOSED");
                setPage(0);
              }}
            >
              <CheckCircle2 className="pill-icon" />
              종료 <span className="pill-count">({closedCount})</span>
            </button>
          </div>
        </div>

        <CardContent>
          {listLoading ? (
            <div className="py-10 text-center text-sm text-gray-500">로딩 중...</div>
          ) : !zones.length ? (
            <div className="py-10 text-center text-sm text-gray-500">데이터가 없습니다.</div>
          ) : (
            <>
              <div className="table-wrap">
                <table className="noise-table">
                  <thead>
                    <tr>
                      <th>위치</th>
                      <th>최근 30일 발생</th>
                      <th>평균 강도</th>
                      <th>등록일</th>
                      <th>상태</th>
                      <th>작업</th>
                    </tr>
                  </thead>

                  <tbody>
                    {zones.map((z) => {
                      const s = statusBadge(z.status);
                      return (
                        <tr key={z.zoneId} className={z.status === "MONITORING" ? "row-hot" : ""}>
                          <td>
                            {z.upperHouseDong}동 {z.upperHouseHo}호 ↔ {z.lowerHouseDong}동{" "}
                            {z.lowerHouseHo}호
                          </td>
                          <td className="font-medium text-red-600">{z.eventCount30d}회</td>
                          <td className="font-medium text-orange-600">
                            {Number(z.avgSoundLevel ?? 0).toFixed(1)} dB
                          </td>
                          <td>
                            {z.startedAt ? new Date(z.startedAt).toLocaleDateString("ko-KR") : "-"}
                          </td>
                          <td>
                            <Badge className={s.cls}>{s.label}</Badge>
                          </td>
                          <td>
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => openDetail(z.zoneId)}
                            >
                              <Eye className="size-3 mr-1" />
                              상세
                            </Button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>

              {totalPages > 1 && (
                <div className="pagination">
                  <Button disabled={page === 0} variant="outline" onClick={() => setPage(page - 1)}>
                    이전
                  </Button>

                  <span className="page-indicator">
                    {page + 1} / {totalPages}
                  </span>

                  <Button
                    disabled={page + 1 >= totalPages}
                    variant="outline"
                    onClick={() => setPage(page + 1)}
                  >
                    다음
                  </Button>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>

      {/* ===== 상세 모달 ===== */}
      <HabitualDetailModal
        open={detailOpen}
        onClose={closeDetail}
        loading={detailLoading}
        detail={detail}
        onSendNotification={sendNotification}
        onEndMonitoring={endMonitoring}
      />
    </div>
  );
}
