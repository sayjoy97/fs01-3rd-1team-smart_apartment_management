// import { useState, useEffect } from "react";
// import {
//   energyDeviceList,
//   energyDeviceDetail,
//   energyDeviceStatusUpdate,
//   energyDeviceControl,
//   energyPolicyGet,
//   energyPolicyUpdate,
// } from "../../api/energyAPI";
// import {
//   Card,
//   CardContent,
//   CardDescription,
//   CardHeader,
//   CardTitle,
// } from "../../components/ui/card";
// import { Button } from "../../components/ui/button";
// import { Badge } from "../../components/ui/badge";
// import { Input } from "../../components/ui/input";
// import { Label } from "../../components/ui/label";
// import {
//   Dialog,
//   DialogContent,
//   DialogDescription,
//   DialogHeader,
//   DialogTitle,
//   DialogFooter,
// } from "../../components/ui/dialog";
// import { Tabs, TabsContent, TabsList, TabsTrigger } from "../../components/ui/tabs";
// import {
//   Table,
//   TableBody,
//   TableCell,
//   TableHead,
//   TableHeader,
//   TableRow,
// } from "../../components/ui/table";
// import {
//   Tooltip,
//   TooltipContent,
//   TooltipProvider,
//   TooltipTrigger,
// } from "../../components/ui/tooltip";
// import {
//   LineChart,
//   Line,
//   PieChart,
//   Pie,
//   Cell,
//   XAxis,
//   YAxis,
//   CartesianGrid,
//   Tooltip as RechartsTooltip,
//   Legend,
//   ResponsiveContainer,
//   Area,
//   AreaChart,
// } from "recharts";
// import {
//   Zap,
//   TrendingUp,
//   TrendingDown,
//   AlertTriangle,
//   CheckCircle,
//   Clock,
//   Eye,
//   Power,
//   User,
//   Calendar,
//   Info,
//   Settings,
//   ChevronLeft,
//   ChevronRight,
// } from "lucide-react";
// import { toast } from "sonner";
// import {
//   Select,
//   SelectContent,
//   SelectItem,
//   SelectTrigger,
//   SelectValue,
// } from "../../components/ui/select";

// function EnergyPage() {
//   const [devices, setDevices] = useState([]);

//   // 디바이스 불러오기
//   const [loading, setLoading] = useState(false);

//   useEffect(() => {
//     loadDevices();
//   }, []);

//   const loadDevices = async () => {
//     try {
//       setLoading(true);

//       const res = await energyDeviceList({
//         page: 0,
//         size: 10,
//       });

//       console.log("디바이스 응답:", res);

//       setDevices(res?.data?.content || []);
//     } catch (e) {
//       console.error(e);
//       setDevices([]);
//     } finally {
//       setLoading(false);
//     }
//   };
//   // 일단 에러 안나게 임시 추가
//   const patternData = [];
//   const categoryData = [];
//   const savingsData = [];
//   //

//   const [selectedDevice, setSelectedDevice] = useState(null);

//   // 운영이력 연결
//   const [logs, setLogs] = useState([]);

//   const [detailModal, setDetailModal] = useState(false);
//   const [policyModal, setPolicyModal] = useState(false);
//   const [patternPeriod, setPatternPeriod] = useState("daily");

//   const [currentPage, setCurrentPage] = useState(1);
//   const [statusFilter, setStatusFilter] = useState("ALL");
//   const itemsPerPage = 10;

//   // 정책 설정 상태
//   const [policySettings, setPolicySettings] = useState({
//     wasteThreshold: 100,
//     changeRateThreshold: 20,
//     autoInspectionEnabled: true,
//     notificationEnabled: true,
//     operatingHoursStart: "06:00",
//     operatingHoursEnd: "22:00",
//     // 고급 설정
//     comparisonBase: "동일 설비 과거 평균",
//     repeatDetectionCount: 3,
//     repeatDetectionHours: 24,
//     ignoreSingleViolation: false,
//   });

//   // 요약 지표 계산
//   const totalUsageThisMonth = 8230;
//   const totalUsageLastMonth = 9150;
//   const usageChange = (
//     ((totalUsageThisMonth - totalUsageLastMonth) / totalUsageLastMonth) *
//     100
//   ).toFixed(1);
//   const newFlaggedToday = devices.filter(
//     (d) =>
//       d.deviceStatus === "CHECK_REQUIRED" &&
//       d.lastCheckDate &&
//       new Date(d.lastCheckDate).toDateString() === new Date().toDateString(),
//   ).length;
//   const potentialSavings = devices
//     .filter((d) => d.deviceStatus === "CHECK_REQUIRED")
//     .reduce((sum, d) => sum + (d.estimatedWasteCost || 0), 0)
//     .toFixed(0);

//   // 상태별 필터링
//   const filteredDevices =
//     statusFilter === "ALL" ? devices : devices.filter((d) => d.deviceStatus === statusFilter);

//   // 페이지네이션
//   const totalPages = Math.ceil(filteredDevices.length / itemsPerPage);
//   const paginatedDevices = filteredDevices.slice(
//     (currentPage - 1) * itemsPerPage,
//     currentPage * itemsPerPage,
//   );

//   const handleViewDetail = async (device) => {
//     setSelectedDevice(device);
//     const res = await energyDeviceDetail(device.deviceId);
//     setLogs(res.data.data || []);
//     setDetailModal(true);
//   };

//   // 디바이스 상태 변경 함수
//   const handleStatusChange = async (deviceId, newStatus) => {
//     try {
//       await energyDeviceStatusUpdate(deviceId, newStatus);
//       toast.success("상태가 변경되었습니다");
//       loadDevices();
//     } catch (e) {
//       console.error(e);
//     }
//   };

//   const handlePeriodChange = (period) => {
//     setPatternPeriod(period);
//   };

