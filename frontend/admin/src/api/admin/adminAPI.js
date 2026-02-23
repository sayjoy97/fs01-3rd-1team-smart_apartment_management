import backendServer from "../backendServer";
import requests from "../requests";

// 관리자 로그인 API
export const login = async (loginForm) => {
  try {
    const response = await backendServer.post(requests.login, loginForm);

    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response.data);
    throw error;
  }
};

// 관리자 최초 로그인 시 설정 API
export const initialSetupAdmin = async (adminId, setupForm) => {
  try {
    const response = await backendServer.post(requests.initialSetupAdmin(adminId), setupForm);
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response.data);
    throw error;
  }
};

// 관리자 로그아웃 API
export const logout = async (adminId) => {
  try {
    const response = await backendServer.post(requests.logout(adminId));

    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 비밀번호 찾기 API
export const findPass = async (findPassForm) => {
  try {
    const response = await backendServer.post(requests.findPass, findPassForm);
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 비밀번호 변경 API
export const changePass = async (changePassForm) => {
  try {
    const response = await backendServer.put(requests.changePass, changePassForm);
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 관리자 정보 조회
export const getAdminInfo = async (adminId) => {
  try {
    const response = await backendServer.get(requests.adminInfo(adminId));

    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 관리자 정보 수정
export const adminInfoUpdate = async (adminId, updateForm) => {
  try {
    const response = await backendServer.put(requests.adminInfoUpdate(adminId), updateForm);

    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 관리자 로그인 내역 조회
export const logHistoryList = async (adminId, searchParams, pageable) => {
  try {
    const response = await backendServer.get(requests.logHistoryList(adminId), {
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

// 관리자 통계 정보 조회
export const getAdminsStats = async () => {
  try {
    const response = await backendServer.get(requests.adminsStats);
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 관리자 목록 조회
export const getAdminList = async (searchParams, pageable) => {
  try {
    const response = await backendServer.get(requests.adminList, {
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

// 관리자 추가
export const createAdmin = async (createAdminForm) => {
  try {
    const response = await backendServer.post(requests.createAdmin, createAdminForm);
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 관리자 역할 수정
export const updateAdminRole = async (adminId, targetAdminId, editRoleForm) => {
  try {
    const response = await backendServer.put(
      requests.updateAdminRole(adminId, targetAdminId),
      editRoleForm,
    );
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};

// 관리자 삭제
export const deleteAdmin = async (adminId, targetAdminId, deleteAdminForm) => {
  try {
    const response = await backendServer.delete(requests.deleteAdmin(adminId, targetAdminId), {
      data: deleteAdminForm,
    });
    return response.data;
  } catch (error) {
    console.log("에러 발생:", error.response?.data || error.message);
    throw error;
  }
};
