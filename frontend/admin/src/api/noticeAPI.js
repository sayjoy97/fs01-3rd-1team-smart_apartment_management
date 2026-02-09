import { noticeMockData } from "@/mocks/exMocks";

// MOCK / REAL 전환 스위치
const USE_MOCK = true;

export async function getNotices() {
  if (USE_MOCK) return Promise.resolve(noticeMockData);
  throw new Error("getNotices: REAL API not implemented");
}

// 나중에 Swagger 연결 시
// const res = await axios.get("/api/notices");
// return res.data;

export async function getNoticeById(id) {
  if (USE_MOCK) {
    return Promise.resolve(noticeMockData.find((n) => n.id === Number(id)));
  }
  throw new Error("Not implemented");
}

export async function createNotice(data) {
  if (USE_MOCK) {
    return Promise.resolve({ id: Date.now(), ...data });
  }
  throw new Error("Not implemented");
}
