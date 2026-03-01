import { Card, CardContent, CardHeader, CardTitle } from "../../../components/ui/card";
import { Button } from "../../../components/ui/button";
import { Badge } from "../../../components/ui/badge";
import { Eye, MapPin, Bell, Clock } from "lucide-react";
import { statusUi } from "./noiseUi";

function timeZoneBadge(tz) {
  return tz === "야간" ? "bg-indigo-500" : "bg-orange-500";
}

export default function NoiseEventTable({
  events,
  loading,
  page,
  totalPages,
  totalElements,
  viewMode,
  eventFilter,
  onChangeFilter,
  onChangeViewMode,
  onPrevPage,
  onNextPage,
  onOpenDetail,
  listCounts, // ✅ 추가
  countLoading,
}) {
  return (
    <Card className="noise2-card noise2-mt">
      <CardHeader className="noise2-table-header">
        <div className="noise2-table-headrow">
          <div>
            <CardTitle className="noise2-cardtitle">소음 이벤트 목록</CardTitle>
            <div className="noise2-carddesc">전체 {totalElements}건</div>
          </div>

          {/* <div className="noise2-table-actions"> */}
          <div className="noise2-pillbar">
            {/* 상태 필터 */}
            <button
              type="button"
              className={`pill ${eventFilter === "all" ? "active" : ""}`}
              onClick={() => onChangeFilter("all")}
            >
              전체{" "}
              <span className="pill-count">({countLoading ? "…" : (listCounts?.all ?? 0)})</span>
            </button>

            <button
              type="button"
              className={`pill ${eventFilter === "unprocessed" ? "active" : ""}`}
              onClick={() => onChangeFilter("unprocessed")}
            >
              <Clock className="pill-icon" />
              대기{" "}
              <span className="pill-count">
                ({countLoading ? "…" : (listCounts?.unprocessed ?? 0)})
              </span>
            </button>

            <button
              type="button"
              className={`pill ${eventFilter === "observing" ? "active" : ""}`}
              onClick={() => onChangeFilter("observing")}
            >
              <Eye className="pill-icon" />
              관찰 중{" "}
              <span className="pill-count">
                ({countLoading ? "…" : (listCounts?.observing ?? 0)})
              </span>
            </button>

            <button
              type="button"
              className={`pill ${eventFilter === "notified" ? "active" : ""}`}
              onClick={() => onChangeFilter("notified")}
            >
              <Bell className="pill-icon" />
              알림 완료{" "}
              <span className="pill-count">
                ({countLoading ? "…" : (listCounts?.notified ?? 0)})
              </span>
            </button>

            <div className="pill-spacer" />

            {/* 뷰모드 */}
            <button
              type="button"
              className={`pill pill-ghost ${viewMode === "all" ? "active" : ""}`}
              onClick={() => onChangeViewMode("all")}
            >
              전체
            </button>

            <button
              type="button"
              className={`pill pill-ghost ${viewMode === "day" ? "active day" : ""}`}
              onClick={() => onChangeViewMode("day")}
            >
              주간
            </button>

            <button
              type="button"
              className={`pill pill-ghost ${viewMode === "night" ? "active night" : ""}`}
              onClick={() => onChangeViewMode("night")}
            >
              야간
            </button>
          </div>
        </div>
      </CardHeader>

      <CardContent>
        {loading ? (
          <div className="noise2-empty">로딩 중...</div>
        ) : !events?.length ? (
          <div className="noise2-empty">데이터가 없습니다.</div>
        ) : (
          <>
            <div className="noise2-tablewrap">
              <table className="noise2-table">
                <thead>
                  <tr>
                    <th>발생시간</th>
                    <th>위치</th>
                    <th>강도</th>
                    <th>반복</th>
                    <th>시간대</th>
                    <th>상태</th>
                    <th className="th-actions">작업</th>
                  </tr>
                </thead>

                <tbody>
                  {events.map((e) => (
                    <tr key={e.noiseEventId} className={e.urgentBreak ? "row-hot" : ""}>
                      <td className="td-muted">{new Date(e.occurredAt).toLocaleString("ko-KR")}</td>
                      <td>
                        <div className="td-location">
                          <MapPin className="size-3 text-gray-400" />
                          {e.upperHouseDong}동 {e.upperHouseHo}호 ↔ {e.lowerHouseDong}동{" "}
                          {e.lowerHouseHo}호
                        </div>
                      </td>
                      <td
                        className={
                          e.soundLevel >= 80
                            ? "td-db td-db-red"
                            : e.soundLevel >= 70
                              ? "td-db td-db-orange"
                              : "td-db"
                        }
                      >
                        {e.soundLevel} dB
                      </td>
                      <td>{e.repeatCount}회</td>
                      <td>
                        <Badge className={timeZoneBadge(e.timeZone)}>{e.timeZone}</Badge>
                      </td>
                      <td>
                        {(() => {
                          const s = statusUi(e.status);
                          return (
                            <div className="td-status">
                              <Badge className={s.cls}>{s.label}</Badge>

                              {/* URGENT는 "상태"가 아니라 "플래그"니까 옆에 작은 뱃지로만 */}
                              {e.urgentBreak && <Badge className="st-urgent">URGENT</Badge>}
                            </div>
                          );
                        })()}
                      </td>
                      <td className="td-actions">
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => onOpenDetail(e.noiseEventId)}
                        >
                          <Eye className="size-3 mr-1" />
                          상세
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {totalPages > 1 && (
              <div className="noise2-pagination">
                <div className="noise2-pageinfo">
                  {page + 1} / {totalPages}
                </div>
                <div className="noise2-pagebtns">
                  <Button variant="outline" disabled={page === 0} onClick={onPrevPage}>
                    이전
                  </Button>
                  <Button variant="outline" disabled={page + 1 >= totalPages} onClick={onNextPage}>
                    다음
                  </Button>
                </div>
              </div>
            )}
          </>
        )}
      </CardContent>
    </Card>
  );
}
