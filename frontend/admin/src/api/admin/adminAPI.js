import backendServer from "../backendServer";
import requests from "../requests";

// 관리자 로그인 API
export const login = async (loginForm) => {
  try {
    const response = await backendServer.post(requests.login, loginForm);

    return response.data.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
  }
};

// 관리자 최초 로그인 시 설정 API

// 관리자 로그아웃 API
export const logout = async (adminId) => {
  try {
    const response = await backendServer.post(requests.logout, adminId);

    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
  }
};