//   // 디바이스 제어 함수(on/off)
//   const handleControlDevice = async (device) => {
//     try {
//       await energyDeviceControl(device.deviceId, !device.isOperating);

//       toast.success("제어 명령이 전송되었습니다");
//       loadDevices();
//       setDetailModal(false);
//     } catch (e) {
//       console.error(e);
//     }
//   };

//   const handleSavePolicy = () => {
//     toast.success("정책 설정이 저장되었습니다");
//     setPolicyModal(false);
//   };

//   const handleStatusFilterChange = (status) => {
//     setStatusFilter(status);
//     setCurrentPage(1);
//   };

//   const getStatusColor = (status) => {
//     switch (status) {
//       case "CHECK_REQUIRED":
//         return "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300";
//       case "CHECKING":
//         return "bg-yellow-100 text-yellow-700 dark:bg-yellow-900 dark:text-yellow-300";
//       case "NORMAL":
//         return "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300";
//       default:
//         return "bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300";
//     }
//   };

//   const getStatusLabel = (status) => {
//     switch (status) {
//       case "CHECK_REQUIRED":
//         return "점검 권장";
//       case "CHECKING":
//         return "점검 중";
//       case "NORMAL":
//         return "정상";
//       default:
//         return status;
//     }
//   };

//   const getStatusIcon = (status) => {
//     switch (status) {
//       case "CHECK_REQUIRED":
//         return AlertTriangle;
//       case "CHECKING":
//         return Clock;
//       case "NORMAL":
//         return CheckCircle;
//       default:
//         return Info;
//     }
//   };

//   const COLORS = ["#2563eb", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6"];

//   return (
//     <div className="space-y-6">
//       <div className="flex items-center justify-between">
//         <div>
//           <h1 className="text-3xl mb-2 dark:text-white">에너지 관리</h1>
//           <p className="text-gray-500 dark:text-gray-400">
//             아파트 단지 에너지 소비 분석 및 관리자 점검 권장
//           </p>
//         </div>
//         <Button onClick={() => setPolicyModal(true)} className="bg-blue-600 hover:bg-blue-700">
//           <Settings className="size-4 mr-2" />
//           정책 설정
//         </Button>
//       </div>

//       {/* 요약 카드 - 4개 한 줄 */}
//       <div className="grid grid-cols-4 gap-4">
//         <Card className="dark:bg-gray-800 dark:border-gray-700">
//           <CardHeader className="pb-3">
//             <CardDescription className="text-xs dark:text-gray-400">
//               이번 달 총 사용량
//             </CardDescription>
//             <CardTitle className="text-2xl dark:text-white">
//               {totalUsageThisMonth.toLocaleString()} kWh
//             </CardTitle>
//           </CardHeader>
//           <CardContent className="pb-3">
//             <div className="flex items-center gap-2">
//               <Badge
//                 className={
//                   parseFloat(usageChange) < 0
//                     ? "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300"
//                     : "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300"
//                 }
//               >
//                 {parseFloat(usageChange) < 0 ? (
//                   <TrendingDown className="size-3 mr-1" />
//                 ) : (
//                   <TrendingUp className="size-3 mr-1" />
//                 )}
//                 {Math.abs(usageChange)}%
//               </Badge>
//               <span className="text-xs text-gray-500 dark:text-gray-400">전월 대비</span>
//             </div>
//           </CardContent>
//         </Card>

//         <Card className="dark:bg-gray-800 dark:border-gray-700">
//           <CardHeader className="pb-3">
//             <CardDescription className="text-xs dark:text-gray-400">오늘 점검 필요</CardDescription>
//             <CardTitle className="text-2xl dark:text-white">{newFlaggedToday}건</CardTitle>
//           </CardHeader>
//           <CardContent className="pb-3">
//             <div className="flex items-center gap-2">
//               <AlertTriangle className="size-4 text-red-600 dark:text-red-400" />
//               <span className="text-xs text-gray-500 dark:text-gray-400">긴급 점검 권장</span>
//             </div>
//           </CardContent>
//         </Card>

//         <Card className="dark:bg-gray-800 dark:border-gray-700">
//           <CardHeader className="pb-3">
//             <div className="flex items-center gap-1">
//               <CardDescription className="text-xs dark:text-gray-400">
//                 예상 절감 가능 비용
//               </CardDescription>
//               <TooltipProvider>
//                 <Tooltip>
//                   <TooltipTrigger>
//                     <Info className="size-3 text-gray-400" />
//                   </TooltipTrigger>
//                   <TooltipContent className="dark:bg-gray-700 dark:border-gray-600 max-w-xs">
//                     <p className="text-xs">
//                       최근 평균 사용량 대비 과다 사용량을 기준으로 추정된 값입니다.
//                     </p>
//                     <p className="text-xs mt-1">
//                       실제 절감액은 조치 내용과 운영 환경에 따라 달라질 수 있습니다.
//                     </p>
//                   </TooltipContent>
//                 </Tooltip>
//               </TooltipProvider>
//             </div>
//             <CardTitle className="text-2xl dark:text-white">
//               ₩{potentialSavings.toLocaleString()}
//             </CardTitle>
//           </CardHeader>
//           <CardContent className="pb-3">
//             <div className="flex items-center gap-2">
//               <Zap className="size-4 text-blue-600 dark:text-blue-400" />
//               <span className="text-xs text-gray-500 dark:text-gray-400">월간 추정</span>
//             </div>
//           </CardContent>
//         </Card>

