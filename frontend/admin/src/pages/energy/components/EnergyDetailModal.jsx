import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../../components/ui/dialog";

import { Card, CardContent } from "../../../components/ui/card";
import { Badge } from "../../../components/ui/badge";
import { Button } from "../../../components/ui/button";

import { Power } from "lucide-react";

function EnergyDetailModal({ open, onClose, detail, onControl }) {
  if (!detail) return null;

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-3xl">
        <DialogHeader>
          <DialogTitle>설비 상세 정보</DialogTitle>
        </DialogHeader>

        <div className="space-y-4">
          <Card>
            <CardContent className="space-y-2 pt-4">
              <p>
                <strong>설비명:</strong> {detail.deviceName}
              </p>
              <p>
                <strong>위치:</strong> {detail.location}
              </p>
              <p>
                <strong>설비 유형:</strong> {detail.deviceType}
              </p>
              <p>
                <strong>운영 상태:</strong> <Badge>{detail.isOperating ? "ON" : "OFF"}</Badge>
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="space-y-2 pt-4">
              <p>
                <strong>상태:</strong> {detail.status}
              </p>
              <p>
                <strong>월간 낭비량:</strong> {detail.estimatedWasteKwh} kWh
              </p>
              <p>
                <strong>변화율:</strong> {detail.monthChangeRate}%
              </p>
              <p>
                <strong>예상 절감액:</strong> ₩{detail.estimatedWasteCost?.toLocaleString()}
              </p>
            </CardContent>
          </Card>

          {detail.causeEstimate && (
            <Card>
              <CardContent className="pt-4">
                <p>
                  <strong>예상 원인:</strong>
                </p>
                <p>{detail.causeEstimate}</p>
              </CardContent>
            </Card>
          )}

          <Button onClick={() => onControl(detail.deviceId, !detail.isOperating)}>
            <Power className="size-4 mr-2" />
            {detail.isOperating ? "OFF 제어" : "ON 제어"}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}

export default EnergyDetailModal;
