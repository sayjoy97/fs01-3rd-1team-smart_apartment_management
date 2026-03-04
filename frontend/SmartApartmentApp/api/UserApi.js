import backendServer from "./backendServer";
import request from "./requests";

// 유저 정보
export const getMyHouseApi = async () => {
  const res = await backendServer.get(request.userInfo);

  return res.data;
};

// 민원 목록 조회
export const complaintListApi = async () => {
  const res = await backendServer.get(request.complaintList);

  return res.data;
};

// 민원
export const complaintDetailApi = async (complaintId) => {
  const res = await backendServer.get(`${request.complaintDetail}/${complaintId}`);
  return res.data;
};
// 민원 작성
export const complaintWrtieApi = async (data) => {
  const res = await backendServer.post(request.complaintWrite, data);
  return res.data;
};

// 민원 삭제
export const complaintDeleteApi = async (complaintId) => {
  const res = await backendServer.delete(`${request.complaintDelete}?complaintId=${complaintId}`, {
    params: { complaintId },
  });
  return res.data;
};

// 민원 수정
export const complaintUpdateApi = async (complaintId, data) => {
  const res = await backendServer.put(
    `${request.complaintUpdate}?complaintId=${complaintId}`,
    data,
    { params: { complaintId } },
  );
  return res.data;
};