//         <Card className="dark:bg-gray-800 dark:border-gray-700">
//           <CardHeader className="pb-3">
//             <CardDescription className="text-xs dark:text-gray-400">점검 대기 중</CardDescription>
//             <CardTitle className="text-2xl dark:text-white">
//               {devices.filter((d) => d.deviceStatus === "CHECK_REQUIRED").length}대
//             </CardTitle>
//           </CardHeader>
//           <CardContent className="pb-3">
//             <div className="flex items-center gap-2">
//               <Clock className="size-4 text-yellow-600 dark:text-yellow-400" />
//               <span className="text-xs text-gray-500 dark:text-gray-400">조치 필요</span>
//             </div>
//           </CardContent>
//         </Card>
//       </div>

//       {/* 우선순위 점검 디바이스 목록 */}
//       <Card className="dark:bg-gray-800 dark:border-gray-700">
//         <CardHeader>
//           <CardTitle className="dark:text-white">점검 우선순위 목록</CardTitle>
//           <CardDescription className="dark:text-gray-400">
//             관리자 점검이 권장되는 디바이스를 예상 영향도 순으로 표시합니다
//           </CardDescription>
//         </CardHeader>
//         <CardContent>
//           {/* 상태별 필터 탭 */}
//           <Tabs value={statusFilter} onValueChange={handleStatusFilterChange} className="mb-4">
//             <TabsList className="dark:bg-gray-700">
//               <TabsTrigger value="ALL" className="dark:data-[state=active]:bg-gray-600">
//                 전체 ({devices.length})
//               </TabsTrigger>
//               <TabsTrigger value="CHECK_REQUIRED" className="dark:data-[state=active]:bg-gray-600">
//                 <AlertTriangle className="size-3 mr-1" />
//                 점검 권장 ({devices.filter((d) => d.deviceStatus === "CHECK_REQUIRED").length})
//               </TabsTrigger>
//               <TabsTrigger value="CHECKING" className="dark:data-[state=active]:bg-gray-600">
//                 <Clock className="size-3 mr-1" />
//                 점검 중 ({devices.filter((d) => d.deviceStatus === "CHECKING").length})
//               </TabsTrigger>
//               <TabsTrigger value="NORMAL" className="dark:data-[state=active]:bg-gray-600">
//                 <CheckCircle className="size-3 mr-1" />
//                 정상 ({devices.filter((d) => d.deviceStatus === "NORMAL").length})
//               </TabsTrigger>
//             </TabsList>
//           </Tabs>

//           <div className="border rounded-lg dark:border-gray-700">
//             <Table>
//               <TableHeader>
//                 <TableRow className="dark:border-gray-700 bg-gray-50 dark:bg-gray-900">
//                   <TableHead className="dark:text-gray-300">디바이스</TableHead>
//                   <TableHead className="dark:text-gray-300">위치</TableHead>
//                   <TableHead className="dark:text-gray-300">운영 상태</TableHead>
//                   <TableHead className="dark:text-gray-300">상태</TableHead>
//                   <TableHead className="dark:text-gray-300">예상 낭비</TableHead>
//                   <TableHead className="dark:text-gray-300">변화율</TableHead>
//                   <TableHead className="dark:text-gray-300">예상 절감액</TableHead>
//                   <TableHead className="dark:text-gray-300">작업</TableHead>
//                 </TableRow>
//               </TableHeader>
//               <TableBody>
//                 {paginatedDevices.map((device) => {
//                   const StatusIcon = getStatusIcon(device.deviceStatus);
//                   return (
//                     <TableRow key={device.deviceId} className="dark:border-gray-700">
//                       <TableCell className="dark:text-white">
//                         <div>
//                           <p className="font-medium">{device.deviceName}</p>
//                           <p className="text-xs text-gray-500 dark:text-gray-400">
//                             {device.deviceId}
//                           </p>
//                         </div>
//                       </TableCell>
//                       <TableCell className="text-sm dark:text-gray-300">
//                         {device.location}
//                       </TableCell>
//                       <TableCell>
//                         <Badge
//                           className={
//                             device.isOperating
//                               ? "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300"
//                               : "bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300"
//                           }
//                         >
//                           <Power className="size-3 mr-1" />
//                           {device.isOperating ? "ON" : "OFF"}
//                         </Badge>
//                       </TableCell>
//                       <TableCell>
//                         <Badge className={getStatusColor(device.deviceStatus)}>
//                           <StatusIcon className="size-3 mr-1" />
//                           {device.deviceStatus}
//                         </Badge>
//                       </TableCell>
//                       <TableCell className="dark:text-white">
//                         <div>
//                           <p className="font-medium">{device.estimatedWasteKwh} kWh</p>
//                           <p className="text-xs text-gray-500 dark:text-gray-400">월간 추정</p>
//                         </div>
//                       </TableCell>
//                       <TableCell>
//                         <Badge variant={device.monthChangeRate > 0 ? "destructive" : "default"}>
//                           {device.monthChangeRate > 0 ? "+" : ""}
//                           {device.monthChangeRate}%
//                         </Badge>
//                       </TableCell>
//                       <TableCell className="dark:text-white font-medium">
//                         ₩{Math.round(device.estimatedWasteCost).toLocaleString()}
//                       </TableCell>
//                       <TableCell>
//                         <div className="flex gap-2">
//                           <Button
//                             size="sm"
//                             variant="outline"
//                             onClick={() => handleViewDetail(device)}
//                             className="dark:border-gray-600 dark:text-gray-300"
//                           >
//                             <Eye className="size-3 mr-1" />
//                             상세
//                           </Button>
//                         </div>
//                       </TableCell>
//                     </TableRow>
//                   );
//                 })}
//               </TableBody>
//             </Table>
//           </div>

