import AsyncStorage from "@react-native-async-storage/async-storage";
import axios from "axios";

export const API_SERVER_HOST = "http://localhost:9600";

const jwtAxios = axios.create({
  baseURL: API_SERVER_HOST,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

// 요청 전 자동 토큰
jwtAxios.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem("accessToken");

    console.log("토큰:", token);
    // 토큰이 있으면 헤더에 추가
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    console.log("헤더:", config.headers);

    return config;
  },
  (err) => Promise.reject(err),
);

export default jwtAxios;
