import { Card, CardContent, CardHeader, CardTitle } from "../../../components/ui/card";
import { Badge } from "../../../components/ui/badge";
import { Button } from "../../../components/ui/button";
import { AlertCircle, MapPin, Eye, Bell } from "lucide-react";

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
          {pendingEvents.map((e, idx) => (
            <div key={e.noiseEventId} className="pending-item">
              <div className="pending-left">
                <div className="pending-topline">
                  <span className="prio">#{idx + 1}</span>

                  <span className="pending-loc">
                    <MapPin className="size-4" />
                    {e.upperHouseDong}동 {e.upperHouseHo}호 ↔ {e.lowerHouseDong}동 {e.lowerHouseHo}
                    호
                  </span>

                  <span className={`tz-pill ${e.timeZone === "야간" ? "tz-night" : "tz-day"}`}>
                    {e.timeZone ?? "-"}
                  </span>

                  <span className="db-pill">
                    추정 <b>{e.soundLevel}</b>dB
                  </span>
                </div>

                <div className="pending-subline">
                  <span>{formatDateTime(e.createdAt)}</span>
                  <span>반복 {e.repeatCount}회</span>
                  <span>{e.noisePattern1 ?? "-"}</span>
                </div>
              </div>

              <div className="pending-actions">
                <Button size="sm" variant="outline" onClick={() => onOpenDetail?.(e.noiseEventId)}>
                  <Eye className="size-4 mr-1" /> 상세
                </Button>

                {/* 빠른 액션은 "원하면"만. 너희 시나리오 맞춰서 사용 */}
                {onQuickObserve && (
                  <Button
                    size="sm"
                    className="btn-observe"
                    onClick={() => onQuickObserve(e.noiseEventId)}
                  >
                    관찰
                  </Button>
                )}

                {onQuickNotify && (
                  <Button
                    size="sm"
                    className="btn-notify"
                    onClick={() => onQuickNotify(e.noiseEventId)}
                  >
                    <Bell className="size-4 mr-1" /> 알림
                  </Button>
                )}
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
