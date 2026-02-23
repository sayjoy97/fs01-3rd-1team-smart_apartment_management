import { useMemo, useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../../components/ui/dialog";
import { Card, CardContent, CardHeader, CardTitle } from "../../../components/ui/card";
import { Badge } from "../../../components/ui/badge";
import { Button } from "../../../components/ui/button";

import { Bell, XCircle, MapPin, TrendingUp, Clock } from "lucide-react";
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip } from "recharts";

function statusBadge(status) {
  if (status === "MONITORING") return { cls: "bg-red-100 text-red-700", label: "모니터링 중" };
  if (status === "CLOSED") return { cls: "bg-gray-100 text-gray-700", label: "종료" };
  return { cls: "bg-gray-100 text-gray-700", label: status ?? "-" };
}

function toKoreanDayNightLabel(dt) {
  const d = new Date(dt);
  const hour = d.getHours();
  return hour >= 6 && hour < 22 ? "주간" : "야간";
}

export default function HabitualDetailModal({
  open,
  onClose,
  loading,
  detail,
  onSendNotification, // ✅ (payload) => Promise
  onEndMonitoring, // ✅ (payload) => Promise
}) {
  const [memo, setMemo] = useState("");

  const s = statusBadge(detail?.status);

  const headerText = useMemo(() => {
    if (!detail) return "";
    return `${detail.upperHouseDong}동 ${detail.upperHouseHo}호(상층) ↔ ${detail.lowerHouseDong}동 ${detail.lowerHouseHo}호(하층)`;
  }, [detail]);

  // ✅ React Compiler 경고 피하려고 의존성은 detail로 통일
  const dailyData = useMemo(() => {
    const arr = Array.isArray(detail?.dailyCounts) ? detail.dailyCounts : [];
    return arr.map((x) => ({
      date: String(x.date ?? "").slice(5), // MM-DD
      count: Number(x.count ?? 0),
    }));
  }, [detail]);

  const timeline = useMemo(() => {
    const arr = Array.isArray(detail?.timeline) ? detail.timeline : [];
    const mapped = arr.map((t) => {
      const occurredAt = t.occurredAt ?? t.createdAt ?? t.time ?? null;
      const occurredAtStr = occurredAt ? new Date(occurredAt).toLocaleString("ko-KR") : "-";
      const dayNight = occurredAt ? toKoreanDayNightLabel(occurredAt) : "-";
      return { ...t, occurredAt, occurredAtStr, dayNight };
    });

    return mapped.sort((a, b) => {
      const ta = a.occurredAt ? new Date(a.occurredAt).getTime() : 0;
      const tb = b.occurredAt ? new Date(b.occurredAt).getTime() : 0;
      return tb - ta;
    });
  }, [detail]);

  // ✅ “알림 발송”은 상습구간 전용 API가 아니라,
  // 타임라인에서 가장 최근 noiseEventId를 골라 /noise/api/event/{id}/notify 를 호출
  const latestNoiseEventId = useMemo(() => timeline?.[0]?.noiseEventId ?? null, [timeline]);

  const handleClose = () => {
    setMemo(""); // 닫을 때만 초기화
    onClose?.();
  };

  if (!open) return null;

  return (
    <Dialog
      open={open}
      onOpenChange={(nextOpen) => {
        // ✅ shadcn Dialog는 nextOpen=false로 닫힐 때 여기로 들어옴
        if (!nextOpen) handleClose();
        // 열릴 때 초기화가 필요하면 여기서만:
        // else setMemo("");
      }}
    >
      <DialogContent className="max-w-5xl max-h-[85vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <MapPin className="size-5 text-red-500" />
            상습 구간 상세
            {detail?.status && <Badge className={s.cls}>{s.label}</Badge>}
          </DialogTitle>

          {detail && <div className="text-sm text-gray-600 mt-1">{headerText}</div>}
        </DialogHeader>

        {loading ? (
          <div className="py-10 text-center text-sm text-gray-500">로딩 중...</div>
        ) : !detail ? (
          <div className="py-10 text-center text-sm text-gray-500">상세 데이터가 없습니다.</div>
        ) : (
          <>
            {/* 요약 카드 */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-600">최근 30일 발생</CardTitle>
                </CardHeader>
                <CardContent className="text-2xl font-semibold text-red-600">
                  {Number(detail.eventCount30d ?? 0)}회
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-600">평균 강도</CardTitle>
                </CardHeader>
                <CardContent className="text-2xl font-semibold text-orange-600">
                  {Number(detail.avgSoundLevel ?? 0).toFixed(1)} dB
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-600">등록일</CardTitle>
                </CardHeader>
                <CardContent className="text-sm font-medium">
                  {detail.startedAt ? new Date(detail.startedAt).toLocaleDateString("ko-KR") : "-"}
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-600">종료일</CardTitle>
                </CardHeader>
                <CardContent className="text-sm font-medium">
                  {detail.endedAt ? new Date(detail.endedAt).toLocaleDateString("ko-KR") : "-"}
                </CardContent>
              </Card>
            </div>

            {/* 14일 차트 */}
            <div className="mt-4">
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-700 flex items-center gap-2">
                    <TrendingUp className="size-4 text-blue-500" />
                    일별 발생 추이 (최근 14일)
                  </CardTitle>
                </CardHeader>

                <CardContent>
                  {!dailyData.length ? (
                    <div className="py-6 text-center text-sm text-gray-500">데이터가 없습니다.</div>
                  ) : (
                    <ResponsiveContainer width="100%" height={220}>
                      <BarChart data={dailyData}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="date" style={{ fontSize: "12px" }} />
                        <YAxis style={{ fontSize: "12px" }} />
                        <Tooltip />
                        <Bar dataKey="count" name="발생건수" />
                      </BarChart>
                    </ResponsiveContainer>
                  )}
                </CardContent>
              </Card>
            </div>

            {/* 타임라인 */}
            <div className="mt-4">
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-700 flex items-center gap-2">
                    <Clock className="size-4 text-purple-500" />
                    발생 이력 타임라인
                  </CardTitle>
                </CardHeader>

                <CardContent>
                  {!timeline.length ? (
                    <div className="py-6 text-center text-sm text-gray-500">이력이 없습니다.</div>
                  ) : (
                    <div className="space-y-3 max-h-80 overflow-y-auto">
                      {timeline.map((t, idx) => (
                        <div
                          key={t.noiseEventId ?? idx}
                          className="flex items-start gap-3 p-3 rounded-lg border bg-gray-50"
                        >
                          <div className="flex-shrink-0 w-2 h-2 mt-2 rounded-full bg-red-500" />
                          <div className="flex-1">
                            <div className="flex items-center justify-between mb-1">
                              <span className="text-sm font-medium">{t.occurredAtStr}</span>
                              <Badge
                                className={
                                  t.dayNight === "주간" ? "bg-orange-500" : "bg-indigo-500"
                                }
                              >
                                {t.dayNight}
                              </Badge>
                            </div>

                            <div className="flex items-center gap-3 text-sm text-gray-600">
                              <span className="font-semibold text-orange-600">
                                {Number(t.soundLevel ?? 0)} dB
                              </span>
                              {t.urgentBreak ? (
                                <Badge className="bg-red-100 text-red-700">URGENT</Badge>
                              ) : (
                                <Badge className="bg-gray-100 text-gray-700">NORMAL</Badge>
                              )}
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>

            {/* 메모 + 버튼 */}
            <div className="mt-4 flex flex-col gap-2">
              <div className="text-sm text-gray-600">관리자 메모(선택)</div>
              <textarea
                className="w-full border rounded-lg p-2 text-sm"
                rows={3}
                value={memo}
                onChange={(e) => setMemo(e.target.value)}
                placeholder="알림/종료 시 남길 메모를 입력하세요."
              />

              <div className="mt-2 flex flex-col sm:flex-row gap-2 justify-end">
                <Button
                  disabled={!latestNoiseEventId}
                  onClick={() => onSendNotification?.({ noiseEventId: latestNoiseEventId, memo })}
                  className="bg-orange-500 hover:bg-orange-600"
                  title={
                    !latestNoiseEventId ? "타임라인에 이벤트가 없어 알림을 보낼 수 없습니다." : ""
                  }
                >
                  <Bell className="size-4 mr-2" />
                  알림 발송
                </Button>

                {detail.status === "MONITORING" && (
                  <Button
                    variant="outline"
                    className="border-red-600 text-red-600 hover:bg-red-50"
                    onClick={() => onEndMonitoring?.({ zoneId: detail.zoneId, memo })}
                  >
                    <XCircle className="size-4 mr-2" />
                    모니터링 종료
                  </Button>
                )}

                <Button variant="outline" onClick={handleClose}>
                  닫기
                </Button>
              </div>
            </div>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
