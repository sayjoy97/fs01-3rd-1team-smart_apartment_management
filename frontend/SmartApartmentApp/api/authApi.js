import jwtAxios from "@/utils/jwtAxios";
import backendServer from "./backendServer";
import requests from "./requests";
import axios from "axios";

// 로그인 api
export const loginApi = async (payload) => {
  const res = await backendServer.post(requests.loginAction, payload, {
    validateStatus: () => true,
  });

  if (res.data.error) {
    throw new Error(res.data.error.message || "로그인 실패");
  }

  // accessToken 없으면 실패 처리
  if (!res.data.accessToken) {
    throw new Error("로그인 토큰이 없습니다.");
  }

  return res.data;
};

// 비밀번호 변경 api
export const changePasswordApi = async (data) => {
  const res = await jwtAxios.post(requests.changePassword, data);
  return res.data;
};
