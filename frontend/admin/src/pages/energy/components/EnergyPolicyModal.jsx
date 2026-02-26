// src/pages/energy/components/EnergyPolicyModal.jsx
import { useEffect, useMemo, useState } from "react";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "../../../components/ui/dialog";
import { Button } from "../../../components/ui/button";
import { Input } from "../../../components/ui/input";
import { Label } from "../../../components/ui/label";
import { Card, CardContent } from "../../../components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../../../components/ui/select";

import { getActiveEnergyPolicy, createEnergyPolicy } from "../../../api/energyAPI";

// ✅ 백엔드 enum 그대로 (CompareBase)
const COMPARE_BASE = {
  DEVICE_PAST_AVG: "동일 설비 과거 평균",
  DEVICE_TYPE_AVG: "동일 설비 유형 평균",
  TIME_SLOT_AVG: "시간대별 평균",
};

function toHHmmss(v) {
  if (!v) return "00:00:00";
  const s = String(v);
  if (/^\d{2}:\d{2}:\d{2}$/.test(s)) return s;
  if (/^\d{2}:\d{2}$/.test(s)) return `${s}:00`;
  const m = s.match(/^(\d{2}):(\d{2}):(\d{2})/);
  if (m) return `${m[1]}:${m[2]}:${m[3]}`;
  return "00:00:00";
}

function defaultPolicy() {
  return {
    sensitivityPercent: 10,
    warningPercent: 25,
    compareBase: "DEVICE_PAST_AVG",
    idleStartTime: "00:00:00",
    idleEndTime: "06:00:00",
    repeatLimit: 3,
    repeatWindowHours: 24,
    ignoreSingleBreach: true,
    alertWarning: true,
    alertCheck: true,
    costPerKwh: 200,
    wasteThresholdKwh: 2.0,
  };
}

