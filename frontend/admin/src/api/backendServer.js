import axios from "axios";

export const API_SERVER_HOST = "http://localhost:9600";
const backendServer = axios.create({
  baseURL: API_SERVER_HOST,
  headers: {
    "Content-Type": "application/json",
  },
});

export default backendServer;
