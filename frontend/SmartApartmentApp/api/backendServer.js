import AsyncStorage from "@react-native-async-storage/async-storage";
import axios from "axios";

export const API_SERVER_HOST = "http://localhost:9600"; // 서버 주소
const backendServer = axios.create({
  baseURL: API_SERVER_HOST,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

// 요청 인터셉터: Access Token 자동 추가
backendServer.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// 토큰 만료 처리 및 재발급
backendServer.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Access Token 만료
    if (error.response?.status === 403 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = await AsyncStorage.getItem("refreshToken");
        if (!refreshToken) throw new Error("Refresh Token 없음");

        // Refresh Token을 쿠키나 헤더로 서버에 전달
        const res = await backendServer.post("/account/api/refresh", null, {
          headers: {
            Cookie: `refreshToken=${refreshToken}`, // RN에서는 쿠키 직접 지정
          },
        });

        const newAccessToken = res.data.data.accessToken;
        await AsyncStorage.setItem("accessToken", newAccessToken);

        // 원래 요청 헤더에 새 토큰 적용 후 재요청
        originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;
        return backendServer(originalRequest);
      } catch (refreshError) {
        // 재발급 실패 시 로그아웃 처리
        await AsyncStorage.removeItem("accessToken");
        await AsyncStorage.removeItem("refreshToken");
        console.warn("Refresh Token 실패 → 로그아웃 처리 필요");
        // 필요 시 네비게이션으로 로그인 화면 이동
      }
    }

    // 인증 실패 → 401 처리
    if (error.response?.status === 401) {
      await AsyncStorage.removeItem("accessToken");
      await AsyncStorage.removeItem("refreshToken");
      console.warn("인증 실패 → 로그아웃 처리 필요");
      // 필요 시 네비게이션으로 로그인 화면 이동
    }

    return Promise.reject(error);
  },
);

export default backendServer;
