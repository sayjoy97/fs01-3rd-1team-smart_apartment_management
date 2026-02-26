import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";
import { Badge } from "../../components/ui/badge";
import { Button } from "../../components/ui/button";
import {
  DoorOpen,
  Clock,
  Car,
  Zap,
  Volume2,
  Sun,
  Edit3,
  Save,
  X as XIcon,
  ChevronRight,
  Key,
  CreditCard,
  User,
  Droplets,
  Wind,
  Info,
  Shield,
  ClipboardCheck,
  Megaphone,
  CalendarDays,
  Cloud,
  CloudRain,
  CloudSnow,
  CloudSun,
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import {
  getRecentEntranceLogs,
  getParkingFeeSummary,
  getEnergyDashboard,
  getMyAdminSummary,
  getFixedNotices,
  getWaitingComplaints,
} from "../../api/dashboardAPI";
import { getCurrentWeather } from "../../api/weatherAPI";
import "./Dashboard.css";

export default function Dashboard({ onNavigate }) {
  const navigate = useNavigate();
  const [currentTime, setCurrentTime] = useState(new Date());
  const [isEditMode, setIsEditMode] = useState(false);

  // 출입관리 컴포넌트
  const [recentLogs, setRecentLogs] = useState([]);
  const [logsLoading, setLogsLoading] = useState(false);
  const [logsError, setLogsError] = useState(null);

  // 차량요금 컴포넌트
  const [feeSummary, setFeeSummary] = useState(null);
  const [feeLoading, setFeeLoading] = useState(false);

  // 에너지요약 컴포넌트
  const [energySummary, setEnergySummary] = useState(null);
  const [energyLoading, setEnergyLoading] = useState(false);

  // 날씨 API 컴포넌트
  const [weather, setWeather] = useState(null);
  const [weatherLoading, setWeatherLoading] = useState(false);

  // 관리자 정보 컴포넌트
  const [adminSummary, setAdminSummary] = useState(null);
  const [adminLoading, setAdminLoading] = useState(false);

  // 고정공지&미답변민원 컴포넌트
  const [fixedNotices, setFixedNotices] = useState([]);
  const [fixedLoading, setFixedLoading] = useState(false);

  const [waitingComplaints, setWaitingComplaints] = useState([]);
  const [waitingLoading, setWaitingLoading] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    (async () => {
      try {
        setLogsLoading(true);
        setLogsError(null);
        const data = await getRecentEntranceLogs(); // {content, ...}
        setRecentLogs(data?.content ?? []);
      } catch (e) {
        console.error(e);
        setLogsError("최근 출입 로그를 불러오지 못했습니다.");
        setRecentLogs([]);
      } finally {
        setLogsLoading(false);
      }
    })();
  }, []);

  useEffect(() => {
    (async () => {
      try {
        setFeeLoading(true);
        const res = await getParkingFeeSummary();
        setFeeSummary(res?.data ?? null); // ✅ ApiResponse 구조면 res.data에 실제 payload가 있을 것
      } catch (e) {
        console.error(e);
        setFeeSummary(null);
      } finally {
        setFeeLoading(false);
      }
    })();
  }, []);

  useEffect(() => {
    (async () => {
      try {
        setEnergyLoading(true);
        const res = await getEnergyDashboard();
        setEnergySummary(res?.data ?? null); // ApiResponse 구조면 여기
      } catch (e) {
        console.error(e);
        setEnergySummary(null);
      } finally {
        setEnergyLoading(false);
      }
    })();
  }, []);

  useEffect(() => {
    (async () => {
      try {
        setWeatherLoading(true);
        const data = await getCurrentWeather(37.5665, 126.978);
        console.log("KEY:", import.meta.env.VITE_WEATHER_API_KEY);
        setWeather(data);
      } catch (e) {
        console.error(e);
        console.log("KEY:", import.meta.env.VITE_WEATHER_API_KEY);
      } finally {
        setWeatherLoading(false);
        console.log("KEY:", import.meta.env.VITE_WEATHER_API_KEY);
      }
    })();
  }, []);

  useEffect(() => {
    (async () => {
      try {
        setAdminLoading(true);
        const res = await getMyAdminSummary();
        setAdminSummary(res?.data ?? null); // ApiResponse라면 res.data에 payload
      } catch (e) {
        console.error(e);
        setAdminSummary(null);
      } finally {
        setAdminLoading(false);
      }
    })();
  }, []);

  useEffect(() => {
    (async () => {
      try {
        setFixedLoading(true);
        const res = await getFixedNotices();
        // ApiResponse.success(list)라면 list는 res.data
        setFixedNotices(res?.data ?? []);
      } catch (e) {
        console.error(e);
        setFixedNotices([]);
      } finally {
        setFixedLoading(false);
      }
    })();
  }, []);

  useEffect(() => {
    (async () => {
      try {
        setWaitingLoading(true);
        // 카드 크기에 맞춰 N 조절 (예: 4)
        const res = await getWaitingComplaints(4);
        // 민원은 Map 형태: {content, totalPages...}
        setWaitingComplaints(res?.content ?? []);
      } catch (e) {
        console.error(e);
        setWaitingComplaints([]);
      } finally {
        setWaitingLoading(false);
      }
    })();
  }, []);

  // 차량요금 포맷 함수
  const formatWon = (v) => {
    if (v === null || v === undefined) return "-";
    const n = typeof v === "string" ? Number(v) : v;
    if (Number.isNaN(n)) return "-";
    return `₩${Math.round(n).toLocaleString("ko-KR")}`;
  };
  // 에너지카드 숫자 포맷 함수
  const formatKwh = (v) => {
    if (v === null || v === undefined) return "-";
    return `${Number(v).toLocaleString("ko-KR")} kWh`;
  };
  const renderWeatherIcon = () => {
    if (!weather) return null;

    const main = weather.weather[0].main;

    if (main === "Clear") {
      return <div className="sun-icon" />;
    }

    if (main === "Clouds") {
      return <div className="cloud-icon" />;
    }

    if (main === "Rain" || main === "Drizzle") {
      return <div className="rain-icon" />;
    }

    if (main === "Snow") {
      return <div className="snow-icon" />;
    }

    return <div className="cloud-icon" />;
  };

  return (
    <div className="dashboard-container1">
      {/* 헤더 섹션 */}
      <div className="dashboard-header mb-5 flex justify-between items-end">
        <div>
          <h1 className="text-xl font-semibold text-[var(--blue-deep)] -mt-9 ml-18">
            단지 통합 대시보드
          </h1>
          <p className="text-sm text-[var(--muted-foreground)]">
            {currentTime.toLocaleDateString("ko-KR")}{" "}
            <span className="text-[var(--blue-primary)] font-medium -mt+2 ml-1">
              {currentTime.toLocaleTimeString("ko-KR")}
            </span>
          </p>
        </div>
        {/* <div className="flex gap-2">
          {!isEditMode ? (
            <Button
              onClick={() => setIsEditMode(true)}
              variant="outline"
              className="rounded-xl border-[var(--blue-light)]"
            >
              <Edit3 className="size-4 mr-2" /> 편집
            </Button>
          ) : (
            <div className="flex gap-2">
              <Button
                onClick={() => setIsEditMode(false)}
                className="rounded-xl bg-[var(--blue-primary)] text-white"
              >
                <Save className="size-4 mr-2" /> 저장
              </Button>
              <Button onClick={() => setIsEditMode(false)} variant="ghost" className="text-red-500">
                취소
              </Button>
            </div>
          )}
        </div> */}
      </div>

      {/* 그리드 시스템: col-span-X 클래스가 작동하도록 확실히 배치 */}
      <div className="dashboard-grid">
        {/* 0. 관리자 */}
        <Card
          className="widget-card col-span-3 group h-full cursor-pointer hover:bg-slate-50 transition"
          onClick={() => navigate("/mypage")}
        >
          <div className="widget-inner h-full">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="widget-title">
                <Shield className="size-4" /> 관리자
              </CardTitle>
              <ChevronRight className="size-4 text-slate-300" />
            </CardHeader>

            <CardContent className="widget-content flex flex-col gap-3">
              {adminLoading || !adminSummary ? (
                <div className="text-sm text-slate-400 py-6">불러오는 중...</div>
              ) : (
                <>
                  <div className="flex items-center justify-between">
                    <div className="min-w-0">
                      <p className="text-base font-bold text-[var(--blue-deep)] truncate">
                        {adminSummary.adminRes?.adminName ?? "관리자"}
                      </p>
                      <div className="flex items-center gap-2 mt-1">
                        <Badge className="bg-slate-100 text-slate-700 border-none">
                          {adminSummary.adminRes?.adminRole ?? "-"}
                        </Badge>
                        <Badge className="bg-green-100 text-green-700 border-none">활성 계정</Badge>
                      </div>
                    </div>
                  </div>

                  <div className="grid grid-cols-3 gap-2">
                    <div className="bg-slate-50 rounded-lg p-2 text-center">
                      <p className="text-[10px] text-slate-400 flex items-center justify-center gap-1">
                        <ClipboardCheck className="size-3" /> 처리민원
                      </p>
                      <p className="text-sm font-bold text-blue-600">
                        {adminSummary.resolvedComplaintCount?.toLocaleString("ko-KR") ?? 0}
                      </p>
                    </div>

                    <div className="bg-slate-50 rounded-lg p-2 text-center">
                      <p className="text-[10px] text-slate-400 flex items-center justify-center gap-1">
                        <Megaphone className="size-3" /> 작성공지
                      </p>
                      <p className="text-sm font-bold text-purple-600">
                        {adminSummary.postedNoticeCount?.toLocaleString("ko-KR") ?? 0}
                      </p>
                    </div>

                    <div className="bg-slate-50 rounded-lg p-2 text-center">
                      <p className="text-[10px] text-slate-400 flex items-center justify-center gap-1">
                        <CalendarDays className="size-3" /> 근무일
                      </p>
                      <p className="text-sm font-bold text-slate-900">
                        {(adminSummary.totalWorkingDays ?? 0).toLocaleString("ko-KR")}일
                      </p>
                    </div>
                  </div>
                </>
              )}
            </CardContent>

            <div className="widget-footer">
              마이페이지로 이동 <ChevronRight className="size-3" />
            </div>
          </div>
        </Card>

        {/* 1. 공동현관 */}
        <Card
          className="widget-card col-span-4 group h-full cursor-pointer hover:bg-slate-50 transition"
          onClick={() => navigate("/entrance")}
          role="button"
          tabIndex={0}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") onNavigate?.("/entrance");
          }}
        >
          <div className="widget-inner h-full">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="widget-title">
                <DoorOpen className="size-4" /> 공동현관
              </CardTitle>
              <ChevronRight className="size-4 text-slate-300" />
            </CardHeader>

            <CardContent className="widget-content">
              <div className="text-xs text-slate-400 mb-2">최근 출입 로그</div>

              {logsLoading ? (
                <div className="text-xs text-slate-400 py-4">불러오는 중...</div>
              ) : logsError ? (
                <div className="text-xs text-red-500 py-4">{logsError}</div>
              ) : recentLogs.length === 0 ? (
                <div className="text-xs text-slate-400 py-4">최근 로그가 없습니다</div>
              ) : (
                <div className="flex flex-col gap-2 mb-3">
                  {recentLogs.map((log) => {
                    const dt = log.accessedAt ? new Date(log.accessedAt) : null;
                    const isFail = !!log.failReason && log.failReason !== "NONE";

                    return (
                      <div
                        key={log.accessLogId}
                        className="flex items-center justify-between gap-2"
                      >
                        <div className="min-w-0">
                          <div className="text-sm font-semibold text-slate-900 truncate">
                            {log.houseDong}동 {log.houseHo}호
                          </div>
                          <div className="text-[11px] text-slate-400 truncate">
                            {dt ? dt.toLocaleString("ko-KR") : "-"}
                            {log.accessType ? ` · ${log.accessType}` : ""}
                            {log.adminName ? ` · ${log.adminName}` : ""}
                          </div>
                        </div>

                        <Badge
                          className={`shrink-0 border-none ${
                            isFail ? "bg-red-100 text-red-700" : "bg-[#e8f5e9] text-[#2e7d32]"
                          }`}
                        >
                          {isFail ? "실패" : "성공"}
                        </Badge>
                      </div>
                    );
                  })}
                </div>
              )}
            </CardContent>

            <div className="widget-footer">
              출입관리로 이동 <ChevronRight className="size-3" />
            </div>
          </div>
        </Card>

        {/* 2. 차량 요금 */}
        <Card
          className="widget-card col-span-2 group h-full cursor-pointer hover:bg-slate-50 transition"
          onClick={() => navigate("/cargate")}
        >
          <div className="widget-inner h-full">
            <CardHeader className="pb-2">
              <CardTitle className="widget-title">
                <Car className="size-4" /> 차량 요금
              </CardTitle>
            </CardHeader>

            <CardContent className="widget-content flex flex-col gap-2">
              <div className="bg-[#f0f9f4] p-4 rounded-xl text-center border border-[#dcfce7]">
                <p className="text-[10px] text-[#166534] font-bold mb-1">금일 누적금액</p>
                {feeLoading ? (
                  <p className="text-xl font-black text-[#166534]">불러오는 중...</p>
                ) : (
                  <p className="text-2xl font-black text-[#166534]">
                    {formatWon(feeSummary?.todayCount)}
                  </p>
                )}
              </div>

              <div className="grid grid-cols-2 gap-2 mt-1">
                <div className="bg-slate-50 p-2 rounded-lg text-center">
                  <p className="text-[10px] text-slate-400">월평균 금액</p>
                  <p className="text-sm font-bold text-blue-600">
                    {feeLoading ? "-" : formatWon(feeSummary?.monthAverageCount)}
                  </p>
                </div>

                <div className="bg-purple-50 p-2 rounded-lg text-center">
                  <p className="text-[10px] text-slate-400">월평균 방문차량</p>
                  <p className="text-sm font-bold text-purple-600">
                    {feeLoading
                      ? "-"
                      : `${(feeSummary?.unRegisAverageCount ?? "-").toLocaleString("ko-KR")}대`}
                  </p>
                </div>
              </div>
            </CardContent>

            <div className="widget-footer">
              방문차량 관리로 이동 <ChevronRight className="size-3" />
            </div>
          </div>
        </Card>

        {/* 3. 에너지 관리 */}
        <Card
          className="widget-card col-span-2 group h-full cursor-pointer hover:bg-slate-50 transition"
          onClick={() => navigate("/energy")}
        >
          <div className="widget-inner h-full">
            <CardHeader className="pb-2 text-sm font-medium">
              <CardTitle className="widget-title">
                <Zap className="size-4" /> 에너지 관리
              </CardTitle>
            </CardHeader>

            <CardContent className="widget-content grid grid-cols-2 gap-3">
              <div className="p-3 border border-red-100 bg-red-50/50 rounded-xl relative flex flex-col justify-center">
                <p className="text-[10px] text-slate-500 mb-1 font-medium">점검 필요 설비</p>
                <p className="text-2xl font-bold text-red-500 text-center">
                  {energyLoading ? "-" : `${energySummary?.checkRequiredCount ?? 0}대`}
                </p>
              </div>

              <div className="p-3 border border-orange-100 bg-orange-50/50 rounded-xl">
                <p className="text-[10px] text-slate-500 mb-1">점검 중 설비</p>
                <p className="text-xl font-bold text-orange-600">
                  {energyLoading ? "-" : `${energySummary?.checkingCount ?? 0}대`}
                </p>
              </div>

              <div className="col-span-2 p-3 border border-blue-100 bg-blue-50/50 rounded-xl">
                <p className="text-[10px] text-slate-500 mb-1">이번달 총 사용량</p>
                <p className="text-xl font-bold text-blue-700">
                  {energyLoading ? "-" : formatKwh(energySummary?.monthlyUsageKwh)}
                </p>
              </div>
            </CardContent>

            <div className="widget-footer">
              에너지 관리로 이동 <ChevronRight className="size-3" />
            </div>
          </div>
        </Card>

        {/* 4. 현재 날씨 */}
        <Card className="widget-card col-span-1 group h-full bg-blue-50/30">
          <div className="widget-inner h-full">
            <CardHeader className="pb-2">
              <CardTitle className="widget-title">
                <Sun className="size-4" /> 현재 날씨
              </CardTitle>
            </CardHeader>

            <CardContent className="widget-content flex flex-col items-center">
              {weatherLoading || !weather ? (
                <div className="text-sm text-slate-400 py-6">불러오는 중...</div>
              ) : (
                <>
                  <div className="flex items-center gap-4 w-full justify-between">
                    <div>
                      <p className="text-4xl font-black">{Math.round(weather.main.temp)}°C</p>
                      <p className="text-xs text-slate-500 font-medium">
                        {weather.weather[0].description}
                      </p>
                    </div>
                    {renderWeatherIcon()}
                  </div>

                  <div className="flex gap-2 mt-4 w-full">
                    <div className="weather-small-box text-[9px]">
                      <Wind className="size-3" /> 체감 {Math.round(weather.main.feels_like)}°C
                    </div>
                    <div className="weather-small-box text-[9px]">
                      <Droplets className="size-3" /> {weather.main.humidity}%
                    </div>
                    <div className="weather-small-box text-[9px]">
                      <Wind className="size-3" /> {weather.wind.speed}m/s
                    </div>
                  </div>
                </>
              )}
            </CardContent>
          </div>
        </Card>

        {/* 5. 고정 공지 */}
        <Card
          className="widget-card col-span-6 group cursor-pointer"
          onClick={() => navigate("/notices")}
        >
          <div className="widget-inner">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="widget-title">
                <Megaphone className="size-4" /> 고정 공지
              </CardTitle>
              <ChevronRight className="size-4 text-slate-300" />
            </CardHeader>

            <CardContent className="widget-content">
              {fixedLoading ? (
                <div className="text-sm text-slate-400 py-6">불러오는 중...</div>
              ) : fixedNotices.length === 0 ? (
                <div className="text-sm text-slate-400 py-6">고정 공지가 없습니다</div>
              ) : (
                <div className="flex flex-col gap-2">
                  {fixedNotices.slice(0, 3).map((n) => (
                    <div
                      key={n.noticeId}
                      className="flex items-center justify-between gap-3"
                      onClick={(e) => {
                        e.stopPropagation();
                        navigate(`/notices/${n.noticeId}`);
                      }}
                    >
                      <div className="min-w-0">
                        <div className="text-sm font-semibold text-slate-900 truncate">
                          {n.noticeTitle}
                        </div>
                        <div className="text-[11px] text-slate-400 truncate">
                          {n.adminName} · {new Date(n.createdAt).toLocaleDateString("ko-KR")}
                        </div>
                      </div>
                      <Badge className="shrink-0 bg-slate-100 text-slate-700 border-none">
                        고정
                      </Badge>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>

            <div className="widget-footer">
              공지사항으로 이동 <ChevronRight className="size-3" />
            </div>
          </div>
        </Card>

        {/* 6. 미답변 민원 */}
        <Card
          className="widget-card col-span-6 group cursor-pointer"
          onClick={() => navigate("/complaint")}
        >
          <div className="widget-inner">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="widget-title">
                <ClipboardCheck className="size-4" /> 미답변 민원
              </CardTitle>
              <ChevronRight className="size-4 text-slate-300" />
            </CardHeader>

            <CardContent className="widget-content">
              {waitingLoading ? (
                <div className="text-sm text-slate-400 py-6">불러오는 중...</div>
              ) : waitingComplaints.length === 0 ? (
                <div className="text-sm text-slate-400 py-6">미답변 민원이 없습니다</div>
              ) : (
                <div className="flex flex-col gap-2">
                  {waitingComplaints.map((c) => (
                    <div
                      key={c.complaintId}
                      className="flex items-center justify-between gap-3"
                      onClick={(e) => {
                        e.stopPropagation();
                        navigate("/complaint");
                      }}
                    >
                      <div className="min-w-0">
                        <div className="text-sm font-semibold text-slate-900 truncate">
                          {c.title}
                        </div>
                        <div className="text-[11px] text-slate-400 truncate">
                          {c.houseDong}동 {c.houseHo}호 · {c.category} ·{" "}
                          {new Date(c.createAt).toLocaleString("ko-KR")}
                        </div>
                      </div>
                      <Badge className="shrink-0 bg-orange-100 text-orange-700 border-none">
                        대기
                      </Badge>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>

            <div className="widget-footer">
              민원관리로 이동 <ChevronRight className="size-3" />
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}
