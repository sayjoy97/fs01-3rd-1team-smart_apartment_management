import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { toast } from "sonner";

import {
  getNoiseDashboard,
  getNoiseEventList,
  getNoiseEventDetail,
  observeNoiseEvent,
  notifyNoiseEvent,
  getNoiseStatistics,
  registerHabitualZone,
  getActiveNoisePolicy,
  createNoisePolicy,
  getNoiseUrgentEvents,
} from "../../../api/noiseAPI";

function hhmmToMin(hhmm) {
  if (!hhmm) return null;
  const [h, m] = String(hhmm).slice(0, 5).split(":").map(Number);
  if (Number.isNaN(h) || Number.isNaN(m)) return null;
  return h * 60 + m;
}

/**
 * viewMode: all | day | night
 * eventFilter: all | unprocessed | notified
 */
export default function useNoisePage() {
  // ---------- DASHBOARD ----------
  const [dashboard, setDashboard] = useState(null);
  const [dashboardLoading, setDashboardLoading] = useState(false);
  const didInit = useRef(false);

  const loadDashboard = useCallback(async () => {
    setDashboardLoading(true);
    try {
      const res = await getNoiseDashboard();
      if (!res?.success) throw new Error("dashboard fail");
      setDashboard(res.data);
    } catch (e) {
      console.error(e);
      toast.error("대시보드를 불러오지 못했습니다.");
      setDashboard(null);
    } finally {
      setDashboardLoading(false);
    }
  }, []);

  // ---------- LIST ----------
  const [events, setEvents] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loadingList, setLoadingList] = useState(false);

  const [viewMode, setViewMode] = useState("all");
  const [eventFilter, setEventFilter] = useState("all");
  const lastListKeyRef = useRef("");

  const [listCounts, setListCounts] = useState({
    all: 0,
    unprocessed: 0,
    observing: 0,
    notified: 0,
  });
  const [countLoading, setCountLoading] = useState(false);

  const loadListCounts = useCallback(async () => {
    setCountLoading(true);
    try {
      // totalElements만 필요하니까 size=1로 충분
      const base = { viewMode, page: 0, size: 1, sort: "createdAt,desc" };

      const [a, u, o, n] = await Promise.all([
        getNoiseEventList({ ...base, status: undefined }),
        getNoiseEventList({ ...base, status: "UNPROCESSED" }),
        getNoiseEventList({ ...base, status: "OBSERVING" }),
        getNoiseEventList({ ...base, status: "NOTIFIED" }),
      ]);

      setListCounts({
        all: a?.data?.totalElements ?? 0,
        unprocessed: u?.data?.totalElements ?? 0,
        observing: o?.data?.totalElements ?? 0,
        notified: n?.data?.totalElements ?? 0,
      });
    } catch (e) {
      console.error(e);
      // 카운트는 실패해도 목록은 보이게(토스트는 optional)
      setListCounts((prev) => prev);
    } finally {
      setCountLoading(false);
    }
  }, [viewMode]);

  const loadEvents = useCallback(async () => {
    setLoadingList(true);
    try {
      const status =
        eventFilter === "all"
          ? undefined
          : eventFilter === "unprocessed"
            ? "UNPROCESSED"
            : eventFilter === "observing"
              ? "OBSERVING"
              : eventFilter === "notified"
                ? "NOTIFIED"
                : undefined;

      const res = await getNoiseEventList({
        status,
        viewMode,
        page,
        size: 10,
        sort: "createdAt,desc",
      });

      if (!res?.success) throw new Error("list fail");

      const pg = res.data;
      setEvents(pg?.content ?? []);
      setTotalPages(pg?.totalPages ?? 1);
      setTotalElements(pg?.totalElements ?? 0);
    } catch (e) {
      console.error(e);
      toast.error("소음 이벤트 목록을 불러오지 못했습니다.");
      setEvents([]);
      setTotalPages(1);
      setTotalElements(0);
    } finally {
      setLoadingList(false);
    }
  }, [eventFilter, page, viewMode]);

  useEffect(() => {
    const key = `${eventFilter}|${viewMode}|${page}`;
    if (lastListKeyRef.current === key) return; // 같은 조건이면 중복 호출 막기
    lastListKeyRef.current = key;

    loadEvents();
  }, [eventFilter, viewMode, page, loadEvents]);

  // ---------- URGENT (즉시 처리 필요 패널) ----------
  const [urgentEvents, setUrgentEvents] = useState([]);
  const [urgentLoading, setUrgentLoading] = useState(false);

  const [urgentPage, setUrgentPage] = useState(0);
  const [urgentTotalPages, setUrgentTotalPages] = useState(1);
  const urgentSize = 5;

  const loadUrgentEvents = useCallback(async () => {
    setUrgentLoading(true);
    try {
      const res = await getNoiseUrgentEvents(urgentPage, urgentSize, "createdAt,desc");
      if (!res?.success) throw new Error("urgent fail");

      const pg = res.data;
      setUrgentEvents(pg?.content ?? []);
      setUrgentTotalPages(pg?.totalPages ?? 1);
    } catch (e) {
      console.error(e);
      toast.error("즉시 처리 필요 목록을 불러오지 못했습니다.");
      setUrgentEvents([]);
      setUrgentTotalPages(1);
    } finally {
      setUrgentLoading(false);
    }
  }, [urgentPage]);

  useEffect(() => {
    loadUrgentEvents();
  }, [loadUrgentEvents]);

  // ---------- DETAIL (모달) ----------
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);

  const [adminMemo, setAdminMemo] = useState("");

  const openDetail = useCallback(async (noiseEventId) => {
    setShowDetailModal(true);
    setDetailLoading(true);
    setSelectedEvent(null);
    setAdminMemo("");

    try {
      const res = await getNoiseEventDetail(noiseEventId);
      if (!res?.success) throw new Error("detail fail");
      setSelectedEvent(res.data);
    } catch (e) {
      console.error(e);
      toast.error("상세 정보를 불러오지 못했습니다.");
      setShowDetailModal(false);
      setSelectedEvent(null);
    } finally {
      setDetailLoading(false);
    }
  }, []);

  const closeDetail = useCallback(() => {
    setShowDetailModal(false);
    setSelectedEvent(null);
    setDetailLoading(false);
    setAdminMemo("");
  }, []);

  // ---------- ACTIONS ----------
  const startObserving = useCallback(async () => {
    if (!selectedEvent?.noiseEventId) return;

    try {
      const res = await observeNoiseEvent(selectedEvent.noiseEventId, adminMemo);
      if (!res?.success) throw new Error("observe fail");

      toast.success("관찰 시작 처리되었습니다.");
      closeDetail();
      await Promise.all([loadDashboard(), loadEvents(), loadUrgentEvents(), loadListCounts()]);
    } catch (e) {
      console.error(e);
      toast.error("관찰 처리 실패");
    }
  }, [
    adminMemo,
    closeDetail,
    loadDashboard,
    loadEvents,
    loadUrgentEvents,
    loadListCounts,
    selectedEvent?.noiseEventId,
  ]);

  const sendNotification = useCallback(async () => {
    if (!selectedEvent?.noiseEventId) return;

    try {
      const res = await notifyNoiseEvent(selectedEvent.noiseEventId, adminMemo);
      if (!res?.success) throw new Error("notify fail");

      toast.success("알림 발송 완료");
      closeDetail();
      await Promise.all([loadDashboard(), loadEvents(), loadUrgentEvents()]);
    } catch (e) {
      console.error(e);
      toast.error("알림 발송 실패");
    }
  }, [
    adminMemo,
    closeDetail,
    loadDashboard,
    loadEvents,
    loadUrgentEvents,
    selectedEvent?.noiseEventId,
  ]);

  // 상습 구간 등록(상세에서만)
  const registerHabitual = useCallback(async () => {
    const pid = selectedEvent?.noiseEventProcessId;
    if (!pid) {
      toast.error("상습 등록에 필요한 processId가 없습니다.");
      return;
    }

    try {
      const res = await registerHabitualZone({
        noiseEventProcessId: pid,
        memo: adminMemo,
      });

      if (!res?.success) throw new Error("register fail");

      toast.success("상습 구간 등록 완료");
      closeDetail();
      await Promise.all([loadDashboard(), loadEvents(), loadUrgentEvents()]);
    } catch (e) {
      console.error(e);
      toast.error("상습 구간 등록 실패");
    }
  }, [
    adminMemo,
    closeDetail,
    loadDashboard,
    loadEvents,
    loadUrgentEvents,
    selectedEvent?.noiseEventProcessId,
  ]);

  // ---------- POLICY ----------
  const [policyOpen, setPolicyOpen] = useState(false);
  const [policyLoading, setPolicyLoading] = useState(false);
  const [activePolicy, setActivePolicy] = useState(null);

  const loadActivePolicy = useCallback(async () => {
    setPolicyLoading(true);
    try {
      const res = await getActiveNoisePolicy();
      if (!res?.success) throw new Error("policy load fail");
      setActivePolicy(res.data);
    } catch (e) {
      console.error(e);
      // activePolicy 없을 수도 있으니까 치명 toast는 상황 봐서
      toast.error("활성 정책을 불러오지 못했습니다.");
      setActivePolicy(null);
    } finally {
      setPolicyLoading(false);
    }
  }, []);

  // ---------- STATISTICS ----------
  const [statisticsLoading, setStatisticsLoading] = useState(false);
  const [statistics, setStatistics] = useState(null);

  const loadStatistics = useCallback(async () => {
    setStatisticsLoading(true);
    try {
      const res = await getNoiseStatistics();
      if (!res?.success) throw new Error("statistics fail");
      setStatistics(res.data);
    } catch (e) {
      console.error(e);
      toast.error("통계를 불러오지 못했습니다.");
      setStatistics(null);
    } finally {
      setStatisticsLoading(false);
    }
  }, []);

  // charts mapping (Map -> recharts array)
  const charts = useMemo(() => {
    const byHourMap = statistics?.noiseCountByHour ?? {};
    const breakHourMap = statistics?.policyBreakCountByHour ?? {};
    const sensorMap = statistics?.sensorTypeDistribution ?? {};
    const patternMap = statistics?.noisePatternDistribution ?? {};

    const hours = Array.from({ length: 24 }, (_, h) => h);

    const dayStart = hhmmToMin(activePolicy?.dayStartTime) ?? 6 * 60;
    const nightStart = hhmmToMin(activePolicy?.nightStartTime) ?? 22 * 60;

    const isDayHour = (h) => {
      const m = h * 60;
      if (dayStart < nightStart) return m >= dayStart && m < nightStart; // 일반(06~22)
      return m >= dayStart || m < nightStart; // 랩(22~06)
    };

    const allowHour = (h) => {
      if (viewMode === "all") return true;
      if (viewMode === "day") return isDayHour(h);
      if (viewMode === "night") return !isDayHour(h);
      return true;
    };

    const noiseByHour = hours.map((h) => ({
      hour: String(h),
      count: allowHour(h) ? Number(byHourMap?.[h] ?? 0) : 0, // ✅ 여기
    }));

    const breakByHour = hours.map((h) => ({
      hour: String(h),
      count: allowHour(h) ? Number(breakHourMap?.[h] ?? 0) : 0, // ✅ 여기
    }));

    const sensorPie = Object.entries(sensorMap).map(([name, value]) => ({
      name,
      value: Number(value ?? 0),
    }));

    const patternPie = Object.entries(patternMap).map(([name, value]) => ({
      name,
      value: Number(value ?? 0),
    }));

    return { noiseByHour, breakByHour, sensorPie, patternPie };
  }, [statistics, viewMode, activePolicy]);

  const openPolicy = useCallback(async () => {
    setPolicyOpen(true);
    await loadActivePolicy();
  }, [loadActivePolicy]);

  const savePolicy = useCallback(
    async (payload) => {
      try {
        setPolicyLoading(true);
        const res = await createNoisePolicy(payload);
        if (!res?.success) throw new Error("policy save fail");

        toast.success("정책이 저장/적용되었습니다.");
        setPolicyOpen(false);

        // ✅ 정책 바꾸면 timeZone/분석 결과가 달라질 수 있으니 즉시 갱신
        await Promise.all([loadDashboard(), loadEvents(), loadStatistics(), loadActivePolicy()]);
      } catch (e) {
        console.error(e);
        toast.error("정책 저장에 실패했습니다.");
      } finally {
        setPolicyLoading(false);
      }
    },
    [loadActivePolicy, loadDashboard, loadEvents, loadStatistics],
  );
  // 최초 로드
  useEffect(() => {
    if (didInit.current) return;
    didInit.current = true;

    loadDashboard();
    loadActivePolicy();
    loadStatistics();
    loadUrgentEvents();
    loadListCounts();
  }, [loadDashboard, loadActivePolicy, loadStatistics, loadUrgentEvents, loadListCounts]);

  useEffect(() => {
    loadStatistics();
    loadListCounts();
  }, [viewMode, loadStatistics, loadListCounts]);
  return {
    // dashboard
    dashboard,
    dashboardLoading,

    // list
    events,
    loadingList,
    page,
    totalPages,
    totalElements,
    viewMode,
    eventFilter,
    setViewMode,
    setEventFilter,
    setPage,

    // urgent
    urgentEvents,
    urgentLoading,
    urgentPage,
    urgentTotalPages,
    setUrgentPage,
    loadUrgentEvents,

    // detail
    showDetailModal,
    selectedEvent,
    detailLoading,
    adminMemo,
    setAdminMemo,
    openDetail,
    closeDetail,

    // actions
    startObserving,
    sendNotification,
    registerHabitual,

    // statistics
    statisticsLoading,
    charts,

    // policy
    policyOpen,
    setPolicyOpen,
    policyLoading,
    activePolicy,
    openPolicy,
    savePolicy,
    listCounts,
    countLoading,
  };
}
