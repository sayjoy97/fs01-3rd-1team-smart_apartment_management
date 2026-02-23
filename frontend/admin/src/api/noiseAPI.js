import backendServer from "./backendServer";
import requests from "./requests";

/* ==================================================
   1) Noise (이벤트)
================================================== */

// 대시보드
export const getNoiseDashboard = async () => {
  const res = await backendServer.get(requests.noiseDashboard);
  return res.data; // ApiResponse
};

// 즉시처리(긴급) 목록
export const getNoiseUrgentEvents = async (page = 0, size = 10, sort = "createdAt,desc") => {
  const res = await backendServer.get(requests.noiseUrgentList, {
    params: { page, size, sort },
  });
  return res.data; // ApiResponse(Page)
};

// 이벤트 상세
export const getNoiseEventDetail = async (noiseEventId) => {
  const res = await backendServer.get(requests.noiseEventDetail(noiseEventId));
  return res.data; // ApiResponse(Detail)
};

// 관찰 시작 (OBSERVING)
export const observeNoiseEvent = async (noiseEventId, adminMemo) => {
  const res = await backendServer.post(
    requests.noiseEventObserve(noiseEventId),
    { adminMemo }, // ✅ 컨트롤러가 @RequestBody NoiseEventDecisionRequest 받는 구조
  );
  return res.data; // ApiResponse
};

// 알림 발송 (NOTIFIED)
export const notifyNoiseEvent = async (noiseEventId, adminMemo) => {
  const res = await backendServer.post(requests.noiseEventNotify(noiseEventId), { adminMemo });
  return res.data; // ApiResponse
};

// 통계
export const getNoiseStatistics = async (date) => {
  const res = await backendServer.get(requests.noiseStatistics, {
    params: { date: date || undefined },
  });
  return res.data; // ApiResponse
};

/* ==================================================
   2) Habitual (상습 구간)
================================================== */

// 상단 카드 카운트
export const getHabitualZoneCounts = async () => {
  const res = await backendServer.get(requests.noiseHabitualZoneCount);
  return res.data; // ApiResponse({monitoringCount, closedCount})
};

// 상습 구간 목록
export const getHabitualZones = async ({
  status,
  page = 0,
  size = 10,
  sort = "createdAt,desc",
} = {}) => {
  const res = await backendServer.get(requests.noiseHabitualZones, {
    params: { status: status || undefined, page, size, sort },
  });
  return res.data; // ApiResponse(Page)
};

// 상습 구간 상세 (모달)
export const getHabitualZoneDetail = async (zoneId) => {
  const res = await backendServer.get(requests.noiseHabitualZoneDetail(zoneId));
  return res.data; // ApiResponse(Detail)
};

// 상습 구간 등록 (수동)
export const registerHabitualZone = async ({ noiseEventProcessId, memo }) => {
  const res = await backendServer.post(requests.noiseHabitualZoneRegister, null, {
    params: {
      noiseEventProcessId,
      memo: memo || undefined,
    },
  });
  return res.data; // ApiResponse
};

// 상습 구간 종료
export const closeHabitualZone = async ({ zoneId, memo }) => {
  const res = await backendServer.post(requests.noiseHabitualZoneClose(zoneId), null, {
    params: {
      memo: memo || undefined,
    },
  });
  return res.data; // ApiResponse
};

export const getNoiseEventList = async ({
  status,
  viewMode = "all",
  page = 0,
  size = 10,
  sort = "createdAt,desc",
} = {}) => {
  const res = await backendServer.get(requests.noiseEventList, {
    params: {
      status: status || undefined,
      viewMode,
      page,
      size,
      sort,
    },
  });

  return res.data; // ApiResponse<Page<NoiseEventListResponse>>
};

// 정책 조회
export const getActiveNoisePolicy = async () => {
  const res = await backendServer.get(requests.activeNoisePolicy);
  return res.data;
};

// 정책 생성/적용
export const createNoisePolicy = async (payload) => {
  const res = await backendServer.post(requests.createNoisePolicy, payload);
  return res.data;
};
