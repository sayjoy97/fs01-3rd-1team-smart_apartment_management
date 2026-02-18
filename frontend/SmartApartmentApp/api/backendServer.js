import AsyncStorage from "@react-native-async-storage/async-storage";
import axios from "axios";

export const API_SERVER_HOST = "http://localhost:9600";
const backendServer = axios.create({
  baseURL: API_SERVER_HOST,
  headers: {
    "Content-Type": "application/json",
  },
});

// JWT 자동 추가
backendServer.interceptors.request.use(async (config) => {
  const token = await AsyncStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default backendServer;
