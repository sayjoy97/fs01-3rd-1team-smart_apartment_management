import React from "react";

const requests = {
  // 로그인

  // -------- 메인-대시보드 --------

  // -------- 세대·입주민 --------
  // 1) 세대 관리
  houseList: "/house/api/list", // 세대 목록 조회
  houseDetail: "/house/api/detail/${houseId}", // 세대 상세 조회
  houseInsert: "/house/api/insert", // 세대 관리 (등록, 수정, 초기화)

  // 2) 민원 관리
  complaintList: "/complaint/api/list", // 민원 목록 조회
  complaintAnswerWrite: "/complaint/api/write", // 민원 관리자 답변
  complaintDetail: "/complaint/api/detail/${complaintId}",

  // 3) 공지사항

  // -------- 출입·보안 --------
  // 1) 출입 관리
  entranceLog: "/entrance/api/log", // 공동현관 출입기록
  entraceDoorList: "/entrance/api/dong/list", // 세대별 공동 현관

  // -------- 시설 --------

  // -------- 환경·에너지 --------

  // -------- 시스템 --------
};

export default requests;
