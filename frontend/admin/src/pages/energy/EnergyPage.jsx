// src/pages/energy/EnergyPage.jsx
import "./EnergyPage.css";

import { useEffect, useMemo, useState } from "react";
import { Button } from "../../components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";
import { Badge } from "../../components/ui/badge";

import {
  AlertTriangle,
  CheckCircle,
  Clock,
  Eye,
  Power,
  TrendingDown,
  TrendingUp,
  Settings,
  Info,
} from "lucide-react";

import useEnergyPage from "./components/useEnergyPage";
import EnergyDetailModal from "./components/EnergyDetailModal";
import EnergyPolicyModal from "./components/EnergyPolicyModal";

// recharts
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip as RechartsTooltip,
  Legend,
  PieChart,
  Pie,
  Cell,
} from "recharts";

import { getEnergyPattern, getEnergyCategory } from "../../api/energyAPI";

function statusBadge(status) {
  switch (status) {
    case "CHECK_REQUIRED":
      return {
        cls: "bg-red-100 text-red-700",
        icon: <AlertTriangle className="size-3 mr-1" />,
        label: "CHECK_REQUIRED",
      };
    case "CHECKING":
      return {
        cls: "bg-yellow-100 text-yellow-700",
        icon: <Clock className="size-3 mr-1" />,
        label: "CHECKING",
      };
    case "NORMAL":
      return {
        cls: "bg-green-100 text-green-700",
        icon: <CheckCircle className="size-3 mr-1" />,
        label: "NORMAL",
      };
    default:
      return { cls: "bg-gray-100 text-gray-700", icon: null, label: status ?? "-" };
  }
}

// pattern DTO 흡수
function normalizePattern(points) {
  const arr = Array.isArray(points) ? points : [];
  return arr.map((p, idx) => {
    const label =
      p.label ??
      p.timeLabel ??
      p.slotLabel ??
      p.dayLabel ??
      p.monthLabel ??
      p.timeSlot ??
      p.date ??
      p.day ??
      p.month ??
      String(idx + 1);

    const actual =
      p.actual ?? p.real ?? p.actualKwh ?? p.actualUsageKwh ?? p.usedKwh ?? p.usageKwh ?? 0;

    const expected =
      p.expected ?? p.base ?? p.expectedKwh ?? p.expectedUsageKwh ?? p.baselineKwh ?? 0;

    return {
      label: String(label),
      actual: Number(actual) || 0,
      expected: Number(expected) || 0,
    };
  });
}

// category DTO 흡수
function normalizeCategory(slices) {
  const arr = Array.isArray(slices) ? slices : [];
  return arr.map((s, idx) => {
    const name = s.name ?? s.typeName ?? s.category ?? s.deviceType ?? `분류${idx + 1}`;
    const value = s.value ?? s.kwh ?? s.totalKwh ?? s.usageKwh ?? s.amount ?? s.sum ?? 0;

    return {
      name: String(name),
      value: Number(value) || 0,
    };
  });
}

