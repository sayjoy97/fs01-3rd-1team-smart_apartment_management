// src/pages/energy/components/EnergyDetailModal.jsx
import { useMemo } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../../components/ui/dialog";
import { Card, CardContent, CardHeader, CardTitle } from "../../../components/ui/card";
import { Badge } from "../../../components/ui/badge";
import { Button } from "../../../components/ui/button";

import { Tabs, TabsContent, TabsList, TabsTrigger } from "../../../components/ui/tabs";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../../../components/ui/table";

import { Power, Clock, Info } from "lucide-react";

function statusBadge(status) {
  switch (status) {
    case "CHECK_REQUIRED":
      return { cls: "bg-red-100 text-red-700", label: "점검 권장" };
    case "CHECKING":
      return { cls: "bg-yellow-100 text-yellow-700", label: "점검 중" };
    case "NORMAL":
      return { cls: "bg-green-100 text-green-700", label: "정상" };
    default:
      return { cls: "bg-gray-100 text-gray-700", label: status ?? "-" };
  }
}

export default function EnergyDetailModal({
  open,
  onClose,
  detail,
  loading,
  controlLogs = [],
  savingResults,
  onControl,
}) {
  const s = statusBadge(detail?.deviceStatus);

  const safeLogs = Array.isArray(controlLogs) ? controlLogs : [];

  const savingContent = savingResults?.content ?? [];

  const lastAnalyzed = useMemo(() => {
    const v = detail?.analyzedAt;
    if (!v) return "-";

    const d = new Date(v);
    if (Number.isNaN(d.getTime())) return String(v);

    return d.toLocaleString("ko-KR");
  }, [detail?.analyzedAt]);

  if (!open) return null;

  return (
    <Dialog open={open} onOpenChange={onClose}>
      {/* ✅ 가로 넓은 모달 */}
      <DialogContent className="max-w-5xl max-h-[85vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>설비 상세 정보</DialogTitle>
        </DialogHeader>

        {loading ? (
          <div className="py-10 text-center text-sm text-gray-500">로딩 중...</div>
        ) : !detail ? (
          <div className="py-10 text-center text-sm text-gray-500">상세 데이터가 없습니다.</div>
        ) : (
          <>
            {/* 상단 요약 */}
            <div className="grid grid-cols-3 gap-3">
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-600">기본 정보</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2">
                  <div className="text-sm">
                    <span className="text-gray-500">설비명</span>
                    <div className="font-medium">{detail.deviceName}</div>
                  </div>
                  <div className="text-sm">
                    <span className="text-gray-500">위치</span>
                    <div className="font-medium">{detail.location ?? "-"}</div>
                  </div>
                  <div className="text-sm">
                    <span className="text-gray-500">유형</span>
                    <div className="font-medium">{detail.deviceType ?? "-"}</div>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-600">상태</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2">
                  <div className="flex items-center gap-2">
                    <Badge
                      className={
                        detail.isOperating
                          ? "bg-green-100 text-green-700"
                          : "bg-gray-100 text-gray-700"
                      }
                    >
                      <Power className="size-3 mr-1" />
                      {detail.isOperating ? "ON" : "OFF"}
                    </Badge>

                    <Badge className={s.cls}>{s.label}</Badge>
                  </div>

                  <div className="text-xs text-gray-500 flex items-center gap-2">
                    <Clock className="size-4" />
                    분석 시각: {lastAnalyzed}
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-600">추정 지표</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2">
                  <div className="text-sm">
                    <span className="text-gray-500">월간 낭비량</span>
                    <div className="font-semibold">
                      {Number(detail.estimatedWasteKwh ?? 0).toLocaleString()} kWh
                    </div>
                  </div>
                  <div className="text-sm">
                    <span className="text-gray-500">변화율</span>
                    <div className="font-semibold">
                      {Number(detail.monthChangeRate ?? 0).toLocaleString()}%
                    </div>
                  </div>
                  <div className="text-sm">
                    <span className="text-gray-500">예상 절감액</span>
                    <div className="font-semibold">
                      ₩{Number(detail.estimatedWasteCost ?? 0).toLocaleString()}
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* 추천/효과 메시지 */}
            <div className="mt-3 grid grid-cols-2 gap-3">
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-600">권장 조치</CardTitle>
                </CardHeader>
                <CardContent className="text-sm text-gray-700">
                  {detail.recommendedAction ?? "권장 조치가 없습니다."}
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-gray-600">예상 효과</CardTitle>
                </CardHeader>
                <CardContent className="text-sm text-gray-700">
                  {detail.expectedEffectMessage ?? "예상 효과 메시지가 없습니다."}
                </CardContent>
              </Card>
            </div>

            {/* 예상 원인 */}
            {detail.causeEstimate && (
              <div className="mt-3">
                <Card>
                  <CardHeader className="pb-2">
                    <CardTitle className="text-sm text-gray-600 flex items-center gap-2">
                      <Info className="size-4" />
                      예상 원인(추정)
                    </CardTitle>
                  </CardHeader>
                  <CardContent className="text-sm text-gray-700">
                    {detail.causeEstimate}
                  </CardContent>
                </Card>
              </div>
            )}

            {/* 탭: 운영이력 / 절감성과 */}
            <div className="mt-4">
              <Tabs defaultValue="history">
                <TabsList>
                  <TabsTrigger value="history">운영 이력</TabsTrigger>
                  <TabsTrigger value="saving">절감 성과</TabsTrigger>
                </TabsList>

                <TabsContent value="history" className="mt-3">
                  <Card>
                    <CardHeader className="pb-2">
                      <CardTitle className="text-sm text-gray-600">최근 제어 로그</CardTitle>
                    </CardHeader>
                    <CardContent>
                      {!safeLogs.length ? (
                        <div className="py-6 text-center text-sm text-gray-500">
                          로그가 없습니다.
                        </div>
                      ) : (
                        <div className="border rounded-lg overflow-auto">
                          <Table>
                            <TableHeader>
                              <TableRow>
                                <TableHead>작업</TableHead>
                                <TableHead>시간</TableHead>
                                <TableHead>사유</TableHead>
                              </TableRow>
                            </TableHeader>
                            <TableBody>
                              {safeLogs.map((log, idx) => {
                                const after = Boolean(log.afterState);
                                const when = log.controlledAt
                                  ? new Date(log.controlledAt).toLocaleString("ko-KR")
                                  : "-";
                                return (
                                  <TableRow key={idx}>
                                    <TableCell>
                                      <Badge
                                        className={
                                          after
                                            ? "bg-green-100 text-green-700"
                                            : "bg-gray-100 text-gray-700"
                                        }
                                      >
                                        <Power className="size-3 mr-1" />
                                        {after ? "켜기" : "끄기"}
                                      </Badge>
                                    </TableCell>
                                    <TableCell>{when}</TableCell>
                                    <TableCell>{log.reason ?? "-"}</TableCell>
                                  </TableRow>
                                );
                              })}
                            </TableBody>
                          </Table>
                        </div>
                      )}
                    </CardContent>
                  </Card>
                </TabsContent>

                <TabsContent value="saving" className="mt-3">
                  <Card>
                    <CardHeader className="pb-2">
                      <CardTitle className="text-sm text-gray-600">절감 결과</CardTitle>
                    </CardHeader>
                    <CardContent>
                      {!savingContent?.length ? (
                        <div className="py-6 text-center text-sm text-gray-500">
                          절감 결과 데이터가 없습니다.
                        </div>
                      ) : (
                        <div className="border rounded-lg overflow-auto">
                          <Table>
                            <TableHeader>
                              <TableRow>
                                <TableHead>기간</TableHead>
                                <TableHead>절감(kWh)</TableHead>
                                <TableHead>절감액(₩)</TableHead>
                                <TableHead>생성일</TableHead>
                              </TableRow>
                            </TableHeader>
                            <TableBody>
                              {savingContent.map((r, idx) => (
                                <TableRow key={idx}>
                                  <TableCell>{r.periodLabel ?? "-"}</TableCell>
                                  <TableCell>{Number(r.savedKwh ?? 0).toLocaleString()}</TableCell>
                                  <TableCell>{Number(r.savedCost ?? 0).toLocaleString()}</TableCell>
                                  <TableCell>
                                    {r.createdAt
                                      ? new Date(r.createdAt).toLocaleString("ko-KR")
                                      : "-"}
                                  </TableCell>
                                </TableRow>
                              ))}
                            </TableBody>
                          </Table>
                        </div>
                      )}
                    </CardContent>
                  </Card>
                </TabsContent>
              </Tabs>
            </div>

            {/* 하단 제어 버튼 */}
            <div className="mt-4 flex justify-end gap-2">
              <Button
                variant="outline"
                onClick={() => {
                  const next = !detail.isOperating;
                  if (
                    !window.confirm(
                      `${detail.deviceName}을(를) ${next ? "켜기" : "끄기"} 하시겠습니까?`,
                    )
                  )
                    return;
                  onControl?.(detail.deviceId, next);
                }}
              >
                <Power className="size-4 mr-2" />
                {detail.isOperating ? "설비 OFF" : "설비 ON"}
              </Button>

              <Button variant="outline" onClick={onClose}>
                닫기
              </Button>
            </div>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
