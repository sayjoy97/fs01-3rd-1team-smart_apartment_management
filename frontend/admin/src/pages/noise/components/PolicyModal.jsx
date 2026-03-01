import { useMemo, useState, useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../../components/ui/dialog";
import { Button } from "../../../components/ui/button";
import { Input } from "../../../components/ui/input";
import { Label } from "../../../components/ui/label";

function toTimeHHmm(t) {
  if (!t) return "";
  return String(t).slice(0, 5);
}
function toTimeHHmmss(t) {
  if (!t) return null;
  const s = String(t);
  return s.length === 5 ? `${s}:00` : s;
}

const DEFAULT_FORM = {
  policyName: "",
  dayStartTime: "06:00",
  nightStartTime: "22:00",
  soundLimit: 70,
  repeatLimit: 5,
  timeThreshold: 10,
};

function buildForm(policy) {
  if (!policy) return { ...DEFAULT_FORM };
  return {
    policyName: policy.policyName ?? "",
    dayStartTime: toTimeHHmm(policy.dayStartTime) || "06:00",
    nightStartTime: toTimeHHmm(policy.nightStartTime) || "22:00",
    soundLimit: Number(policy.soundLimit ?? 70),
    repeatLimit: Number(policy.repeatLimit ?? 5),
    timeThreshold: Number(policy.timeThreshold ?? 10),
  };
}

export default function PolicyModal({ open, onOpenChange, loading, policy, onSave }) {
  // 최초 마운트 때만 초기값 세팅 (리셋은 부모 key가 담당)
  const [form, setForm] = useState(() => buildForm(policy));

  useEffect(() => {
    if (!open) return;
    setForm(buildForm(policy));
  }, [open, policy]);

  const canSave = useMemo(() => {
    if (!form.policyName?.trim()) return false;
    if (!form.dayStartTime || !form.nightStartTime) return false;
    if (Number.isNaN(Number(form.soundLimit))) return false;
    if (Number.isNaN(Number(form.repeatLimit))) return false;
    if (Number.isNaN(Number(form.timeThreshold))) return false;
    return true;
  }, [form]);

  const handleSave = async () => {
    const payload = {
      policyName: form.policyName.trim(),
      dayStartTime: toTimeHHmmss(form.dayStartTime),
      nightStartTime: toTimeHHmmss(form.nightStartTime),
      soundLimit: Number(form.soundLimit),
      repeatLimit: Number(form.repeatLimit),
      timeThreshold: Number(form.timeThreshold),
      // isActive: true,
    };
    await onSave?.(payload);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-xl">
        <DialogHeader>
          <DialogTitle>정책 설정</DialogTitle>
        </DialogHeader>

        {loading ? (
          <div className="py-8 text-center text-sm text-gray-500">로딩 중...</div>
        ) : (
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>정책 이름</Label>
              <Input
                value={form.policyName}
                onChange={(e) => setForm((p) => ({ ...p, policyName: e.target.value }))}
                placeholder="예) 기본 정책 v2"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-2">
                <Label>주간 시작</Label>
                <Input
                  type="time"
                  value={form.dayStartTime}
                  onChange={(e) => setForm((p) => ({ ...p, dayStartTime: e.target.value }))}
                />
              </div>
              <div className="space-y-2">
                <Label>야간 시작</Label>
                <Input
                  type="time"
                  value={form.nightStartTime}
                  onChange={(e) => setForm((p) => ({ ...p, nightStartTime: e.target.value }))}
                />
              </div>
            </div>

            <div className="grid grid-cols-3 gap-3">
              <div className="space-y-2">
                <Label>강도 기준(dB)</Label>
                <Input
                  type="number"
                  min={0}
                  max={120}
                  value={form.soundLimit}
                  onChange={(e) => setForm((p) => ({ ...p, soundLimit: e.target.value }))}
                />
              </div>

              <div className="space-y-2">
                <Label>반복 기준(회)</Label>
                <Input
                  type="number"
                  min={1}
                  max={50}
                  value={form.repeatLimit}
                  onChange={(e) => setForm((p) => ({ ...p, repeatLimit: e.target.value }))}
                />
              </div>

              <div className="space-y-2">
                <Label>시간 창(분)</Label>
                <Input
                  type="number"
                  min={1}
                  max={120}
                  value={form.timeThreshold}
                  onChange={(e) => setForm((p) => ({ ...p, timeThreshold: e.target.value }))}
                />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button variant="outline" onClick={() => onOpenChange(false)}>
                취소
              </Button>
              <Button disabled={!canSave} onClick={handleSave}>
                저장/적용
              </Button>
            </div>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}
