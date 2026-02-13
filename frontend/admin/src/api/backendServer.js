import axios from "axios";

export const API_SERVER_HOST = "http://localhost:9600";
const backendServer = axios.create({
  baseURL: API_SERVER_HOST,
  headers: {
    "Content-Type": "application/json",
  },
});

backendServer.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token && !config.url.includes("/login")) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default backendServer;
