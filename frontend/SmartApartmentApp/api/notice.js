import backendServer from "./backendServer";
import requests from "./requests";

// 전체 공지사항 조회
export const noticeAllList = async ({ page, size }) => {
  try {
    const params = {};
    params.size = size;
    params.page = page;

    const response = await backendServer.get(requests.noticeAllList, { params });

    return response.data;
  } catch (error) {
    console.error("전체 공지사항 호출 도중 에러발생: ", error);
    return [];
  }
};

// 고정 공지사항 조회
export const fixedNoticeList = async () => {
  try {
    const response = await backendServer.get(requests.fixedNoticeList);

    return response.data;
  } catch (error) {
    console.error("고정 공지사항 호출 도중 에러발생: ", error);
    return [];
  }
};

// 공지사항 상세조회
export const noticeDetail = async (notice_id) => {
  try {
    const response = await backendServer.get(requests.noticeDetail, {
      params: { notice_id: notice_id },
    });

    return response.data;
  } catch (error) {
    console.error("공지사항 상세정보 호출 도중 에러발생: ", error);
    return [];
  }
};
