import axios from "axios";
import request from "./requests";
import backendServer from "./backendServer";

export const getMyHouseApi = async () => {
  const res = await backendServer.get(request.userInfo);

  return res.data;
};
