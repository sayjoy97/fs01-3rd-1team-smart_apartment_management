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
  onSendNotification,
  onEndMonitoring,
}) {
  const [memo, setMemo] = useState("");

  const s = statusBadge(detail?.status);

  const headerText = useMemo(() => {
    if (!detail) return "";
    return `${detail.upperHouseDong}동 ${detail.upperHouseHo}호(상층) ↔ ${detail.lowerHouseDong}동 ${detail.lowerHouseHo}호(하층)`;
  }, [detail]);

  const dailyData = useMemo(() => {
    const arr = Array.isArray(detail?.dailyCounts) ? detail.dailyCounts : [];
    return arr.map((x) => ({
      date: String(x.date ?? "").slice(5),
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

  const latestNoiseEventId = useMemo(() => timeline?.[0]?.noiseEventId ?? null, [timeline]);

  const handleClose = () => {
    setMemo("");
    onClose?.();
  };

  if (!open) return null;

  return (
    <Dialog
      open={open}
      onOpenChange={(nextOpen) => {
        if (!nextOpen) handleClose();
      }}
    >
      {/* ✅ 모달 폭 확장 + 내부 스크롤 */}
      <DialogContent className="sm:max-w-[1000px] max-w-[90vw] max-h-[90vh] overflow-y-auto">
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
          <div className="flex flex-col gap-4 mt-2">
            {/* ✅ 요약 카드 4개 - 항상 1줄 (grid-cols-4 고정) */}
            <div className="grid grid-cols-4 gap-3">
              <Card>
                <CardHeader className="pb-1 pt-3 px-4">
                  <CardTitle className="text-xs text-gray-500">최근 30일 발생</CardTitle>
                </CardHeader>
                <CardContent className="px-4 pb-3 text-2xl font-bold text-red-600">
                  {Number(detail.eventCount30d ?? 0)}회
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-1 pt-3 px-4">
                  <CardTitle className="text-xs text-gray-500">평균 강도</CardTitle>
                </CardHeader>
                <CardContent className="px-4 pb-3 text-2xl font-bold text-orange-600">
                  {Number(detail.avgSoundLevel ?? 0).toFixed(1)} dB
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-1 pt-3 px-4">
                  <CardTitle className="text-xs text-gray-500">등록일</CardTitle>
                </CardHeader>
                <CardContent className="px-4 pb-3 text-sm font-medium text-gray-700">
                  {detail.startedAt ? new Date(detail.startedAt).toLocaleDateString("ko-KR") : "-"}
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-1 pt-3 px-4">
                  <CardTitle className="text-xs text-gray-500">종료일</CardTitle>
                </CardHeader>
                <CardContent className="px-4 pb-3 text-sm font-medium text-gray-700">
                  {detail.endedAt ? new Date(detail.endedAt).toLocaleDateString("ko-KR") : "-"}
                </CardContent>
              </Card>
            </div>

            {/* ✅ 차트 + 타임라인 좌우 2단 레이아웃 */}
            <div className="grid grid-cols-2 gap-4 items-start">
              {/* 왼쪽: 차트 */}
              <Card className="h-full">
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-700 flex items-center gap-2">
                    <TrendingUp className="size-4 text-blue-500" />
                    일별 발생 추이 (최근 14일)
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  {!dailyData.length ? (
                    <div className="py-6 text-center text-sm text-gray-400">데이터가 없습니다.</div>
                  ) : (
                    <ResponsiveContainer width="100%" height={260}>
                      <BarChart
                        data={dailyData}
                        margin={{ top: 4, right: 8, left: -16, bottom: 0 }}
                      >
                        <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                        <XAxis dataKey="date" style={{ fontSize: "11px" }} />
                        <YAxis style={{ fontSize: "11px" }} />
                        <Tooltip />
                        <Bar dataKey="count" name="발생건수" fill="#f87171" radius={[3, 3, 0, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                  )}
                </CardContent>
              </Card>

              {/* 오른쪽: 타임라인 */}
              <Card className="h-full">
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-700 flex items-center gap-2">
                    <Clock className="size-4 text-purple-500" />
                    발생 이력 타임라인
                    <span className="ml-auto text-xs text-gray-400 font-normal">
                      총 {timeline.length}건
                    </span>
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  {!timeline.length ? (
                    <div className="py-6 text-center text-sm text-gray-400">이력이 없습니다.</div>
                  ) : (
                    /* ✅ 차트 높이(260px)에 맞춰 타임라인 높이 통일 */
                    <div className="space-y-2 overflow-y-auto" style={{ maxHeight: "260px" }}>
                      {timeline.map((t, idx) => (
                        <div
                          key={t.noiseEventId ?? idx}
                          className="flex items-start gap-3 p-2.5 rounded-lg border bg-gray-50 hover:bg-gray-100 transition-colors"
                        >
                          <div className="flex-shrink-0 w-2 h-2 mt-1.5 rounded-full bg-red-400" />
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center justify-between gap-2 mb-1">
                              <span className="text-xs text-gray-500">{t.occurredAtStr}</span>
                              <Badge
                                className={`text-xs shrink-0 ${
                                  t.dayNight === "주간"
                                    ? "bg-orange-100 text-orange-700"
                                    : "bg-indigo-100 text-indigo-700"
                                }`}
                              >
                                {t.dayNight}
                              </Badge>
                            </div>
                            <div className="flex items-center gap-2 text-sm">
                              <span className="font-semibold text-orange-600">
                                {Number(t.soundLevel ?? 0)} dB
                              </span>
                              {t.urgentBreak ? (
                                <Badge className="bg-red-100 text-red-700 text-xs">URGENT</Badge>
                              ) : (
                                <Badge className="bg-gray-100 text-gray-600 text-xs">NORMAL</Badge>
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

            {/* ✅ 메모 + 액션 버튼 */}
            <div className="flex flex-col gap-2 pt-1 border-t border-gray-100">
              <label className="text-sm text-gray-600 font-medium">관리자 메모 (선택)</label>
              <textarea
                className="w-full border rounded-lg p-2.5 text-sm resize-none focus:outline-none focus:ring-2 focus:ring-gray-200"
                rows={2}
                value={memo}
                onChange={(e) => setMemo(e.target.value)}
                placeholder="알림/종료 시 남길 메모를 입력하세요."
              />
              <div className="flex gap-2 justify-end mt-1">
                <Button
                  disabled={!latestNoiseEventId}
                  onClick={() => onSendNotification?.({ noiseEventId: latestNoiseEventId, memo })}
                  className="bg-orange-500 hover:bg-orange-600 text-white"
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
                    className="border-red-500 text-red-600 hover:bg-red-50"
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
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}
