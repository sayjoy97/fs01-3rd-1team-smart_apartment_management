import backendServer from "./backendServer";
import request from "./requests";

// 세대 목록 조회
export const houseAllList = async ({ houseDong, houseHo, householderName }) => {
  try {
    const params = {};
    if (houseDong) params.houseDong = houseDong;
    if (houseHo) params.houseHo = houseHo;
    if (householderName) params.householderName = householderName;

    const response = await backendServer.get(request.houseList, { params });
    console.log("API응답: ", response.data);

    return response.data;
  } catch (error) {
    console.error("세대 목록 호출 중 에러발생: ", error);
    return [];
  }
};

// 세대 상세
export const houseDetail = async (houseId) => {
  try {
    const response = await backendServer.get(`/house/api/detail/${houseId}`);
    return response;
  } catch (error) {
    console.error("세대 상세 중 에러발생", error);
  }
};

// 세대 관리
export const hoouseManagement = async (houseId, inputData) => {
  const { houseId: _, ...body } = inputData;

  body.householdSize = Number(body.householdSize);

  if (typeof body.cardUid === "string") {
    body.cardUid = [body.cardUid];
  }
  try {
    const response = await backendServer.put(request.houseInsert, inputData, {
      params: { houseId },
    });

    return response.data;
  } catch (error) {
    const serverMsg = error.response?.data?.message;
    console.error("세대 관리 중 에러발생: ", serverMsg);

    throw new Error(serverMsg || "세대 등록 중 오류 발생");
  }
};
