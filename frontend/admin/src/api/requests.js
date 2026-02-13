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

  // -------- 시스템 --------
};

export default requests;
