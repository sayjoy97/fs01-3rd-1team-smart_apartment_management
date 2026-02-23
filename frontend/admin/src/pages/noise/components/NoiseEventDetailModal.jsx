import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../../components/ui/dialog";
import { Button } from "../../../components/ui/button";
import { Badge } from "../../../components/ui/badge";

export default function NoiseEventDetailModal({
  open,
  onOpenChange,
  loading,
  detail,
  adminMemo,
  setAdminMemo,
  onStartObserving,
  onSendNotification,
  onRegisterHabitual,
}) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>이벤트 상세</DialogTitle>
        </DialogHeader>

        {loading ? (
          <div className="py-8 text-center text-sm text-gray-500">로딩 중...</div>
        ) : !detail ? (
          <div className="py-8 text-center text-sm text-gray-500">상세 데이터가 없습니다.</div>
        ) : (
          <>
            <div className="space-y-2 text-sm">
              <div className="flex items-center gap-2">
                <span className="font-semibold">
                  {detail.upperHouseDong}동 {detail.upperHouseHo}호 ↔ {detail.lowerHouseDong}동{" "}
                  {detail.lowerHouseHo}호
                </span>
                <Badge className={detail.timeZone === "야간" ? "bg-indigo-500" : "bg-orange-500"}>
                  {detail.timeZone}
                </Badge>
                {detail.habitual && <Badge className="bg-red-600 text-white">[상습]</Badge>}
              </div>

              <p>
                <b>발생:</b>{" "}
                {detail.occurredAt ? new Date(detail.occurredAt).toLocaleString("ko-KR") : "-"}
              </p>
              <p>
                <b>센서:</b> {detail.sensorType}
              </p>
              <p>
                <b>dB:</b> {detail.soundLevel}
              </p>
              <p>
                <b>반복:</b> {detail.repeatCount}
              </p>
              <p>
                <b>분석:</b> {detail.analysisNote}
              </p>
              <p>
                <b>상태:</b> {detail.status}
              </p>
            </div>

            <div className="mt-3">
              <div className="text-sm text-gray-600 mb-1">관리자 메모(선택)</div>
              <textarea
                className="w-full border rounded-lg p-2 text-sm"
                rows={3}
                value={adminMemo}
                onChange={(e) => setAdminMemo(e.target.value)}
                placeholder="관찰/알림/상습 등록 시 남길 메모를 입력하세요."
              />
            </div>

            <div className="mt-4 flex justify-end gap-2">
              {detail.status === "UNPROCESSED" && (
                <Button onClick={onStartObserving}>관찰 시작</Button>
              )}

              <Button onClick={onSendNotification} className="bg-orange-500 hover:bg-orange-600">
                알림 발송
              </Button>

              {detail.canRegisterHabitual && (
                <Button
                  variant="outline"
                  className="border-red-600 text-red-600 hover:bg-red-50"
                  onClick={onRegisterHabitual}
                >
                  상습 구간 등록
                </Button>
              )}

              <Button variant="outline" onClick={() => onOpenChange(false)}>
                닫기
              </Button>
            </div>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
