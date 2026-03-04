import axios from "axios";

export const API_SERVER_HOST = "BROKER_URL";
const backendServer = axios.create({
  baseURL: API_SERVER_HOST,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

backendServer.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);
backendServer.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      console.warn("토큰 만료 또는 인증 실패 → 자동 로그아웃");

      localStorage.removeItem("accessToken");

      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);

backendServer.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response && error.response.status === 403 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const res = await backendServer.post("/admin/api/refresh");
        const newAccessToken = res.data.data.accessToken;
        console.log("새로운 액세스 토큰:", newAccessToken);

        localStorage.setItem("accessToken", newAccessToken);
        originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;

        return backendServer(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("adminId");
        localStorage.removeItem("roles");
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  },
);

export default backendServer;
