import React from "react";

const requests = {
  // -------- 관리자 --------
  // 1) 로그인·로그아웃
  login: "/admin/api/login", // 로그인
  initialSetupAdmin: (adminId) => `/admin/api/${adminId}/initial-setup`, // 관리자 최초 로그인 시 설정
  logout: (adminId) => `/admin/api/${adminId}/logout`, // 로그아웃
  findPass: "/admin/api/find-pass", // 비밀번호 찾기
  changePass: "/admin/api/change-pass", // 비밀번호 변경

  // 2) 마이페이지
  adminInfo: (adminId) => `admin/api/${adminId}`, // 관리자 정보 조회
  adminInfoUpdate: (adminId) => `admin/api/${adminId}`, // 관리자 정보 수정
  logHistoryList: (adminId) => `admin/api/${adminId}/access-logs`, // 관리자 로그인 내역 조회

  // -------- 메인-대시보드 --------

  // -------- 세대·입주민 --------
  // 1) 세대 관리
  houseList: "/house/api/list", // 세대 목록 조회
  houseInsert: "/house/api/insert", // 세대 관리 (등록, 수정, 초기화)

  // 2) 민원 관리
  complaintList: "/complaint/api/list", // 민원 목록 조회
  complaintAnswerWrite: "/complaint/api/write", // 민원 관리자 답변
  complaintDetail: "/complaint/api/detail/${complaintId}",

  // 3) 공지사항
  noticeAllList: "notices/api/list", // 전체 공지사항 조회
  fixedNoticeList: "notices/api/fixed", // 고정 공지사항 조회
  noticeBySearch: "notices/api/search", // 타입별 리스트 조회
  noticeDetail: "notices/api/detail", // 공지사항 상세조회
  noticeWrite: "notices/api/write", // 공지사항 작성
  noticeUpdate: "notices/api/update", // 공지사항 수정
  noticeDelete: "notices/api/delete", // 공지사항 삭제
  noticeChangeFixStatus: "notices/api/fixStatus/change", // 공지사항 고정 상태변화

  // -------- 출입·보안 --------

  // 3-1) 차량 출입관리
  lastWeekByTypeList: "cargate/api/lastweek", // 최근 7일 유형별 카운트 조회
  cargateRecordList: "cargate/api/gateRecord/list", // 백엔드 페이지네이션을 이용한 차량출입기록 전체기록 조회
  entryExitLogDetail: "cargate/api/detail", // 차량 출입기록 상세정보 조회
  updateLogData: "cargate/api/update", // 출입기록 로그별 정보수정
  carRegisterByType: "cargate/api/register", // 차량 유형별 등록
  registeredCarList: "cargate/api/registeredCar/list", // 세대 등록차량 조회
  registeredCarDetail: "cargate/api/registeredCar/detail", // 세대 등록차량 상세정보 조회
  deleteRegisteredCar: "cargate/api/registeredCar/delete", // 세대 등록차량 정보삭제
  approvedCarList: "cargate/api/approvedCar/list", // 관리자 승인차량 조회
  approvedCarDetail: "cargate/api/approvedCar/detail", // 관리자 승인차량 상세정보 조회
  updateApprovedCar: "cargate/api/approvedCar/update", // 관리자 승인차량 수정
  deleteApprovedCar: "cargate/api/approvedCar/delete", // 관리자 승인차량 삭제

  // 3-2) 요금관리
  getSimpleCharge: "parkingfee/api/charge", // 간단 요금 조회
  getTotalList: "parkingfee/api/totalList", // 전체 요금 조회
  getDaily30TotalList: "parkingfee/api/runningTotal/daily", // 일별 30일 요금 조회
  getMonthly12TotalList: "parkingfee/api/runningTotal/monthly", // 월별 12개월 요금 조회
  getYearly3TotalList: "parkingfee/api/runningTotal/year", // 연별 3년 요금 조회
  getChargeSetting: "parkingfee/api/charge/setting", // 요금 설정 조회
  updateChargeSetting: "parkingfee/api/charge/setting/update", // 요금 설정 수정

  // -------- 시설 --------
  // 1) 엘리베이터 관리
  elevatorsStats: "/elevator/api/stats", // 엘리베이터 통계 정보 조회
  createElevator: (adminId) => `/elevator/api/admin/${adminId}`, // 엘리베이터 등록
  elevatorList: "/elevator/api/filter", // 엘리베이터 목록 조회
  elevatorDetail: (elevatorId) => `/elevator/api/${elevatorId}`, // 엘리베이터 상세 정보 조회
  updateElevatorState: (elevatorId) => `/elevator/api/${elevatorId}`, // 엘리베이터 상태 변경
  deleteElevator: (elevatorId, adminId) => `/elevator/api/${elevatorId}/admin/${adminId}`, // 엘리베이터 삭제

  // 2) 광고 관리
  createAdvertisement: (adminId) => `elevator/api/admin/${adminId}/advertisement`, // 광고 등록

  // -------- 환경·에너지 --------
  // 1) 층간소음 관리
  noiseDashboard: "/noise/api/dashboard",
  // 이벤트
  noiseEventList: "/noise/api/events", //목록
  noiseUrgentList: "/noise/api/urgent", //목록
  noiseEventDetail: (noiseEventId) => `/noise/api/event/${noiseEventId}`, //상세
  noiseEventObserve: (noiseEventId) => `/noise/api/event/${noiseEventId}/observe`, //상태변경
  noiseEventNotify: (noiseEventId) => `/noise/api/event/${noiseEventId}/notify`, //상태변경
  // 통계
  noiseStatistics: "/noise/api/statistics",
  // 상습 구간
  noiseHabitualZones: "/noise/api/habitual/zones",
  noiseHabitualZoneDetail: (zoneId) => `/noise/api/habitual/zones/${zoneId}`,
  noiseHabitualZoneClose: (zoneId) => `/noise/api/habitual/zones/${zoneId}/close`,
  noiseHabitualZoneRegister: "/noise/api/habitual/zones/register",
  noiseHabitualZoneCount: "/noise/api/habitual/zones/count",
  // 정책
  activeNoisePolicy: "/noise/api/policy/active",
  createNoisePolicy: "/noise/api/policy",

  // 2) 에너지 관리
  energyDashboard: "/energy/api/dashboard",

  energyPolicyActive: "/energy/api/policy/active",
  energyPolicyHistory: "/energy/api/policy/history",
  energyPolicyCreate: "/energy/api/policy",

  energyDeviceList: "/energy/api/devices",
  energyDeviceCheckRequired: "/energy/api/devices/check-required",

  energyDeviceDetail: (id) => `/energy/api/devices/${id}`,
  energyDeviceStartCheck: (id) => `/energy/api/devices/${id}/start-check`,
  energyDeviceCompleteCheck: (id) => `/energy/api/devices/${id}/complete-check`,
  energyDeviceControl: (id) => `/energy/api/devices/${id}/control`,
  energyDeviceControlLogs: (id) => `/energy/api/devices/${id}/control-logs`,
  energyDeviceSavingResults: (id) => `/energy/api/devices/${id}/saving-results`,

  energyUsageCreate: "/energy/api/usage",
  energyMeasurementCreate: "/energy/api/measurement",
  energyPattern: "/energy/api/pattern",
  energyCategory: "/energy/api/category",

  // -------- 시스템 --------
  // 1) 관리자 관리
  adminsStats: "/admin/api/stats", // 관리자 통계 정보 조회
  adminList: "/admin/api/filter", // 관리자 목록 조회
  createAdmin: "/admin/api", // 관리자 추가
  updateAdminRole: (adminId, targetAdminId) => `/admin/api/${adminId}/authority/${targetAdminId}`, // 관리자 역할 수정
  deleteAdmin: (adminId, targetAdminId) => `/admin/api/${adminId}/delete/${targetAdminId}`, // 관리자 삭제
};

export default requests;
