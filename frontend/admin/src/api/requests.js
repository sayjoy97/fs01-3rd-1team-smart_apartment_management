import React from "react";

const requests = {
  // -------- 관리자 --------
  // 1) 로그인·로그아웃
  login: "/admin/api/login", // 로그인
  initialSetupAdmin: "/admin/api/${adminId}/initial-setup", // 관리자 최초 로그인 시 설정
  logout: (adminId) => `/admin/api/${adminId}/logout`, // 로그아웃

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

  // -------- 출입·보안 --------

  // -------- 시설 --------

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
};

export default requests;