export default function EnergyPage() {
  const [policyOpen, setPolicyOpen] = useState(false);

  // 차트 state
  const [patternPeriod, setPatternPeriod] = useState("TIME_SLOT"); // TIME_SLOT | DAILY | MONTHLY
  const [patternLoading, setPatternLoading] = useState(false);
  const [patternData, setPatternData] = useState([]);

  const [categoryLoading, setCategoryLoading] = useState(false);
  const [categoryData, setCategoryData] = useState([]);

  const COLORS = ["#2563eb", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#06b6d4"];

  const {
    // dashboard
    dashboard,
    dashboardLoading,

    // list
    devices,
    devicesLoading,
    statusFilter,
    page,
    totalPages,
    totalElements,
    changeStatusFilter,
    changePage,

    // detail
    detailOpen,
    setDetailOpen,
    detail,
    detailLoading,
    controlLogs,
    savingResults,
    openDeviceDetail,

    // actions
    handleStartCheck,
    handleCompleteCheck,
    handleControl,

    // policy
    activePolicy,
    reloadPolicy,

    // loaders
    loadDashboard,
    loadDevices,
  } = useEnergyPage();

  // dashboard mapping
  const monthlyUsage = dashboard?.monthlyUsageKwh ?? 0;
  const checkRequiredCount = dashboard?.checkRequiredCount ?? 0;
  const checkingCount = dashboard?.checkingCount ?? 0;
  const normalCount = dashboard?.normalCount ?? 0;
  const possibleSavingCost = dashboard?.possibleSavingCost ?? 0;

  // 전월 대비(참고치)
  const approxChange = useMemo(() => {
    if (!devices?.length) return 0;
    const vals = devices.map((d) => Number(d.monthChangeRate ?? 0)).filter((v) => !Number.isNaN(v));
    if (!vals.length) return 0;
    const avg = vals.reduce((a, b) => a + b, 0) / vals.length;
    return Math.round(avg * 10) / 10;
  }, [devices]);

  const changeBadge =
    approxChange < 0
      ? { cls: "bg-green-100 text-green-700", icon: <TrendingDown className="size-3 mr-1" /> }
      : { cls: "bg-red-100 text-red-700", icon: <TrendingUp className="size-3 mr-1" /> };

  // 정책 warningPercent (차트 인사이트 기준)
  const warningThresholdPercent = activePolicy?.warningPercent ?? 25;

  const hasOveruse = useMemo(() => {
    return patternData.some((p) => p.actual > p.expected * (1 + warningThresholdPercent / 100));
  }, [patternData, warningThresholdPercent]);

  const worstCategory = useMemo(() => {
    if (!categoryData.length) return null;
    return [...categoryData].sort((a, b) => b.value - a.value)[0];
  }, [categoryData]);

  // 차트 로드 (패턴)
  const loadPattern = async (period = patternPeriod) => {
    setPatternLoading(true);
    try {
      const res = await getEnergyPattern({ period });
      if (res?.success) setPatternData(normalizePattern(res.data));
      else setPatternData([]);
    } catch (e) {
      console.error("소비 패턴 로딩 실패:", e);
      setPatternData([]);
    } finally {
      setPatternLoading(false);
    }
  };

  // 차트 로드 (유형 분포)
  const loadCategory = async () => {
    setCategoryLoading(true);
    try {
      const res = await getEnergyCategory({});
      if (res?.success) setCategoryData(normalizeCategory(res.data));
      else setCategoryData([]);
    } catch (e) {
      console.error("유형 분포 로딩 실패:", e);
      setCategoryData([]);
    } finally {
      setCategoryLoading(false);
    }
  };

  // 최초 로드
  useEffect(() => {
    loadPattern("TIME_SLOT");
    loadCategory();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // period 변경 시 재로딩
  useEffect(() => {
    loadPattern(patternPeriod);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [patternPeriod]);

  return (
    <div className="energy-page">
      {/* 요약 카드 4개 */}
      <div className="dashboard-grid-energy">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-gray-600">이번 달 총 사용량</CardTitle>
          </CardHeader>
          <CardContent>
            {dashboardLoading ? (
              <div className="text-sm text-gray-500">로딩 중...</div>
            ) : (
              <>
                <div className="text-2xl font-semibold">{monthlyUsage.toLocaleString()} kWh</div>
                <div className="mt-2 flex items-center gap-2">
                  <Badge className={changeBadge.cls}>
                    {changeBadge.icon}
                    {Math.abs(approxChange).toLocaleString()}%
                  </Badge>
                  <span className="text-xs text-gray-500">전 설비 평균 변화율(참고)</span>
                </div>
              </>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-gray-600">점검 필요 설비 수</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-semibold">{checkRequiredCount} 대</div>
            <div className="mt-2 flex items-center gap-2 text-xs text-gray-500">
              <AlertTriangle className="size-4 text-red-600" />
              조치 우선순위 대상
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-gray-600">점검 중 설비 수</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-semibold">{checkingCount} 대</div>
            <div className="mt-2 flex items-center gap-2 text-xs text-gray-500">
              <Clock className="size-4 text-yellow-600" />
              작업 진행 중
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-gray-600">예상 절감 가능 비용</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-semibold">₩{possibleSavingCost.toLocaleString()}</div>
            <div className="mt-2 flex items-center gap-2 text-xs text-gray-500">
              <Power className="size-4 text-blue-600" />
              월간 추정(정책 기준)
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 테이블 */}
      <Card>
        {/* <CardHeader className="pb-2">
          <CardTitle className="text-base">점검 우선순위 목록</CardTitle>
          <div className="text-xs text-gray-500">총 {totalElements.toLocaleString()}개</div>
        </CardHeader> */}
        {/* 상태 필터 */}
        <div className="list-topbar">
          {/* pill 탭 */}
          <div className="pillbar" role="tablist" aria-label="상태 필터">
            <button
              type="button"
              className={`pill ${statusFilter === "ALL" ? "active" : ""}`}
              onClick={() => changeStatusFilter("ALL")}
            >
              전체 <span className="pill-count">({totalElements})</span>
            </button>

            <button
              type="button"
              className={`pill ${statusFilter === "CHECK_REQUIRED" ? "active" : ""}`}
              onClick={() => changeStatusFilter("CHECK_REQUIRED")}
            >
              <AlertTriangle className="pill-icon" />
              점검 권장 <span className="pill-count">({checkRequiredCount})</span>
            </button>

            <button
              type="button"
              className={`pill ${statusFilter === "CHECKING" ? "active" : ""}`}
              onClick={() => changeStatusFilter("CHECKING")}
            >
              <Clock className="pill-icon" />
              점검 중 <span className="pill-count">({checkingCount})</span>
            </button>

            <button
              type="button"
              className={`pill ${statusFilter === "NORMAL" ? "active" : ""}`}
              onClick={() => changeStatusFilter("NORMAL")}
            >
              <CheckCircle className="pill-icon" />
              정상 <span className="pill-count">({normalCount})</span>
            </button>
          </div>

          {/* 오른쪽 정책설정 */}
          <Button
            type="button"
            variant="outline"
            className="policy-btn"
            onClick={() => setPolicyOpen(true)}
          >
            <Settings className="size-4 mr-2" />
            정책 설정
          </Button>
        </div>

        <CardContent>
          {devicesLoading ? (
            <div className="py-10 text-center text-sm text-gray-500">로딩 중...</div>
          ) : !devices?.length ? (
            <div className="py-10 text-center text-sm text-gray-500">데이터가 없습니다.</div>
          ) : (
            <>
              <div className="table-wrap">
                <table className="energy-table">
                  <thead>
                    <tr>
                      <th>설비명</th>
                      <th>위치</th>
                      <th>운영</th>
                      <th>상태</th>
                      <th>낭비(kWh)</th>
                      <th>변화율</th>
                      <th>예상 절감액</th>
                      <th>작업</th>
                    </tr>
                  </thead>

                  <tbody>
                    {devices.map((d) => {
                      const s = statusBadge(d.deviceStatus);
                      return (
                        <tr key={d.deviceId}>
                          <td className="col-title">
                            <div className="font-medium">{d.deviceName}</div>
                            <div className="text-xs text-gray-500">ID: {d.deviceId}</div>
                          </td>

                          <td>{d.location ?? "-"}</td>

                          <td>
                            <Badge
                              className={
                                d.isOperating
                                  ? "bg-green-100 text-green-700"
                                  : "bg-gray-100 text-gray-700"
                              }
                            >
                              <Power className="size-3 mr-1" />
                              {d.isOperating ? "ON" : "OFF"}
                            </Badge>
                          </td>

                          <td>
                            <Badge className={s.cls}>
                              {s.icon}
                              {s.label}
                            </Badge>
                          </td>

                          <td>{Number(d.estimatedWasteKwh ?? 0).toLocaleString()}</td>
                          <td>{Number(d.monthChangeRate ?? 0).toLocaleString()}%</td>
                          <td>₩{Number(d.estimatedWasteCost ?? 0).toLocaleString()}</td>

                          <td className="actions">
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => openDeviceDetail(d.deviceId)}
                            >
                              <Eye className="size-3 mr-1" />
                              상세
                            </Button>

                            {d.deviceStatus === "CHECK_REQUIRED" && (
                              <Button size="sm" onClick={() => handleStartCheck(d.deviceId)}>
                                점검 시작
                              </Button>
                            )}

                            {d.deviceStatus === "CHECKING" && (
                              <Button size="sm" onClick={() => handleCompleteCheck(d.deviceId)}>
                                점검 완료
                              </Button>
                            )}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>

              {totalPages > 1 && (
                <div className="pagination">
                  <Button
                    disabled={page === 0}
                    variant="outline"
                    onClick={() => changePage(page - 1)}
                  >
                    이전
                  </Button>

                  <span className="page-indicator">
                    {page + 1} / {totalPages}
                  </span>

                  <Button
                    disabled={page + 1 >= totalPages}
                    variant="outline"
                    onClick={() => changePage(page + 1)}
                  >
                    다음
                  </Button>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>

      {/* 차트 2개 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {/* 소비 패턴 */}
        <Card>
          <CardHeader className="pb-2">
            <div className="flex items-center justify-between gap-2">
              <div>
                <CardTitle className="text-base">에너지 소비 패턴</CardTitle>
                <div className="text-xs text-gray-500">
                  실제 vs 기준(예상) / warningPercent={warningThresholdPercent}%
                </div>
              </div>

              <div className="flex gap-2">
                <Button
                  size="sm"
                  variant={patternPeriod === "TIME_SLOT" ? "default" : "outline"}
                  onClick={() => setPatternPeriod("TIME_SLOT")}
                >
                  타임슬롯
                </Button>
                <Button
                  size="sm"
                  variant={patternPeriod === "DAILY" ? "default" : "outline"}
                  onClick={() => setPatternPeriod("DAILY")}
                >
                  일별
                </Button>
                <Button
                  size="sm"
                  variant={patternPeriod === "MONTHLY" ? "default" : "outline"}
                  onClick={() => setPatternPeriod("MONTHLY")}
                >
                  월별
                </Button>
              </div>
            </div>
          </CardHeader>

          <CardContent>
            {patternLoading ? (
              <div className="py-10 text-center text-sm text-gray-500">로딩 중...</div>
            ) : !patternData.length ? (
              <div className="py-10 text-center text-sm text-gray-500">데이터가 없습니다.</div>
            ) : (
              <>
                <ResponsiveContainer width="100%" height={230}>
                  <AreaChart data={patternData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="label" style={{ fontSize: "12px" }} />
                    <YAxis style={{ fontSize: "12px" }} />
                    <Legend wrapperStyle={{ fontSize: "12px" }} />

                    <RechartsTooltip
                      formatter={(value, name, props) => {
                        if (name === "실제") {
                          const diff = Number(value) - Number(props?.payload?.expected ?? 0);
                          return [`${Number(value).toFixed(2)} kWh (Δ ${diff.toFixed(2)})`, name];
                        }
                        return [`${Number(value).toFixed(2)} kWh`, name];
                      }}
                    />

                    <Area
                      type="monotone"
                      dataKey="expected"
                      name="기준(예상)"
                      stroke="#2563eb"
                      fill="#2563eb"
                      fillOpacity={0.18}
                    />
                    <Area
                      type="monotone"
                      dataKey="actual"
                      name="실제"
                      stroke={hasOveruse ? "#ef4444" : "#16a34a"}
                      fill={hasOveruse ? "#ef4444" : "#16a34a"}
                      fillOpacity={0.35}
                    />
                  </AreaChart>
                </ResponsiveContainer>

                <div className="mt-3 p-2 rounded-lg border bg-gray-50 text-xs text-gray-700 flex items-start gap-2">
                  <Info className="size-4 mt-0.5" />
                  <div>
                    {hasOveruse
                      ? "⚠ 일부 구간에서 정책 경고 기준을 초과한 사용 패턴이 감지되었습니다."
                      : "✔ 정책 기준 내에서 정상적으로 운영되고 있습니다."}
                  </div>
                </div>
              </>
            )}
          </CardContent>
        </Card>

        {/* 유형 분포 */}
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-base">설비 유형별 분포</CardTitle>
            <div className="text-xs text-gray-500">월간 에너지 소비 비율</div>
          </CardHeader>

          <CardContent>
            {categoryLoading ? (
              <div className="py-10 text-center text-sm text-gray-500">로딩 중...</div>
            ) : !categoryData.length ? (
              <div className="py-10 text-center text-sm text-gray-500">데이터가 없습니다.</div>
            ) : (
              <>
                <ResponsiveContainer width="100%" height={230}>
                  <PieChart>
                    <Pie
                      data={categoryData}
                      dataKey="value"
                      nameKey="name"
                      cx="50%"
                      cy="50%"
                      outerRadius={85}
                      label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    >
                      {categoryData.map((_, idx) => (
                        <Cell key={idx} fill={COLORS[idx % COLORS.length]} />
                      ))}
                    </Pie>
                    <RechartsTooltip formatter={(v) => `${Number(v).toFixed(2)} kWh`} />
                  </PieChart>
                </ResponsiveContainer>

                {worstCategory && (
                  <div className="mt-3 p-2 rounded-lg border bg-gray-50 text-xs text-gray-700 flex items-start gap-2">
                    <Info className="size-4 mt-0.5" />
                    <div>
                      이번 달은 <b>{worstCategory.name}</b> 유형의 사용량이 가장 큽니다 (
                      {worstCategory.value.toFixed(0)} kWh).
                    </div>
                  </div>
                )}
              </>
            )}
          </CardContent>
        </Card>
      </div>

      {/* 상세 모달 */}
      <EnergyDetailModal
        open={detailOpen}
        onClose={() => setDetailOpen(false)}
        detail={detail}
        loading={detailLoading}
        controlLogs={controlLogs}
        savingResults={savingResults}
        onControl={(deviceId, operate) => handleControl(deviceId, operate, "관리자 수동 제어")}
      />

      {/* 정책 모달 */}
      <EnergyPolicyModal
        open={policyOpen}
        onClose={() => setPolicyOpen(false)}
        onSaved={async () => {
          await Promise.all([
            reloadPolicy(),
            loadDashboard(),
            loadDevices(),
            loadPattern(patternPeriod),
            loadCategory(),
          ]);
        }}
      />
    </div>
  );
}
