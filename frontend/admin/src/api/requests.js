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

  // -------- 출입·보안 --------

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

  // -------- 시스템 --------
  // 1) 관리자 관리
  adminsStats: "/admin/api/stats", // 관리자 통계 정보 조회
  adminList: "/admin/api/filter", // 관리자 목록 조회
  createAdmin: "/admin/api", // 관리자 추가
  updateAdminRole: (adminId, targetAdminId) => `/admin/api/${adminId}/authority/${targetAdminId}`, // 관리자 역할 수정
  deleteAdmin: (adminId, targetAdminId) => `/admin/api/${adminId}/delete/${targetAdminId}`, // 관리자 삭제
};

export default requests;
