import { Card, CardContent } from "../../../components/ui/card";
import { Activity, AlertTriangle, Clock, Moon, Sun as SunIcon } from "lucide-react";

function hhmmToMin(hhmm) {
  if (!hhmm) return null;
  const [h, m] = String(hhmm).slice(0, 5).split(":").map(Number);
  if (Number.isNaN(h) || Number.isNaN(m)) return null;
  return h * 60 + m;
}

function isDayNow(activePolicy) {
  const dayStart = hhmmToMin(activePolicy?.dayStartTime) ?? 6 * 60;
  const nightStart = hhmmToMin(activePolicy?.nightStartTime) ?? 22 * 60;

  const now = new Date();
  const nowMin = now.getHours() * 60 + now.getMinutes();

  // 일반 케이스(06~22)
  if (dayStart < nightStart) return nowMin >= dayStart && nowMin < nightStart;

  // 혹시 wrap 케이스(예: 22~06) 대비
  return nowMin >= dayStart || nowMin < nightStart;
}

export default function NoiseDashboardCards({ dashboard, loading, activePolicy }) {
  const todayEventCount = dashboard?.todayEventCount ?? 0;
  const policyBreakCount = dashboard?.todayPolicyBreakCount ?? 0;
  const urgentCount = dashboard?.pendingEventCount ?? 0;

  const day = isDayNow(activePolicy);

  return (
    <div className="noise2-cards4">
      <Card className="noise2-card">
        <CardContent className="noise2-card-content">
          <div className="noise2-card-row">
            <div>
              <p className="noise2-card-label">오늘 발생 이벤트</p>
              <div className="noise2-card-value">
                {loading ? "..." : todayEventCount}
                <span className="noise2-card-unit">건</span>
              </div>
            </div>
            <div className="noise2-iconbox noise2-iconbox-blue">
              <Activity className="size-6 text-white" />
            </div>
          </div>
        </CardContent>
      </Card>

      <Card className="noise2-card">
        <CardContent className="noise2-card-content">
          <div className="noise2-card-row">
            <div>
              <p className="noise2-card-label">정책 위반 의심</p>
              <div className="noise2-card-value">
                {loading ? "..." : policyBreakCount}
                <span className="noise2-card-unit">건</span>
              </div>
            </div>
            <div className="noise2-iconbox noise2-iconbox-red">
              <AlertTriangle className="size-6 text-white" />
            </div>
          </div>
        </CardContent>
      </Card>

      <Card className="noise2-card noise2-card-warn">
        <CardContent className="noise2-card-content">
          <div className="noise2-card-row">
            <div>
              <p className="noise2-card-label noise2-card-label-warn">승인 대기 중</p>
              <div className="noise2-card-value">
                {loading ? "..." : urgentCount}
                <span className="noise2-card-unit">건</span>
              </div>
            </div>
            <div className="noise2-iconbox noise2-iconbox-yellow pulse">
              <Clock className="size-6 text-white" />
            </div>
          </div>
        </CardContent>
      </Card>

      <Card className="noise2-card">
        <CardContent className="noise2-card-content">
          <div className="noise2-card-row">
            <div>
              <p className="noise2-card-label">현재 시간대</p>
              <div className="noise2-card-value">{day ? "주간" : "야간"}</div>
              <div className="noise2-card-sub">
                {activePolicy?.dayStartTime && activePolicy?.nightStartTime
                  ? `(${String(activePolicy.dayStartTime).slice(0, 5)} ~ ${String(activePolicy.nightStartTime).slice(0, 5)})`
                  : "(06:00 ~ 22:00)"}
              </div>
            </div>
            <div
              className={`noise2-iconbox ${day ? "noise2-iconbox-orange" : "noise2-iconbox-indigo"}`}
            >
              {day ? (
                <SunIcon className="size-6 text-white" />
              ) : (
                <Moon className="size-6 text-white" />
              )}
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