export default function EnergyPolicyModal({ open, onClose, onSaved }) {
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState(defaultPolicy());

  const compareBaseLabel = useMemo(() => COMPARE_BASE[form.compareBase] ?? "-", [form.compareBase]);

  const patch = (key, value) => setForm((prev) => ({ ...prev, [key]: value }));

  // ✅ 모달 열릴 때만 active 정책 로드
  useEffect(() => {
    if (!open) return;

    const run = async () => {
      setLoading(true);
      try {
        const res = await getActiveEnergyPolicy(); // {success,data,error}
        if (res?.success && res.data) {
          const p = res.data;
          setForm({
            sensitivityPercent: p.sensitivityPercent ?? 10,
            warningPercent: p.warningPercent ?? 25,
            compareBase: p.compareBase ?? "DEVICE_PAST_AVG",
            idleStartTime: toHHmmss(p.idleStartTime ?? "00:00:00"),
            idleEndTime: toHHmmss(p.idleEndTime ?? "06:00:00"),
            repeatLimit: p.repeatLimit ?? 3,
            repeatWindowHours: p.repeatWindowHours ?? 24,
            ignoreSingleBreach: Boolean(p.ignoreSingleBreach),
            alertWarning: Boolean(p.alertWarning),
            alertCheck: Boolean(p.alertCheck),
            costPerKwh: p.costPerKwh ?? 200,
            wasteThresholdKwh: typeof p.wasteThresholdKwh === "number" ? p.wasteThresholdKwh : 2.0,
          });
        } else {
          setForm(defaultPolicy());
        }
      } catch (e) {
        console.error("활성 정책 로딩 실패:", e);
        setForm(defaultPolicy());
      } finally {
        setLoading(false);
      }
    };

    run();
  }, [open]);

  const onSubmit = async () => {
    const body = {
      sensitivityPercent: Number(form.sensitivityPercent),
      warningPercent: Number(form.warningPercent),
      compareBase: form.compareBase,
      idleStartTime: toHHmmss(form.idleStartTime),
      idleEndTime: toHHmmss(form.idleEndTime),
      repeatLimit: Number(form.repeatLimit),
      repeatWindowHours: Number(form.repeatWindowHours),
      ignoreSingleBreach: Boolean(form.ignoreSingleBreach),
      alertWarning: Boolean(form.alertWarning),
      alertCheck: Boolean(form.alertCheck),
      costPerKwh: Number(form.costPerKwh),
      wasteThresholdKwh: Number(form.wasteThresholdKwh),
    };

    setSaving(true);
    try {
      const res = await createEnergyPolicy(body); // {success,data,error}
      if (res?.success) {
        onSaved?.(res.data);
        onClose?.();
      } else {
        console.error("정책 저장 실패(응답):", res);
        alert("정책 저장에 실패했습니다. (서버 응답 success=false)");
      }
    } catch (e) {
      console.error("정책 저장 실패:", e);
      alert("정책 저장에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  };

  if (!open) return null;

  return (
    <Dialog
      open={open}
      onOpenChange={(nextOpen) => {
        // ✅ Overlay/ESC로 닫힐 때도 정상 동작
        if (!nextOpen) onClose?.();
      }}
    >
      <DialogContent className="sm:max-w-2xl max-h-[65vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>에너지 관리 정책 설정</DialogTitle>
          <DialogDescription>
            시스템 분석 기준 파라미터를 설정합니다 (관리자 전용)
          </DialogDescription>
        </DialogHeader>

        {loading ? (
          <div className="py-10 text-center text-sm text-gray-500">로딩 중...</div>
        ) : (
          <div className="space-y-4">
            {/* 1) 임계값 */}
            <Card>
              <CardContent className="pt-4 space-y-3">
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>민감 임계(%)</Label>
                    <Input
                      type="number"
                      value={form.sensitivityPercent}
                      onChange={(e) => patch("sensitivityPercent", e.target.value)}
                    />
                    <p className="text-xs text-gray-500">초과 시 이상 징후로 판단</p>
                  </div>

                  <div className="space-y-2">
                    <Label>경고 임계(%)</Label>
                    <Input
                      type="number"
                      value={form.warningPercent}
                      onChange={(e) => patch("warningPercent", e.target.value)}
                    />
                    <p className="text-xs text-gray-500">초과 시 점검 권장 후보</p>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>낭비 임계값(kWh)</Label>
                    <Input
                      type="number"
                      step="0.1"
                      value={form.wasteThresholdKwh}
                      onChange={(e) => patch("wasteThresholdKwh", e.target.value)}
                    />
                    <p className="text-xs text-gray-500">월간 추정 낭비량 기준</p>
                  </div>

                  <div className="space-y-2">
                    <Label>kWh 단가(원)</Label>
                    <Input
                      type="number"
                      value={form.costPerKwh}
                      onChange={(e) => patch("costPerKwh", e.target.value)}
                    />
                    <p className="text-xs text-gray-500">절감액 계산에 사용</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* 2) 비운영 시간 + 반복감지 */}
            <Card>
              <CardContent className="pt-4 space-y-3">
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>비운영 시작</Label>
                    <Input
                      type="time"
                      value={String(form.idleStartTime).slice(0, 5)}
                      onChange={(e) => patch("idleStartTime", toHHmmss(e.target.value))}
                    />
                  </div>

                  <div className="space-y-2">
                    <Label>비운영 종료</Label>
                    <Input
                      type="time"
                      value={String(form.idleEndTime).slice(0, 5)}
                      onChange={(e) => patch("idleEndTime", toHHmmss(e.target.value))}
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>반복 감지 횟수</Label>
                    <Input
                      type="number"
                      min="1"
                      value={form.repeatLimit}
                      onChange={(e) => patch("repeatLimit", e.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>반복 감지 윈도우(시간)</Label>
                    <Input
                      type="number"
                      min="1"
                      value={form.repeatWindowHours}
                      onChange={(e) => patch("repeatWindowHours", e.target.value)}
                    />
                  </div>
                </div>

                <div className="flex items-center justify-between rounded-lg border p-3">
                  <div>
                    <div className="font-medium">단발 초과 무시</div>
                    <div className="text-sm text-gray-500">1회만 초과하면 판단에서 제외</div>
                  </div>
                  <input
                    type="checkbox"
                    className="size-5"
                    checked={Boolean(form.ignoreSingleBreach)}
                    onChange={(e) => patch("ignoreSingleBreach", e.target.checked)}
                  />
                </div>
              </CardContent>
            </Card>

            {/* 3) 비교 기준 */}
            <Card>
              <CardContent className="pt-4 space-y-2">
                <Label>비교 기준</Label>
                <Select value={form.compareBase} onValueChange={(v) => patch("compareBase", v)}>
                  <SelectTrigger className="w-full">
                    <SelectValue placeholder={compareBaseLabel} />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="DEVICE_PAST_AVG">{COMPARE_BASE.DEVICE_PAST_AVG}</SelectItem>
                    <SelectItem value="DEVICE_TYPE_AVG">{COMPARE_BASE.DEVICE_TYPE_AVG}</SelectItem>
                    <SelectItem value="TIME_SLOT_AVG">{COMPARE_BASE.TIME_SLOT_AVG}</SelectItem>
                  </SelectContent>
                </Select>
                <p className="text-xs text-gray-500">과다 사용 판단 시 “무엇과 비교할지” 선택</p>
              </CardContent>
            </Card>

            {/* 4) 알림 */}
            <Card>
              <CardContent className="pt-4 space-y-3">
                <div className="flex items-center justify-between rounded-lg border p-3">
                  <div>
                    <div className="font-medium">경고 알림(alertWarning)</div>
                    <div className="text-sm text-gray-500">warningPercent 초과 시</div>
                  </div>
                  <input
                    type="checkbox"
                    className="size-5"
                    checked={Boolean(form.alertWarning)}
                    onChange={(e) => patch("alertWarning", e.target.checked)}
                  />
                </div>

                <div className="flex items-center justify-between rounded-lg border p-3">
                  <div>
                    <div className="font-medium">점검 알림(alertCheck)</div>
                    <div className="text-sm text-gray-500">점검 권장 상태 진입 시</div>
                  </div>
                  <input
                    type="checkbox"
                    className="size-5"
                    checked={Boolean(form.alertCheck)}
                    onChange={(e) => patch("alertCheck", e.target.checked)}
                  />
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        <DialogFooter>
          <Button variant="outline" onClick={onClose} disabled={saving}>
            취소
          </Button>
          <Button onClick={onSubmit} disabled={saving || loading}>
            {saving ? "저장 중..." : "저장"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