//           {/* 페이지네이션 */}
//           <div className="flex items-center justify-between mt-4">
//             <p className="text-sm text-gray-500 dark:text-gray-400">
//               총 {filteredDevices.length}개 중 {(currentPage - 1) * itemsPerPage + 1}-
//               {Math.min(currentPage * itemsPerPage, filteredDevices.length)}개 표시
//             </p>
//             <div className="flex items-center gap-2">
//               <Button
//                 variant="outline"
//                 size="sm"
//                 disabled={currentPage === 1}
//                 onClick={() => setCurrentPage((prev) => prev - 1)}
//                 className="dark:border-gray-600 dark:text-white"
//               >
//                 <ChevronLeft className="size-4" />
//               </Button>
//               {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
//                 const pageNum = currentPage <= 3 ? i + 1 : currentPage - 2 + i;
//                 if (pageNum > totalPages) return null;
//                 return (
//                   <Button
//                     key={pageNum}
//                     variant={currentPage === pageNum ? "default" : "outline"}
//                     size="sm"
//                     onClick={() => setCurrentPage(pageNum)}
//                     className="dark:border-gray-600"
//                   >
//                     {pageNum}
//                   </Button>
//                 );
//               })}
//               <Button
//                 variant="outline"
//                 size="sm"
//                 disabled={currentPage === totalPages}
//                 onClick={() => setCurrentPage((prev) => prev + 1)}
//                 className="dark:border-gray-600 dark:text-white"
//               >
//                 <ChevronRight className="size-4" />
//               </Button>
//             </div>
//           </div>
//         </CardContent>
//       </Card>

//       {/* 에너지 소비 패턴 분석 및 카테고리별 분석 */}
//       <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
//         {/* 에너지 소비 패턴 분석 */}
//         <Card className="dark:bg-gray-800 dark:border-gray-700">
//           <CardHeader>
//             <div className="flex items-center justify-between">
//               <div>
//                 <CardTitle className="dark:text-white">에너지 소비 패턴</CardTitle>
//                 <CardDescription className="dark:text-gray-400">
//                   실제/예상 사용량 비교
//                 </CardDescription>
//               </div>
//               <div className="flex gap-2">
//                 <Button
//                   size="sm"
//                   variant={patternPeriod === "daily" ? "default" : "outline"}
//                   onClick={() => handlePeriodChange("daily")}
//                   className="dark:border-gray-600"
//                 >
//                   일별
//                 </Button>
//                 <Button
//                   size="sm"
//                   variant={patternPeriod === "weekly" ? "default" : "outline"}
//                   onClick={() => handlePeriodChange("weekly")}
//                   className="dark:border-gray-600"
//                 >
//                   주별
//                 </Button>
//                 <Button
//                   size="sm"
//                   variant={patternPeriod === "monthly" ? "default" : "outline"}
//                   onClick={() => handlePeriodChange("monthly")}
//                   className="dark:border-gray-600"
//                 >
//                   월별
//                 </Button>
//               </div>
//             </div>
//           </CardHeader>
//           <CardContent>
//             <ResponsiveContainer width="100%" height={220}>
//               <AreaChart data={patternData}>
//                 <CartesianGrid strokeDasharray="3 3" className="dark:stroke-gray-700" />
//                 <XAxis
//                   dataKey="label"
//                   className="dark:fill-gray-300"
//                   style={{ fontSize: "12px" }}
//                 />
//                 <YAxis className="dark:fill-gray-300" style={{ fontSize: "12px" }} />
//                 <RechartsTooltip
//                   contentStyle={{
//                     backgroundColor: "#1f2937",
//                     border: "1px solid #374151",
//                     color: "#fff",
//                     borderRadius: "8px",
//                   }}
//                 />
//                 <Legend wrapperStyle={{ fontSize: "12px" }} />
//                 <Area
//                   type="monotone"
//                   dataKey="expected"
//                   stackId="1"
//                   stroke="#10b981"
//                   fill="#10b981"
//                   fillOpacity={0.3}
//                   name="예상 사용량"
//                 />
//                 <Area
//                   type="monotone"
//                   dataKey="actual"
//                   stackId="2"
//                   stroke="#2563eb"
//                   fill="#2563eb"
//                   fillOpacity={0.6}
//                   name="실제 사용량"
//                 />
//               </AreaChart>
//             </ResponsiveContainer>
//             <div className="mt-3 p-2 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
//               <p className="text-xs text-gray-700 dark:text-gray-300">
//                 <Info className="size-3 inline mr-1 text-blue-600 dark:text-blue-400" />
//                 <strong>분석:</strong> 비운영 시간대 사용량 증가는 일정 설정 비효율 또는 장비 오작동
//                 가능성을 시사합니다.
//               </p>
//             </div>
//           </CardContent>
//         </Card>

//         {/* 설비 유형별 에너지 분포 */}
//         <Card className="dark:bg-gray-800 dark:border-gray-700">
//           <CardHeader>
//             <CardTitle className="dark:text-white">설비 유형별 분포</CardTitle>
//             <CardDescription className="dark:text-gray-400">전체 에너지 소비 비율</CardDescription>
//           </CardHeader>
//           <CardContent>
//             <ResponsiveContainer width="100%" height={220}>
//               <PieChart>
//                 <Pie
//                   data={categoryData}
//                   cx="50%"
//                   cy="50%"
//                   labelLine={false}
//                   label={({ name, percentage }) => `${name} ${percentage}%`}
//                   outerRadius={80}
//                   fill="#8884d8"
//                   dataKey="value"
//                 >
//                   {categoryData.map((entry, index) => (
//                     <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
//                   ))}
//                 </Pie>
//                 <RechartsTooltip
//                   contentStyle={{
//                     backgroundColor: "#1f2937",
//                     border: "1px solid #374151",
//                     color: "#fff",
//                     borderRadius: "8px",
//                   }}
//                 />
//               </PieChart>
//             </ResponsiveContainer>
//           </CardContent>
//         </Card>
//       </div>

