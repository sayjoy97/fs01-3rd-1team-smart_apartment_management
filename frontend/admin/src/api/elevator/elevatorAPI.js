import backendServer from "../backendServer";
import requests from "../requests";

// 엘리베이터 통계 정보 조회 API
export const getElevatorsStats = async () => {
  try {
    const response = await backendServer.get(requests.elevatorsStats);
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 엘리베이터 등록 API
export const createElevator = async (adminId, createElevatorForm) => {
  try {
    const response = await backendServer.post(requests.createElevator(adminId), createElevatorForm);
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 엘리베이터 목록 조회 API
export const getElevatorList = async (searchParams, pageable) => {
  try {
    const response = await backendServer.get(requests.elevatorList, {
      params: {
        ...searchParams,
        page: pageable.page,
        size: pageable.size,
        sort: pageable.sort,
      },
    });
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 엘리베이터 상세 정보 조회 API
export const getElevatorDetail = async (elevatorId, searchParams, pageable) => {
  try {
    const response = await backendServer.get(requests.elevatorDetail(elevatorId), {
      params: {
        ...searchParams,
        page: pageable.page,
        size: pageable.size,
        sort: pageable.sort,
      },
    });
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 엘리베이터 상태 변경 API
export const updateElevatorState = async (elevatorId, elevatorState) => {
  try {
    const response = await backendServer.put(requests.updateElevatorState(elevatorId), null, {
      params: {
        state: elevatorState,
      },
    });
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 엘리베이터 삭제 API
export const deleteElevator = async (elevatorId, adminId, deleteElevatorForm) => {
  try {
    const response = await backendServer.delete(requests.deleteElevator(elevatorId, adminId), {
      data: deleteElevatorForm,
    });
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 광고 등록 API
export const createAdvertisement = async (adminId, createAdvertisementForm) => {
  try {
    const response = await backendServer.post(
      requests.createAdvertisement(adminId),
      createAdvertisementForm,
    );
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};
