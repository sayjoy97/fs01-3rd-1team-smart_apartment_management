import backendServer from "./backendServer";
import requests from "./requests";

/* ==================================================
   1️⃣ 대시보드
================================================== */

export const getEnergyDashboard = async () => {
  const response = await backendServer.get(requests.energyDashboard);
  return response.data; // ApiResponse
};

/* ==================================================
   2️⃣ 정책
================================================== */

export const getActiveEnergyPolicy = async () => {
  const response = await backendServer.get(requests.energyPolicyActive);
  return response.data;
};

export const getEnergyPolicyHistory = async (page = 0) => {
  const response = await backendServer.get(requests.energyPolicyHistory, {
    params: { page, size: 10 },
  });
  return response.data;
};

export const createEnergyPolicy = async (requestBody) => {
  const response = await backendServer.post(requests.energyPolicyCreate, requestBody);
  return response.data;
};

/* ==================================================
   3️⃣ 설비 목록
================================================== */

export const getEnergyDeviceList = async (status, page = 0) => {
  const response = await backendServer.get(requests.energyDeviceList, {
    params: {
      status: status || undefined,
      page,
      size: 10,
    },
  });
  return response.data;
};

export const getCheckRequiredDevices = async (page = 0) => {
  const response = await backendServer.get(requests.energyDeviceCheckRequired, {
    params: { page, size: 10 },
  });
  return response.data;
};

/* ==================================================
   4️⃣ 설비 상세
================================================== */

export const getEnergyDeviceDetail = async (deviceId) => {
  const response = await backendServer.get(requests.energyDeviceDetail(deviceId));
  return response.data;
};

export const getEnergyDeviceControlLogs = async (deviceId) => {
  const response = await backendServer.get(requests.energyDeviceControlLogs(deviceId));
  return response.data;
};

export const getEnergyDeviceSavingResults = async (deviceId, page = 0) => {
  const response = await backendServer.get(requests.energyDeviceSavingResults(deviceId), {
    params: { page, size: 10 },
  });
  return response.data;
};

/* ==================================================
   5️⃣ 설비 상태 변경
================================================== */

export const startEnergyDeviceCheck = async (deviceId) => {
  const response = await backendServer.post(requests.energyDeviceStartCheck(deviceId));
  return response.data;
};

export const completeEnergyDeviceCheck = async (deviceId) => {
  const response = await backendServer.post(requests.energyDeviceCompleteCheck(deviceId));
  return response.data;
};

/* ==================================================
   6️⃣ 설비 ON/OFF 제어
================================================== */

export const controlEnergyDevice = async (deviceId, operate, reason) => {
  const response = await backendServer.post(requests.energyDeviceControl(deviceId), null, {
    params: {
      operate,
      reason,
    },
  });
  return response.data;
};

/* ==================================================
   7️⃣ 데이터 수집
================================================== */

export const createEnergyUsageSummary = async (requestBody) => {
  const response = await backendServer.post(requests.energyUsageCreate, requestBody);
  return response.data;
};

export const createEnergyMeasurement = async (requestBody) => {
  const response = await backendServer.post(requests.energyMeasurementCreate, requestBody);
  return response.data;
};
