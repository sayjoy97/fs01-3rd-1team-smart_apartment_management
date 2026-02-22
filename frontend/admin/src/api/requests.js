import React from "react";

const requests = {
  // 로그인

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

  // 3) 차량 출입관리
  lastWeekByTypeList: "cargate/api/lastweek", // 최근 7일 유형별 카운트 조회
  cargateRecordList: "cargate/api/gateRecord/list", // 백엔드 페이지네이션을 이용한 차량출입기록 전체기록 조회
  entryExitLogDetail: "cargate/api/update", // 차량 출입기록 상세정보 조회
  updateLogData: "cargate/api/update", // 출입기록 로그별 정보수정
  carRegisterByType: "cargate/api/register", // 차량 유형별 등록
  registeredCarList: "cargate/api/registeredCar/list", // 세대 등록차량 조회
  registeredCarDetail: "cargate/api/registeredCar/detail", // 세대 등록차량 상세정보 조회
  deleteRegisteredCar: "cargate/api/registeredCar/delete", // 세대 등록차량 정보삭제
  approvedCarList: "cargate/api/approvedCar/list", // 관리자 승인차량 조회
  approvedCarDetail: "cargate/api/approvedCar/detail", // 관리자 승인차량 상세정보 조회
  updateApprovedCar: "cargate/api/approvedCar/update", // 관리자 승인차량 수정
  deleteApprovedCar: "cargate/api/approvedCar/delete", // 관리자 승인차량 삭제

  // -------- 시설 --------

  // -------- 환경·에너지 --------

  // -------- 시스템 --------
};

export default requests;