//       {/* 에너지 절감 성과 */}
//       <Card className="dark:bg-gray-800 dark:border-gray-700">
//         <CardHeader>
//           <CardTitle className="dark:text-white">에너지 절감 성과</CardTitle>
//           <CardDescription className="dark:text-gray-400">
//             관리자 조치 전후 소비량 비교
//           </CardDescription>
//         </CardHeader>
//         <CardContent>
//           <ResponsiveContainer width="100%" height={240}>
//             <LineChart data={savingsData}>
//               <CartesianGrid strokeDasharray="3 3" className="dark:stroke-gray-700" />
//               <XAxis dataKey="month" className="dark:fill-gray-300" style={{ fontSize: "12px" }} />
//               <YAxis className="dark:fill-gray-300" style={{ fontSize: "12px" }} />
//               <RechartsTooltip
//                 contentStyle={{
//                   backgroundColor: "#1f2937",
//                   border: "1px solid #374151",
//                   color: "#fff",
//                   borderRadius: "8px",
//                 }}
//               />
//               <Legend wrapperStyle={{ fontSize: "12px" }} />
//               <Line
//                 type="monotone"
//                 dataKey="조치전기준소비"
//                 stroke="#ef4444"
//                 strokeWidth={2}
//                 name="조치 전 기준 소비"
//                 dot={{ fill: "#ef4444" }}
//               />
//               <Line
//                 type="monotone"
//                 dataKey="조치후실제소비"
//                 stroke="#10b981"
//                 strokeWidth={2}
//                 name="조치 후 실제 소비"
//                 dot={{ fill: "#10b981" }}
//               />
//             </LineChart>
//           </ResponsiveContainer>
//           <div className="mt-4 grid grid-cols-2 gap-4">
//             <div className="p-3 bg-green-50 dark:bg-green-900/20 rounded-lg border border-green-200 dark:border-green-800">
//               <p className="text-xs text-gray-600 dark:text-gray-400">누적 절감액</p>
//               <p className="text-xl font-bold text-green-600 dark:text-green-400">₩1,245만</p>
//             </div>
//             <div className="p-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
//               <div className="flex items-center gap-1 mb-1">
//                 <p className="text-xs text-gray-600 dark:text-gray-400">추가 절감 가능 (추정)</p>
//                 <TooltipProvider>
//                   <Tooltip>
//                     <TooltipTrigger>
//                       <Info className="size-3 text-gray-400" />
//                     </TooltipTrigger>
//                     <TooltipContent className="dark:bg-gray-700 dark:border-gray-600 max-w-xs">
//                       <p className="text-xs">
//                         동일 설비 또는 동일 시간대 평균과의 비교를 통해 계산되었습니다.
//                       </p>
//                       <p className="text-xs mt-1">
//                         실제 절감액은 관리자의 조치 방식에 따라 달라질 수 있습니다.
//                       </p>
//                     </TooltipContent>
//                   </Tooltip>
//                 </TooltipProvider>
//               </div>
//               <p className="text-xl font-bold text-blue-600 dark:text-blue-400">
//                 ₩{potentialSavings.toLocaleString()}
//               </p>
//             </div>
//           </div>
//         </CardContent>
//       </Card>

//       {/* 정책 설정 모달 */}
//       <Dialog open={policyModal} onOpenChange={setPolicyModal}>
//         <DialogContent className="max-w-2xl dark:bg-gray-800 dark:border-gray-700">
//           <DialogHeader>
//             <DialogTitle className="dark:text-white">에너지 관리 정책 설정</DialogTitle>
//             <DialogDescription className="dark:text-gray-400">
//               시스템 분석 기준 파라미터를 설정합니다 (관리자 전용)
//             </DialogDescription>
//           </DialogHeader>

//           <div className="space-y-4">
//             <div className="grid grid-cols-2 gap-4">
//               <div className="space-y-2">
//                 <Label className="dark:text-gray-300">낭비 임계값 (kWh)</Label>
//                 <Input
//                   type="number"
//                   value={policySettings.wasteThreshold}
//                   onChange={(e) =>
//                     setPolicySettings({
//                       ...policySettings,
//                       wasteThreshold: parseInt(e.target.value),
//                     })
//                   }
//                   className="dark:bg-gray-700 dark:border-gray-600 dark:text-white"
//                 />
//                 <p className="text-xs text-gray-500 dark:text-gray-400">
//                   이 값을 초과하면 시스템이 점검을 권장합니다
//                 </p>
//               </div>

//               <div className="space-y-2">
//                 <Label className="dark:text-gray-300">변화율 임계값 (%)</Label>
//                 <Input
//                   type="number"
//                   value={policySettings.changeRateThreshold}
//                   onChange={(e) =>
//                     setPolicySettings({
//                       ...policySettings,
//                       changeRateThreshold: parseInt(e.target.value),
//                     })
//                   }
//                   className="dark:bg-gray-700 dark:border-gray-600 dark:text-white"
//                 />
//                 <p className="text-xs text-gray-500 dark:text-gray-400">전월 대비 증가율 기준</p>
//               </div>
//             </div>

//             <div className="grid grid-cols-2 gap-4">
//               <div className="space-y-2">
//                 <Label className="dark:text-gray-300">운영 시작 시간</Label>
//                 <Input
//                   type="time"
//                   value={policySettings.operatingHoursStart}
//                   onChange={(e) =>
//                     setPolicySettings({ ...policySettings, operatingHoursStart: e.target.value })
//                   }
//                   className="dark:bg-gray-700 dark:border-gray-600 dark:text-white"
//                 />
//               </div>

//               <div className="space-y-2">
//                 <Label className="dark:text-gray-300">운영 종료 시간</Label>
//                 <Input
//                   type="time"
//                   value={policySettings.operatingHoursEnd}
//                   onChange={(e) =>
//                     setPolicySettings({ ...policySettings, operatingHoursEnd: e.target.value })
//                   }
//                   className="dark:bg-gray-700 dark:border-gray-600 dark:text-white"
//                 />
//               </div>
//             </div>

