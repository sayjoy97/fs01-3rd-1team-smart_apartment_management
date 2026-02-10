import backendServer from "./backendServer";
import request from "./requests";

// 세대 목록 조회
export const houseAllList = async ({ houseDong, houseHo, houseHolderName }) => {
  try {
    const params = {};
    if (houseDong) params.houseDong = houseDong;
    if (houseHo) params.houseHo = houseHo;
    if (houseHolderName) params.houseHolderName = houseHolderName;

    const response = await backendServer.get(request.houseList, { params });
    console.log("API응답: ", response.data);

    return response.data;
  } catch (error) {
    console.error("세대 목록 호출 중 에러발생: ", error);
    return [];
  }
};

// 세대 관리
export const hoouseManagement = async (houseId, inputData) => {
  try {
    const response = await backendServer.post(
      request.houseInsert,
      { inputData },
      {
        params: { houseId },
      },
    );

    return response.data;
  } catch (error) {
    console.error("세대 관리 중 에러발생: ", error);
  }
};
