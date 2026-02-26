import backendServer from "./backendServer";
import requests from "./requests";

// 최근 7일 유형별 카운트 조회
export const last7TypeCountList = async () => {
  try {
    const response = await backendServer.get(requests.lastWeekByTypeList);

    return response.data;
  } catch (error) {
    console.error("타입별 카운트 호출도중 에러발생: ", error);
    return [];
  }
};

// 백엔드 페이지네이션을 이용한 차량출입기록 전체기록 조회
export const getCargateRecordList = async ({ size, page }) => {
  try {
    const params = {};
    params.size = size;
    params.page = page;
    const response = await backendServer.get(requests.cargateRecordList, { params });

    return response.data;
  } catch (error) {
    console.error("차량 출입기록 호출도중 에러발생: ", error);
    return [];
  }
};

// 차량 출입기록 상세정보 조회
export const getLogDetail = async ({ cargate_event_log_id }) => {
  try {
    const params = {};
    params.cargate_event_log_id = cargate_event_log_id;

    const response = await backendServer.get(requests.entryExitLogDetail, { params });

    return response.data;
  } catch (error) {
    console.error(cargate_event_log_id, ": 차량 출입기록 상세정보 호출도중 에러발생: ", error);
    return [];
  }
};

// 출입기록 로그별 정보수정
export const getUpdateLogData = async ({ cargate_event_log_id }, updateData) => {
  try {
    const response = await backendServer.put(
      requests.updateLogData,
      updateData, // body
      { params: { cargate_event_log_id } }, // params
    );
    return response.data;
  } catch (error) {
    console.error("정보수정 실패:", error);
  }
};

// 차량 유형별 등록
export const carRegister = async (writeData) => {
  try {
    const response = await backendServer.post(requests.carRegisterByType, writeData);

    return response;
  } catch (error) {
    console.error("차량 유형별 등록도중 에러발생: ", error);
  }
};

// 세대 등록차량 조회
export const getRegisCarList = async ({ size, page }) => {
  try {
    const params = {};
    params.size = size;
    params.page = page;

    const response = await backendServer.get(requests.registeredCarList, { params });

    return response.data;
  } catch (error) {
    console.error("세대 등록차량 리스트 호출도중 에러발생: ", error);
    return [];
  }
};

// 세대 등록차량 상세정보 조회
export const getRegisCarDetail = async ({ vehicle_id }) => {
  try {
    const params = {};
    params.vehicle_id = vehicle_id;

    const response = await backendServer.get(requests.registeredCarDetail, { params });

    return response.data;
  } catch (error) {
    console.error(vehicle_id, ": 세대 등록차량 상세정보 호출도중 에러발생: ", error);
    return [];
  }
};

// 세대 등록차량 정보삭제
export const deleteRegisCar = async ({ vehicle_id }) => {
  try {
    const params = {};
    params.vehicle_id = vehicle_id;

    const response = await backendServer.delete(requests.deleteRegisteredCar, { params });

    return response;
  } catch (error) {
    console.error(vehicle_id, ": 세대 등록차량 정보삭제 도중 에러발생: ", error);
  }
};

// 관리자 승인차량 조회
export const getApproCarList = async ({ size, page }) => {
  try {
    const params = {};
    params.size = size;
    params.page = page;

    const response = await backendServer.get(requests.approvedCarList, { params });

    return response.data;
  } catch (error) {
    console.error("관리자 승인차량 리스트 호출도중 에러발생: ", error);
    return [];
  }
};

// 관리자 승인차량 상세정보 조회
export const getApproCarDetail = async ({ vehicle_id }) => {
  try {
    const params = {};
    params.vehicle_id = vehicle_id;

    const response = await backendServer.get(requests.approvedCarDetail, { params });

    return response.data;
  } catch (error) {
    console.error(vehicle_id, ": 관리자 승인차량 상세정보 호출도중 에러발생: ", error);
    return [];
  }
};

// 관리자 승인차량 수정
export const updateApproCar = async ({ vehicle_id }, updateData) => {
  try {
    const params = {};
    params.vehicle_id = vehicle_id;

    const response = await backendServer.put(requests.updateApprovedCar, { params }, updateData);

    return response;
  } catch (error) {
    console.error(vehicle_id, ": 관리자 승인차량 수정도중 에러발생: ", error);
  }
};

// 관리자 승인차량 삭제
export const deleteApproCar = async ({ vehicle_id }) => {
  try {
    const params = {};
    params.vehicle_id = vehicle_id;

    const response = await backendServer.delete(requests.deleteApprovedCar, { params });

    return response;
  } catch (error) {
    console.error(vehicle_id, ": 관리자 승인차량 삭제도중 에러발생: ", error);
  }
};
