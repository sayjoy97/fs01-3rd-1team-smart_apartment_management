import React from "react";

const requests = {
  // 로그인
  loginAction: "/account/api/login",
  // 비밀번호 변경
  changePassword: "/account/api/password/change",
  // 로그인 유저 정보
  userInfo: "/account/api/house",

  // 민원
  complaintList: "/user/api/list", // 민원 목록
  complaintDetail: "/user/api/complaints", // 민원 상세 조회
  complaintWrite: "/user/api/write", // 민원 작성
  complaintDelete: "/user/api/delete", // 민원 삭제
  complaintUpdate: "/user/api/update", // 민원 수정
};

export default requests;