//             <div className="space-y-3 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
//               <div className="flex items-center justify-between">
//                 <div>
//                   <p className="font-medium dark:text-white">자동 점검 권장 활성화</p>
//                   <p className="text-sm text-gray-500 dark:text-gray-400">
//                     임계값 초과 시 시스템이 자동으로 점검 권장 상태로 표시
//                   </p>
//                 </div>
//                 <input
//                   type="checkbox"
//                   checked={policySettings.autoInspectionEnabled}
//                   onChange={(e) =>
//                     setPolicySettings({
//                       ...policySettings,
//                       autoInspectionEnabled: e.target.checked,
//                     })
//                   }
//                   className="size-5"
//                 />
//               </div>

//               <div className="flex items-center justify-between">
//                 <div>
//                   <p className="font-medium dark:text-white">알림 활성화</p>
//                   <p className="text-sm text-gray-500 dark:text-gray-400">
//                     점검 권장 디바이스 발생 시 관리자에게 알림 전송
//                   </p>
//                 </div>
//                 <input
//                   type="checkbox"
//                   checked={policySettings.notificationEnabled}
//                   onChange={(e) =>
//                     setPolicySettings({ ...policySettings, notificationEnabled: e.target.checked })
//                   }
//                   className="size-5"
//                 />
//               </div>
//             </div>

//             {/* 고급 설정 섹션 */}
//             <div className="space-y-3 p-4 bg-blue-50/50 dark:bg-blue-900/10 rounded-lg border border-blue-200 dark:border-blue-800">
//               <div className="mb-3">
//                 <h3 className="font-medium text-blue-900 dark:text-blue-300">고급 설정</h3>
//                 <p className="text-xs text-blue-700 dark:text-blue-400 mt-1">
//                   세부 분석 기준을 설정합니다
//                 </p>
//               </div>

//               <div className="space-y-2">
//                 <Label className="dark:text-gray-300">비교 기준 선택</Label>
//                 <Select
//                   value={policySettings.comparisonBase}
//                   onValueChange={(value) =>
//                     setPolicySettings({ ...policySettings, comparisonBase: value })
//                   }
//                 >
//                   <SelectTrigger className="dark:bg-gray-700 dark:border-gray-600 dark:text-white w-full">
//                     <SelectValue />
//                   </SelectTrigger>
//                   <SelectContent className="dark:bg-gray-700 dark:border-gray-600">
//                     <SelectItem value="동일 설비 과거 평균" className="dark:text-white">
//                       동일 설비 과거 평균
//                     </SelectItem>
//                     <SelectItem value="동일 설비 유형 평균" className="dark:text-white">
//                       동일 설비 유형 평균
//                     </SelectItem>
//                     <SelectItem value="시간대별 평균" className="dark:text-white">
//                       시간대별 평균
//                     </SelectItem>
//                   </SelectContent>
//                 </Select>
//                 <p className="text-xs text-gray-500 dark:text-gray-400">
//                   에너지 사용량을 비교할 기준 데이터를 선택합니다
//                 </p>
//               </div>

//               <div className="space-y-2">
//                 <Label className="dark:text-gray-300">반복 감지 조건</Label>
//                 <div className="flex items-center gap-2">
//                   <Input
//                     type="number"
//                     min="1"
//                     value={policySettings.repeatDetectionCount}
//                     onChange={(e) =>
//                       setPolicySettings({
//                         ...policySettings,
//                         repeatDetectionCount: parseInt(e.target.value) || 1,
//                       })
//                     }
//                     className="dark:bg-gray-700 dark:border-gray-600 dark:text-white w-20"
//                   />
//                   <span className="text-sm dark:text-gray-300">회 /</span>
//                   <Input
//                     type="number"
//                     min="1"
//                     value={policySettings.repeatDetectionHours}
//                     onChange={(e) =>
//                       setPolicySettings({
//                         ...policySettings,
//                         repeatDetectionHours: parseInt(e.target.value) || 1,
//                       })
//                     }
//                     className="dark:bg-gray-700 dark:border-gray-600 dark:text-white w-20"
//                   />
//                   <span className="text-sm dark:text-gray-300">시간</span>
//                 </div>
//                 <p className="text-xs text-gray-500 dark:text-gray-400">
//                   설정된 시간 내 임계 초과가 반복되면 점검 권장 상태로 격상됩니다
//                 </p>
//               </div>

//               <div className="flex items-center justify-between pt-2">
//                 <div>
//                   <p className="font-medium dark:text-white">단발 초과 무시 옵션</p>
//                   <p className="text-sm text-gray-500 dark:text-gray-400">
//                     임계값을 1회만 초과한 경우에는 점검 판단에서 제외합니다
//                   </p>
//                 </div>
//                 <input
//                   type="checkbox"
//                   checked={policySettings.ignoreSingleViolation}
//                   onChange={(e) =>
//                     setPolicySettings({
//                       ...policySettings,
//                       ignoreSingleViolation: e.target.checked,
//                     })
//                   }
//                   className="size-5"
//                 />
//               </div>
//             </div>
//           </div>

//           <DialogFooter>
//             <Button
//               variant="outline"
//               onClick={() => setPolicyModal(false)}
//               className="dark:border-gray-600 dark:text-gray-300"
//             >
//               취소
//             </Button>
//             <Button onClick={handleSavePolicy} className="bg-blue-600 hover:bg-blue-700">
//               저장
//             </Button>
//           </DialogFooter>
//         </DialogContent>
//       </Dialog>

