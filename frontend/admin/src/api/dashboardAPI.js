import backendServer from "./backendServer";
import requests from "./requests";

// 대시보드 - 최근 공동현관 출입 로그 3개
export const getRecentEntranceLogs = async (cond = {}) => {
  const response = await backendServer.get(requests.entranceLog, {
    params: {
      ...cond, // (선택) houseDong, houseHo, accessType 등 검색조건
      page: 0,
      size: 3,
    },
  });
  return response.data; // Map: { content, totalPages, totalElements, page }
};

// 대시보드 - 차량 요금 요약(금일누적/월평균/월평균방문차량)
export const getParkingFeeSummary = async () => {
  const response = await backendServer.get(requests.getTotalList);
  return response.data; // ApiResponse.success(allInOneChargeView)
};

// 대시보드 - 에너지 관리기능 요약
export const getEnergyDashboard = async () => {
  const response = await backendServer.get(requests.energyDashboard);
  return response.data; // ApiResponse.success(...)
};

// 관리자 정보 정리 카드
export const getMyAdminSummary = async () => {
  const adminId = localStorage.getItem("adminId");
  const response = await backendServer.get(requests.adminInfo(adminId));
  return response.data; // ApiResponse.success(MyPageRes)
};

// 고정 공지
export const getFixedNotices = async () => {
  const response = await backendServer.get(requests.fixedNoticeList);
  return response.data; // ApiResponse
};

// 미답변 민원 최신 N개 (status=WAITING, size=N)
// cond는 쿼리스트링으로 자동 바인딩 됨 (Spring)
export const getWaitingComplaints = async (size = 5) => {
  const response = await backendServer.get(requests.complaintList, {
    params: {
      status: "WAITING",
      page: 0,
      size,
      // 필요하면 정렬 파라미터 추가 (백엔드가 지원하면)
      // sort: "createdAt,desc"
    },
  });
  return response.data; // Map or ApiResponse 형태 (너 백엔드가 Map으로 주는 듯)
};
