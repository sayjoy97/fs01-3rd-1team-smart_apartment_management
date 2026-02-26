import "./NoisePage.css";
import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import HabitualPage from "./HabitualPage";
import useNoisePage from "./components/useNoisePage";

import NoiseHeader from "./components/NoiseHeader";
import NoiseDashboardCards from "./components/NoiseDashboardCards";
import NoiseCharts from "./components/NoiseCharts";
import NoiseEventTable from "./components/NoiseEventTable";
import NoisePendingPanel from "./components/NoisePendingPanel";
import NoiseDistributions from "./components/NoiseDistributions";

import PolicyModal from "./components/PolicyModal";
import NoiseEventDetailModal from "./components/NoiseEventDetailModal";

export default function NoisePage() {
  const [showHabitual, setShowHabitual] = useState(false);
  const navigate = useNavigate();

  const {
    dashboard,
    dashboardLoading,

    events,
    loadingList,
    page,
    totalPages,
    totalElements,
    viewMode,
    eventFilter,
    setViewMode,
    setEventFilter,
    setPage,

    showDetailModal,
    selectedEvent,
    detailLoading,
    adminMemo,
    setAdminMemo,
    openDetail,
    closeDetail,

    startObserving,
    sendNotification,
    registerHabitual,

    statisticsLoading,
    charts,
    urgentEvents,
    urgentLoading,
    urgentPage,
    urgentTotalPages,
    setUrgentPage,

    policyOpen,
    setPolicyOpen,
    policyLoading,
    activePolicy,
    openPolicy,
    savePolicy,
  } = useNoisePage();

  const COLORS = useMemo(
    () => ["#2563eb", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#06b6d4"],
    [],
  );

  // if (showHabitual) return <HabitualPage onBack={() => setShowHabitual(false)} />;

  return (
    <div className="noise-page2">
      <NoiseDashboardCards
        dashboard={dashboard}
        loading={dashboardLoading}
        activePolicy={activePolicy}
      />
      <NoiseHeader onOpenHabitual={() => navigate("/noise/habitual")} onOpenPolicy={openPolicy} />

      {/* 대시보드 하단 메인 그리드: 2열 구조 */}
      <div className="noise2-main-layout">
        {/* 왼쪽 열: 즉시 처리 필요 목록 (1줄 차지) */}
        <div className="noise2-left-column">
          <NoisePendingPanel
            pendingEvents={urgentEvents}
            pendingLoading={urgentLoading}
            pendingCount={dashboard?.pendingEventCount ?? 0}
            page={urgentPage}
            totalPages={urgentTotalPages}
            onPrev={() => setUrgentPage((p) => Math.max(0, p - 1))}
            onNext={() => setUrgentPage((p) => Math.min(urgentTotalPages - 1, p + 1))}
            onOpenDetail={openDetail}
          />
        </div>

        {/* 오른쪽 열: 차트와 분포도를 위아래로 배치 */}
        <div className="noise2-right-column">
          <div className="noise2-chart-wrapper">
            <NoiseCharts
              statisticsLoading={statisticsLoading}
              charts={charts}
              viewMode={viewMode}
              onChangeViewMode={(mode) => {
                setViewMode(mode);
                setPage(0);
              }}
              colors={COLORS}
              mode="hourly"
              compact={false} // 가로 폭이 넓어지므로 compact를 꺼도 좋습니다
            />
          </div>

          <div className="noise2-dist-wrapper">
            <NoiseDistributions
              loading={statisticsLoading}
              sensorPie={charts?.sensorPie ?? []}
              patternPie={charts?.patternPie ?? []}
              colors={COLORS}
            />
          </div>
        </div>
      </div>

      <NoiseEventTable
        events={events}
        loading={loadingList}
        page={page}
        totalPages={totalPages}
        totalElements={totalElements}
        viewMode={viewMode}
        eventFilter={eventFilter}
        onChangeFilter={(filter) => {
          setEventFilter(filter);
          setPage(0);
        }}
        onChangeViewMode={(mode) => {
          setViewMode(mode);
          setPage(0);
        }}
        onPrevPage={() => setPage(page - 1)}
        onNextPage={() => setPage(page + 1)}
        onOpenDetail={openDetail}
      />

      <NoiseEventDetailModal
        open={showDetailModal}
        onOpenChange={(v) => (v ? null : closeDetail())}
        loading={detailLoading}
        detail={selectedEvent}
        adminMemo={adminMemo}
        setAdminMemo={setAdminMemo}
        onStartObserving={startObserving}
        onSendNotification={sendNotification}
        onRegisterHabitual={registerHabitual}
      />

      <PolicyModal
        key={`${policyOpen ? "open" : "close"}-${activePolicy?.policyId ?? "none"}`}
        open={policyOpen}
        onOpenChange={setPolicyOpen}
        loading={policyLoading}
        policy={activePolicy}
        onSave={savePolicy}
      />
    </div>
  );
}

// import "./NoisePage.css";

// import { useMemo, useState } from "react";
// import { Button } from "../../components/ui/button";
// import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";
// import { Badge } from "../../components/ui/badge";

// import {
//   AlertTriangle,
//   Clock,
//   Bell,
//   Eye,
//   BarChart3,
//   PieChart as PieIcon,
//   Settings,
// } from "lucide-react";

// import HabitualPage from "./HabitualPage";
// import useNoisePage from "./components/useNoisePage";

// import PolicyModal from "./components/PolicyModal";
// import NoiseEventDetailModal from "./components/NoiseEventDetailModal";

// import {
//   ResponsiveContainer,
//   BarChart,
//   Bar,
//   XAxis,
//   YAxis,
//   CartesianGrid,
//   Tooltip,
//   PieChart,
//   Pie,
//   Cell,
//   Legend,
// } from "recharts";

// function timeZoneBadge(tz) {
//   return tz === "야간" ? "bg-indigo-500" : "bg-orange-500";
// }

// export default function NoisePage() {
//   const [showHabitual, setShowHabitual] = useState(false);

//   const {
//     dashboard,
//     dashboardLoading,

//     events,
//     loadingList,
//     page,
//     totalPages,
//     totalElements,
//     viewMode,
//     eventFilter,
//     setViewMode,
//     setEventFilter,
//     setPage,

//     showDetailModal,
//     selectedEvent,
//     detailLoading,
//     adminMemo,
//     setAdminMemo,
//     openDetail,
//     closeDetail,

//     startObserving,
//     sendNotification,
//     registerHabitual,

//     statisticsLoading,
//     charts,

//     // ✅ 정책
//     policyOpen,
//     setPolicyOpen,
//     policyLoading,
//     activePolicy,
//     openPolicy,
//     savePolicy,
//   } = useNoisePage();

//   // ✅ Hook 순서 고정(조건부 return 전에 useMemo 호출)
//   const COLORS = useMemo(
//     () => ["#2563eb", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#06b6d4"],
//     [],
//   );

//   const todayEventCount = dashboard?.todayEventCount ?? 0;
//   const policyBreakCount = dashboard?.policyBreakCount ?? 0;
//   const urgentCount = dashboard?.urgentCount ?? 0;

//   // ✅ 이제 여기서 조건부 return
//   if (showHabitual) {
//     return <HabitualPage onBack={() => setShowHabitual(false)} />;
//   }

//   return (
//     <div className="noise-page">
//       {/* 헤더 */}
//       <div className="noise-header-row">
//         <div>
//           <h1 className="noise-title">층간소음 관리</h1>
//           <p className="noise-subtitle">센서 기반 층간소음 감지 및 관리자 처리 시스템</p>
//         </div>

//         <div className="noise-header-actions">
//           <Button variant="outline" onClick={() => setShowHabitual(true)}>
//             상습 구간 관리
//           </Button>

//           <Button variant="outline" onClick={openPolicy}>
//             <Settings className="size-4 mr-2" />
//             정책 설정
//           </Button>
//         </div>
//       </div>

//       {/* DASHBOARD */}
//       <div className="dashboard-grid">
//         <Card>
//           <CardHeader className="pb-2">
//             <CardTitle className="text-sm text-gray-600">오늘 발생 이벤트</CardTitle>
//           </CardHeader>
//           <CardContent>
//             {dashboardLoading ? (
//               <div className="text-sm text-gray-500">로딩 중...</div>
//             ) : (
//               <div className="text-2xl font-semibold">{todayEventCount} 건</div>
//             )}
//           </CardContent>
//         </Card>

//         <Card>
//           <CardHeader className="pb-2">
//             <CardTitle className="text-sm text-gray-600">정책 위반 의심</CardTitle>
//           </CardHeader>
//           <CardContent>
//             <div className="text-2xl font-semibold">{policyBreakCount} 건</div>
//           </CardContent>
//         </Card>

//         <Card>
//           <CardHeader className="pb-2">
//             <CardTitle className="text-sm text-gray-600">긴급 처리 필요</CardTitle>
//           </CardHeader>
//           <CardContent>
//             <div className="text-2xl font-semibold">{urgentCount} 건</div>
//           </CardContent>
//         </Card>
//       </div>

//       {/* CHARTS */}
//       <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
//         <Card>
//           <CardHeader className="pb-2">
//             <CardTitle className="text-base flex items-center gap-2">
//               <BarChart3 className="size-4" />
//               시간대별 발생/정책위반
//             </CardTitle>
//             <div className="text-xs text-gray-500">0~23시 기준</div>
//           </CardHeader>

//           <CardContent>
//             {statisticsLoading ? (
//               <div className="py-10 text-center text-sm text-gray-500">로딩 중...</div>
//             ) : !charts?.noiseByHour?.length ? (
//               <div className="py-10 text-center text-sm text-gray-500">데이터가 없습니다.</div>
//             ) : (
//               <ResponsiveContainer width="100%" height={240}>
//                 <BarChart data={charts.noiseByHour}>
//                   <CartesianGrid strokeDasharray="3 3" />
//                   <XAxis dataKey="hour" style={{ fontSize: "12px" }} interval={2} />
//                   <YAxis style={{ fontSize: "12px" }} />
//                   <Tooltip />
//                   <Legend />
//                   <Bar dataKey="count" name="발생" />
//                 </BarChart>
//               </ResponsiveContainer>
//             )}

//             {!statisticsLoading && charts?.breakByHour?.length ? (
//               <div className="mt-4">
//                 <div className="text-xs text-gray-500 mb-2">시간대별 정책 위반</div>
//                 <ResponsiveContainer width="100%" height={180}>
//                   <BarChart data={charts.breakByHour}>
//                     <CartesianGrid strokeDasharray="3 3" />
//                     <XAxis dataKey="hour" style={{ fontSize: "12px" }} interval={2} />
//                     <YAxis style={{ fontSize: "12px" }} />
//                     <Tooltip />
//                     <Bar dataKey="count" name="정책위반" />
//                   </BarChart>
//                 </ResponsiveContainer>
//               </div>
//             ) : null}
//           </CardContent>
//         </Card>

//         <Card>
//           <CardHeader className="pb-2">
//             <CardTitle className="text-base flex items-center gap-2">
//               <PieIcon className="size-4" />
//               분포 요약
//             </CardTitle>
//             <div className="text-xs text-gray-500">센서유형 / 패턴(NoisePattern1)</div>
//           </CardHeader>

//           <CardContent>
//             {statisticsLoading ? (
//               <div className="py-10 text-center text-sm text-gray-500">로딩 중...</div>
//             ) : (
//               <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
//                 <div>
//                   <div className="text-xs text-gray-500 mb-2">센서 유형</div>
//                   {!charts?.sensorPie?.length ? (
//                     <div className="py-10 text-center text-sm text-gray-500">데이터 없음</div>
//                   ) : (
//                     <ResponsiveContainer width="100%" height={220}>
//                       <PieChart>
//                         <Pie
//                           data={charts.sensorPie}
//                           dataKey="value"
//                           nameKey="name"
//                           cx="50%"
//                           cy="50%"
//                           outerRadius={75}
//                           label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
//                         >
//                           {charts.sensorPie.map((_, idx) => (
//                             <Cell key={idx} fill={COLORS[idx % COLORS.length]} />
//                           ))}
//                         </Pie>
//                         <Tooltip />
//                       </PieChart>
//                     </ResponsiveContainer>
//                   )}
//                 </div>

//                 <div>
//                   <div className="text-xs text-gray-500 mb-2">패턴(1차)</div>
//                   {!charts?.patternPie?.length ? (
//                     <div className="py-10 text-center text-sm text-gray-500">데이터 없음</div>
//                   ) : (
//                     <ResponsiveContainer width="100%" height={220}>
//                       <PieChart>
//                         <Pie
//                           data={charts.patternPie}
//                           dataKey="value"
//                           nameKey="name"
//                           cx="50%"
//                           cy="50%"
//                           outerRadius={75}
//                           label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
//                         >
//                           {charts.patternPie.map((_, idx) => (
//                             <Cell key={idx} fill={COLORS[idx % COLORS.length]} />
//                           ))}
//                         </Pie>
//                         <Tooltip />
//                       </PieChart>
//                     </ResponsiveContainer>
//                   )}
//                 </div>
//               </div>
//             )}
//           </CardContent>
//         </Card>
//       </div>

//       {/* LIST */}
//       <Card className="mt-4">
//         <div className="list-topbar">
//           <div className="pillbar">
//             <button
//               className={`pill ${eventFilter === "all" ? "active" : ""}`}
//               onClick={() => {
//                 setEventFilter("all");
//                 setPage(0);
//               }}
//             >
//               전체 <span className="pill-count">({totalElements})</span>
//             </button>

//             <button
//               className={`pill ${eventFilter === "unprocessed" ? "active" : ""}`}
//               onClick={() => {
//                 setEventFilter("unprocessed");
//                 setPage(0);
//               }}
//             >
//               <Clock className="pill-icon" />
//               미처리
//             </button>

//             <button
//               className={`pill ${eventFilter === "notified" ? "active" : ""}`}
//               onClick={() => {
//                 setEventFilter("notified");
//                 setPage(0);
//               }}
//             >
//               <Bell className="pill-icon" />
//               알림 완료
//             </button>
//           </div>

//           <div className="flex gap-2">
//             <Button
//               size="sm"
//               variant={viewMode === "all" ? "default" : "outline"}
//               onClick={() => {
//                 setViewMode("all");
//                 setPage(0);
//               }}
//             >
//               전체
//             </Button>
//             <Button
//               size="sm"
//               variant={viewMode === "day" ? "default" : "outline"}
//               onClick={() => {
//                 setViewMode("day");
//                 setPage(0);
//               }}
//             >
//               주간
//             </Button>
//             <Button
//               size="sm"
//               variant={viewMode === "night" ? "default" : "outline"}
//               onClick={() => {
//                 setViewMode("night");
//                 setPage(0);
//               }}
//             >
//               야간
//             </Button>
//           </div>
//         </div>

//         <CardContent>
//           {loadingList ? (
//             <div className="py-10 text-center text-sm text-gray-500">로딩 중...</div>
//           ) : !events.length ? (
//             <div className="py-10 text-center text-sm text-gray-500">데이터가 없습니다.</div>
//           ) : (
//             <>
//               <div className="table-wrap">
//                 <table className="noise-table">
//                   <thead>
//                     <tr>
//                       <th>위치</th>
//                       <th>소음(dB)</th>
//                       <th>반복</th>
//                       <th>시간대</th>
//                       <th>상태</th>
//                       <th>작업</th>
//                     </tr>
//                   </thead>

//                   <tbody>
//                     {events.map((e) => (
//                       <tr key={e.noiseEventId} className={e.urgentBreak ? "row-hot" : ""}>
//                         <td>
//                           {e.upperHouseDong}동 {e.upperHouseHo}호 ↔ {e.lowerHouseDong}동{" "}
//                           {e.lowerHouseHo}호
//                         </td>
//                         <td>{e.soundLevel}</td>
//                         <td>{e.repeatCount}</td>
//                         <td>
//                           <Badge className={timeZoneBadge(e.timeZone)}>{e.timeZone}</Badge>
//                         </td>
//                         <td>
//                           {e.urgentBreak ? (
//                             <Badge className="bg-red-100 text-red-700">
//                               <AlertTriangle className="size-3 mr-1" />
//                               URGENT
//                             </Badge>
//                           ) : (
//                             <Badge className="bg-gray-100 text-gray-700">NORMAL</Badge>
//                           )}
//                         </td>
//                         <td>
//                           <Button
//                             size="sm"
//                             variant="outline"
//                             onClick={() => openDetail(e.noiseEventId)}
//                           >
//                             <Eye className="size-3 mr-1" />
//                             상세
//                           </Button>
//                         </td>
//                       </tr>
//                     ))}
//                   </tbody>
//                 </table>
//               </div>

//               {totalPages > 1 && (
//                 <div className="pagination">
//                   <Button disabled={page === 0} variant="outline" onClick={() => setPage(page - 1)}>
//                     이전
//                   </Button>

//                   <span className="page-indicator">
//                     {page + 1} / {totalPages}
//                   </span>

//                   <Button
//                     disabled={page + 1 >= totalPages}
//                     variant="outline"
//                     onClick={() => setPage(page + 1)}
//                   >
//                     다음
//                   </Button>
//                 </div>
//               )}
//             </>
//           )}
//         </CardContent>
//       </Card>

//       {/* ✅ 상세 Dialog 모달 */}
//       <NoiseEventDetailModal
//         open={showDetailModal}
//         onOpenChange={(v) => (v ? null : closeDetail())}
//         loading={detailLoading}
//         detail={selectedEvent}
//         adminMemo={adminMemo}
//         setAdminMemo={setAdminMemo}
//         onStartObserving={startObserving}
//         onSendNotification={sendNotification}
//         onRegisterHabitual={registerHabitual}
//       />

//       {/* ✅ 정책 Dialog 모달 */}
//       <PolicyModal
//         key={`${policyOpen ? "open" : "close"}-${activePolicy?.policyId ?? "none"}`}
//         open={policyOpen}
//         onOpenChange={setPolicyOpen}
//         loading={policyLoading}
//         policy={activePolicy}
//         onSave={savePolicy}
//       />
//     </div>
//   );
// }