//       {/* 디바이스 상세 모달 */}
//       <Dialog open={detailModal} onOpenChange={setDetailModal}>
//         <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto dark:bg-gray-800 dark:border-gray-700">
//           <DialogHeader>
//             <DialogTitle className="dark:text-white">디바이스 상세 정보</DialogTitle>
//             <DialogDescription className="dark:text-gray-400">
//               {selectedDevice?.deviceName} - {selectedDevice?.location}
//             </DialogDescription>
//           </DialogHeader>

//           {selectedDevice && (
//             <Tabs defaultValue="analysis" className="mt-4">
//               <TabsList className="dark:bg-gray-700">
//                 <TabsTrigger value="analysis" className="dark:data-[state=active]:bg-gray-600">
//                   <Zap className="size-4 mr-2" />
//                   에너지 분석
//                 </TabsTrigger>
//                 <TabsTrigger value="history" className="dark:data-[state=active]:bg-gray-600">
//                   <Clock className="size-4 mr-2" />
//                   운영 이력
//                 </TabsTrigger>
//               </TabsList>

//               <TabsContent value="analysis" className="space-y-4 mt-4">
//                 {/* 디바이스 정보 */}
//                 <div className="grid grid-cols-2 gap-4 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
//                   <div>
//                     <p className="text-sm text-gray-500 dark:text-gray-400">디바이스 유형</p>
//                     <p className="font-medium dark:text-white">{selectedDevice.type}</p>
//                   </div>
//                   <div>
//                     <p className="text-sm text-gray-500 dark:text-gray-400">현재 상태</p>
//                     <Badge className={getStatusColor(selectedDevice.deviceStatus)}>
//                       {selectedDevice.deviceStatus}
//                     </Badge>
//                   </div>
//                   <div>
//                     <p className="text-sm text-gray-500 dark:text-gray-400">건물</p>
//                     <p className="font-medium dark:text-white">{selectedDevice.building}</p>
//                   </div>
//                   <div>
//                     <p className="text-sm text-gray-500 dark:text-gray-400">층/호실</p>
//                     <p className="font-medium dark:text-white">
//                       {selectedDevice.floor}, {selectedDevice.room}
//                     </p>
//                   </div>
//                 </div>

//                 {/* 에너지 지표 */}
//                 <Card className="dark:bg-gray-900 dark:border-gray-700">
//                   <CardHeader>
//                     <CardTitle className="text-lg dark:text-white">
//                       예상 에너지 낭비 (추정)
//                     </CardTitle>
//                     <CardDescription className="dark:text-gray-400 text-xs">
//                       시스템 분석 결과이며 실제 값과 차이가 있을 수 있습니다
//                     </CardDescription>
//                   </CardHeader>
//                   <CardContent className="space-y-4">
//                     <div className="grid grid-cols-3 gap-4">
//                       <div className="p-3 bg-red-50 dark:bg-red-900/20 rounded-lg border border-red-200 dark:border-red-800">
//                         <p className="text-sm text-gray-600 dark:text-gray-400">
//                           월간 낭비량 (추정)
//                         </p>
//                         <p className="text-2xl font-bold text-red-600 dark:text-red-400">
//                           {selectedDevice.estimatedWasteKwh} kWh
//                         </p>
//                       </div>
//                       <div className="p-3 bg-orange-50 dark:bg-orange-900/20 rounded-lg border border-orange-200 dark:border-orange-800">
//                         <p className="text-sm text-gray-600 dark:text-gray-400">변화율</p>
//                         <p className="text-2xl font-bold text-orange-600 dark:text-orange-400">
//                           {selectedDevice.monthChangeRate > 0 ? "+" : ""}
//                           {selectedDevice.monthChangeRate}%
//                         </p>
//                       </div>
//                       <div className="p-3 bg-green-50 dark:bg-green-900/20 rounded-lg border border-green-200 dark:border-green-800">
//                         <p className="text-sm text-gray-600 dark:text-gray-400">
//                           예상 절감액 (추정)
//                         </p>
//                         <p className="text-2xl font-bold text-green-600 dark:text-green-400">
//                           ₩{Math.round(selectedDevice.estimatedWasteCost).toLocaleString()}
//                         </p>
//                       </div>
//                     </div>
//                   </CardContent>
//                 </Card>

//                 {/* 예상 원인 */}
//                 {selectedDevice.causeEstimate && (
//                   <Card className="dark:bg-gray-900 dark:border-gray-700 border-l-4 border-l-yellow-500">
//                     <CardHeader>
//                       <CardTitle className="text-lg dark:text-white flex items-center gap-2">
//                         <AlertTriangle className="size-5 text-yellow-600 dark:text-yellow-400" />
//                         예상 원인 (추정)
//                       </CardTitle>
//                       <CardDescription className="dark:text-gray-400">
//                         시스템 자동 분석 결과이며 실제 원인과 다를 수 있습니다
//                       </CardDescription>
//                     </CardHeader>
//                     <CardContent>
//                       <p className="text-gray-700 dark:text-gray-300">
//                         {selectedDevice.causeEstimate}
//                       </p>
//                     </CardContent>
//                   </Card>
//                 )}

