import { Card, CardContent, CardHeader, CardTitle } from "../../../components/ui/card";
import { Badge } from "../../../components/ui/badge";
import { Button } from "../../../components/ui/button";
import { AlertCircle, MapPin, Eye, ChevronLeft, ChevronRight } from "lucide-react";

function formatDateTime(dt) {
  if (!dt) return "-";
  return new Date(dt).toLocaleString("ko-KR", { hour12: false });
}

export default function NoisePendingPanel({
  pendingEvents,
  pendingCount,
  onOpenDetail,
  onQuickObserve, // optional
  onQuickNotify, // optional
  page = 0,
  totalPages = 1,
  onPrev,
  onNext,
  pendingLoading,
}) {
  if (!pendingEvents?.length) return null;

  return (
    <Card className="pending-card">
      <CardHeader className="pending-head">
        <div className="pending-head-row">
          <div className="pending-title-wrap">
            <AlertCircle className="size-5 pending-ic" />
            <CardTitle>즉시 처리 필요</CardTitle>
          </div>
          <Badge className="pending-badge">승인 대기 {pendingCount}건</Badge>
        </div>
        <div className="pending-desc">긴급 이벤트가 승인을 대기하고 있습니다</div>
      </CardHeader>

      <CardContent className="pending-body">
        <div className="pending-list">
          {Array.from({ length: 5 }).map((_, idx) => {
            const e = pendingEvents[idx];

            // 🔹 빈 슬롯
            if (!e) {
              return <div key={`empty-${idx}`} className="pending-item pending-empty-slot" />;
            }

            // 🔹 실제 데이터
            return (
              <div key={e.noiseEventId} className="pending-item">
                <div className="pending-left">
                  <div className="pending-topline">
                    <span className="prio">#{idx + 1}</span>

                    <span className="pending-loc">
                      <MapPin className="size-4" />
                      {e.upperHouseDong}동 {e.upperHouseHo}호 ↔ {e.lowerHouseDong}동{" "}
                      {e.lowerHouseHo}호
                    </span>

                    <span className={`tz-pill ${e.timeZone === "야간" ? "tz-night" : "tz-day"}`}>
                      {e.timeZone ?? "-"}
                    </span>

                    <span className="db-pill">
                      추정 <b>{e.soundLevel}</b>dB
                    </span>
                  </div>

                  <div className="pending-subline">
                    <span>{formatDateTime(e.occurredAt)}</span>
                    <span>반복 {e.repeatCount}회</span>
                    <span className="pending-mini">{e.noisePattern1 ?? "-"}</span>
                  </div>
                </div>

                <div className="pending-actions">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => onOpenDetail?.(e.noiseEventId)}
                  >
                    <Eye className="size-4 mr-1" />
                    상세
                  </Button>
                </div>
              </div>
            );
          })}
        </div>
        {totalPages > 1 && (
          <div className="pending-footer">
            <button className="pending-arrow" disabled={page === 0} onClick={onPrev}>
              <ChevronLeft size={18} />
            </button>

            <div className="pending-pageinfo">
              {page + 1} / {totalPages}
            </div>

            <button className="pending-arrow" disabled={page + 1 >= totalPages} onClick={onNext}>
              <ChevronRight size={18} />
            </button>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
