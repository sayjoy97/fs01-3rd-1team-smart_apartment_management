import backendServer from "./backendServer";
import request from "./requests";

// 세대별 공동 현관
export const getEntranceDoor = async () => {
  try {
    const response = await backendServer.get(`${request.entraceDoorList}`);
    return response.data;
  } catch (error) {
    console.error("에러발생: ", error);
    return [];
  }
};

// 공동현관 상태 변경
export const changeStatusDoor = async (doorId, status) => {
  try {
    const url = request.entranceStatusChange.replace("{doorId}", doorId);

    const response = await backendServer.patch(url, { status });
    return response.data;
  } catch (error) {
    console.error("에러발생: ", error);
    return [];
  }
};

// 공동현관 출입기록
export const getEntranceLog = async ({ houseDong, accessType, page, size }) => {
  try {
    const params = {};
    if (houseDong) params.houseDong = houseDong;
    if (accessType) params.accessType = accessType;
    params.page = page;
    params.size = size;

    const response = await backendServer.get(request.entranceLog, { params });

    console.log("API응답: ", response.data);

    return response.data;
  } catch (error) {
    console.error("공동현관 출입 기록 호출 중 에러발생: ", error);
    return [];
  }
};
