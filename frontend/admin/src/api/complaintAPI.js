import backendServer from "./backendServer";
import request from "./requests";

// 민원 목록 조회
export const complaintAllList = async ({ category, status, page, size }) => {
  try {
    const params = {};
    if (category) params.category = category;
    if (status) params.status = status;
    params.page = page;
    params.size = size;

    const response = await backendServer.get(request.complaintList, { params });

    console.log("API응답: ", response.data);

    return response.data;
  } catch (error) {
    console.error("민원 목록 호출 중 에러발생: ", error);
    return [];
  }
};

// 민원 관리자 답변
export const answerWrite = async (answerData) => {
  try {
    const response = await backendServer.post(request.complaintAnswerWrite, answerData);

    return response;
  } catch (error) {
    console.error("민원 답변 중 에러발생: ", error);
    alert("민원 답변 작성중 에러가 발생했습니다.");
  }
};

// 민원 상세 조회
export const detailView = async (complaintId) => {
  try {
    const response = await backendServer.get(`/detail/${complaintId}`);

    return response;
  } catch (error) {
    console.error("민원 상세 조회 중 에러발생: ", error);
    alert("민원 상세 조회중 에러가 발생했습니다.");
  }
};