//                 {/* 권장 조치 */}
//                 <Card className="dark:bg-gray-900 dark:border-gray-700">
//                   <CardHeader>
//                     <CardTitle className="text-lg dark:text-white">권장 조치 사항</CardTitle>
//                     <CardDescription className="dark:text-gray-400">
//                       위 분석을 바탕으로 관리자가 고려할 수 있는 조치 방안입니다
//                     </CardDescription>
//                   </CardHeader>
//                   <CardContent>
//                     <ul className="space-y-2">
//                       <li className="flex items-start gap-2 text-sm dark:text-gray-300">
//                         <CheckCircle className="size-4 text-blue-600 dark:text-blue-400 mt-0.5 flex-shrink-0" />
//                         현장 점검을 통해 추정된 문제 확인
//                       </li>
//                       <li className="flex items-start gap-2 text-sm dark:text-gray-300">
//                         <CheckCircle className="size-4 text-blue-600 dark:text-blue-400 mt-0.5 flex-shrink-0" />
//                         운영 일정 검토 및 필요시 조정
//                       </li>
//                       <li className="flex items-start gap-2 text-sm dark:text-gray-300">
//                         <CheckCircle className="size-4 text-blue-600 dark:text-blue-400 mt-0.5 flex-shrink-0" />
//                         기계적 문제 또는 고장 여부 확인
//                       </li>
//                       <li className="flex items-start gap-2 text-sm dark:text-gray-300">
//                         <CheckCircle className="size-4 text-blue-600 dark:text-blue-400 mt-0.5 flex-shrink-0" />
//                         조치 후 48시간 모니터링하여 효과 측정
//                       </li>
//                     </ul>
//                   </CardContent>
//                 </Card>

//                 {/* 예상 효과 */}
//                 <Card className="dark:bg-gray-900 dark:border-gray-700 bg-gradient-to-r from-green-50 to-blue-50 dark:from-green-900/20 dark:to-blue-900/20">
//                   <CardHeader>
//                     <CardTitle className="text-lg dark:text-white">
//                       조치 시 예상 효과 (추정)
//                     </CardTitle>
//                   </CardHeader>
//                   <CardContent>
//                     <p className="text-gray-700 dark:text-gray-300">
//                       관리자가 추정된 문제를 해결할 경우 이 디바이스는 월 약{" "}
//                       <strong className="text-green-600 dark:text-green-400">
//                         ₩{Math.round(selectedDevice.estimatedWasteCost).toLocaleString()}
//                       </strong>{" "}
//                       ({selectedDevice.estimatedWasteKwh} kWh)를 절감할 가능성이 있으며, 전체 에너지
//                       효율 목표 달성에 기여할 수 있습니다.
//                     </p>
//                   </CardContent>
//                 </Card>

//                 {/* 관리자 작업 */}
//                 <div className="flex gap-3">
//                   {selectedDevice.deviceStatus === "CHECK_REQUIRED" && (
//                     <Button
//                       onClick={() => {
//                         handleStatusChange(selectedDevice.deviceId, "CHECKING");
//                         setDetailModal(false);
//                       }}
//                       className="flex-1 bg-blue-600 hover:bg-blue-700"
//                     >
//                       점검 중으로 변경
//                     </Button>
//                   )}
//                   {selectedDevice.deviceStatus === "CHECKING" && (
//                     <Button
//                       onClick={() => {
//                         handleStatusChange(selectedDevice.deviceId, "NORMAL");
//                         setDetailModal(false);
//                       }}
//                       className="flex-1 bg-green-600 hover:bg-green-700"
//                     >
//                       점검 완료
//                     </Button>
//                   )}
//                   <Button
//                     variant="outline"
//                     onClick={() => handleControlDevice(selectedDevice)}
//                     className="dark:border-gray-600 dark:text-gray-300"
//                   >
//                     <Power className="size-4 mr-2" />
//                     디바이스 제어: {selectedDevice.isOperating ? "OFF" : "ON"}
//                   </Button>
//                 </div>
//               </TabsContent>

//               <TabsContent value="history" className="mt-4">
//                 <Card className="dark:bg-gray-900 dark:border-gray-700">
//                   <CardHeader>
//                     <CardTitle className="text-lg dark:text-white">운영 이력</CardTitle>
//                     <CardDescription className="dark:text-gray-400">
//                       관리자의 최근 제어 작업 내역
//                     </CardDescription>
//                   </CardHeader>
//                   <CardContent>
//                     <div className="border rounded-lg dark:border-gray-700">
//                       <Table>
//                         <TableHeader>
//                           <TableRow className="dark:border-gray-700">
//                             <TableHead className="dark:text-gray-300">작업</TableHead>
//                             <TableHead className="dark:text-gray-300">관리자</TableHead>
//                             <TableHead className="dark:text-gray-300">시간</TableHead>
//                             <TableHead className="dark:text-gray-300">사유</TableHead>
//                           </TableRow>
//                         </TableHeader>
//                         <TableBody>
//                           {logs.map((log, index) => (
//                             <TableRow key={index} className="dark:border-gray-700">
//                               <TableCell>
//                                 <Badge
//                                   className={
//                                     log.afterState
//                                       ? "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300"
//                                       : "bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300"
//                                   }
//                                 >
//                                   <Power className="size-3 mr-1" />
//                                   {log.afterState ? "켜기" : "끄기"}
//                                 </Badge>
//                               </TableCell>

//                               <TableCell className="dark:text-gray-300">
//                                 <div className="flex items-center gap-2">
//                                   <User className="size-4" />
//                                   {log.adminName}
//                                 </div>
//                               </TableCell>

//                               <TableCell className="text-sm dark:text-gray-300">
//                                 <div className="flex items-center gap-2">
//                                   <Calendar className="size-4" />
//                                   {new Date(log.controlledAt).toLocaleString("ko-KR")}
//                                 </div>
//                               </TableCell>

//                               <TableCell className="text-sm dark:text-gray-300">
//                                 {log.reason}
//                               </TableCell>
//                             </TableRow>
//                           ))}
//                         </TableBody>
//                       </Table>
//                     </div>
//                   </CardContent>
//                 </Card>
//               </TabsContent>
//             </Tabs>
//           )}

//           <DialogFooter>
//             <Button
//               variant="outline"
//               onClick={() => setDetailModal(false)}
//               className="dark:border-gray-600 dark:text-gray-300"
//             >
//               닫기
//             </Button>
//           </DialogFooter>
//         </DialogContent>
//       </Dialog>
//     </div>
//   );
// }
// export default EnergyPage;
