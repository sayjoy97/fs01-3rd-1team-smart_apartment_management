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

// 타입별 리스트 조회
export const searchNocticeList = async ({ search_type, keyword, page, size }) => {
  try {
    const params = { search_type, keyword, page, size };

    const response = await backendServer.get(requests.noticeBySearch, { params });

    return response;
  } catch (error) {
    console.error("타입별 공지사항 리스트 호출 도중 에러발생: ", error);
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

// 공지사항 작성
export const noticeWrite = async (writeData) => {
  try {
    const response = await backendServer.post(requests.noticeWrite, writeData);

    return response;
  } catch (error) {
    console.error("공지사항 작성도중 에러발생: ", error);
    alert("공지사항 작성도중 에러가 발생했습니다.");
  }
};

// 공지사항 수정
export const noticeUpdate = async (updateData) => {
  try {
    const response = await backendServer.put(requests.noticeUpdate, updateData);

    return response;
  } catch (error) {
    console.error("공지사항 수정도중 에러발생: ", error);
    alert("공지사항 수정도중 에러가 발생했습니다.");
  }
};

// 공지사항 삭제
export const noticeDelete = async ({ notice_id }) => {
  try {
    const params = {};
    params.notice_id = notice_id;

    const response = await backendServer.delete(requests.noticeDelete, { params });

    return response;
  } catch (error) {
    console.error("공지사항 삭제도중 에러발생: ", error);
    alert("공지사항 삭제도중 에러가 발생했습니다.");
  }
};

// 공지사항 고정 상태변화
export const noticeFixedChange = async ({ notice_id }) => {
  try {
    console.log("공지사항 고정 상태변화 API 호출, notice_id: ", notice_id);

    const response = await backendServer.put(requests.noticeChangeFixStatus, null, {
      params: { notice_id: notice_id },
    });

    return response;
  } catch (error) {
    console.error("공지사항 고정상태 변화도중 에러발생: ", error);
    alert("공지사항 고정상태 변화도중 에러가 발생했습니다.");
  }
};
