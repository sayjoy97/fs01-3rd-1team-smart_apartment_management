import { useCallback, useState } from "react";
import { toast } from "sonner";
import {
  getHabitualZoneCounts,
  getHabitualZones,
  getHabitualZoneDetail,
  closeHabitualZone,
  notifyNoiseEvent, // ✅ 추가
} from "../../../api/noiseAPI";

export default function useHabitualPage() {
  const [counts, setCounts] = useState(null);
  const [countsLoading, setCountsLoading] = useState(false);

  const [zones, setZones] = useState([]);
  const [listLoading, setListLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const [detailOpen, setDetailOpen] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detail, setDetail] = useState(null);

  const loadCounts = useCallback(async () => {
    setCountsLoading(true);
    try {
      const res = await getHabitualZoneCounts();
      if (!res?.success) throw new Error("count fail");
      setCounts(res.data);
    } catch (e) {
      console.error(e);
      toast.error("상습 구간 카운트를 불러오지 못했습니다.");
      setCounts(null);
    } finally {
      setCountsLoading(false);
    }
  }, []);

  const loadZones = useCallback(async () => {
    setListLoading(true);
    try {
      const status = statusFilter === "ALL" ? undefined : statusFilter;

      const res = await getHabitualZones({
        status,
        page,
        size: 10,
        sort: "createdAt,desc",
      });

      if (!res?.success) throw new Error("list fail");

      const pg = res.data;
      setZones(Array.isArray(pg?.content) ? pg.content : []);
      setTotalPages(Number(pg?.totalPages ?? 1));
      setTotalElements(Number(pg?.totalElements ?? 0));
    } catch (e) {
      console.error(e);
      toast.error("상습 구간 목록을 불러오지 못했습니다.");
      setZones([]);
      setTotalPages(1);
      setTotalElements(0);
    } finally {
      setListLoading(false);
    }
  }, [page, statusFilter]);

  const openDetail = useCallback(async (zoneId) => {
    setDetailOpen(true);
    setDetailLoading(true);
    setDetail(null);

    try {
      const res = await getHabitualZoneDetail(zoneId);
      if (!res?.success) throw new Error("detail fail");
      setDetail(res.data);
    } catch (e) {
      console.error(e);
      toast.error("상습 구간 상세를 불러오지 못했습니다.");
      setDetail(null);
    } finally {
      setDetailLoading(false);
    }
  }, []);

  const closeDetail = useCallback(() => {
    setDetailOpen(false);
    setDetailLoading(false);
    setDetail(null);
  }, []);

  // ✅ 알림 발송: 상습구간 API가 아니라, 최신 noiseEventId로 기존 notify API 호출
  const sendNotification = useCallback(async ({ noiseEventId, memo }) => {
    try {
      if (!noiseEventId) throw new Error("no noiseEventId");
      const res = await notifyNoiseEvent(noiseEventId, memo || "");
      if (!res?.success) throw new Error("notify fail");
      toast.success("알림 발송 완료");
    } catch (e) {
      console.error(e);
      toast.error("알림 발송 실패");
    }
  }, []);

  // ✅ 종료: adminId 없음 (JWT에서 서버가 알아서 adminLoginId 추출)
  const endMonitoring = useCallback(
    async ({ zoneId, memo }) => {
      try {
        const res = await closeHabitualZone({ zoneId, memo });
        if (!res?.success) throw new Error("close fail");

        toast.success("모니터링이 종료되었습니다.");
        closeDetail();

        await Promise.all([loadCounts(), loadZones()]);
      } catch (e) {
        console.error(e);
        toast.error("모니터링 종료에 실패했습니다.");
      }
    },
    [closeDetail, loadCounts, loadZones],
  );

  return {
    counts,
    countsLoading,
    loadCounts,

    zones,
    listLoading,
    statusFilter,
    setStatusFilter,
    page,
    setPage,
    totalPages,
    totalElements,
    loadZones,

    detailOpen,
    detailLoading,
    detail,
    openDetail,
    closeDetail,

    // actions
    endMonitoring,
    sendNotification,
  };
}
