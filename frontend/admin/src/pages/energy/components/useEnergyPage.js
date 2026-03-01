// src/pages/energy/components/useEnergyPage.js
import { useEffect, useState, useCallback } from "react";
import {
  getEnergyDashboard,
  getEnergyDeviceList,
  getEnergyDeviceDetail,
  getEnergyDeviceControlLogs,
  getEnergyDeviceSavingResults,
  startEnergyDeviceCheck,
  completeEnergyDeviceCheck,
  controlEnergyDevice,
  getActiveEnergyPolicy,
} from "../../../api/energyAPI";

export default function useEnergyPage() {
  // dashboard
  const [dashboard, setDashboard] = useState(null);
  const [dashboardLoading, setDashboardLoading] = useState(false);

  // list
  const [devices, setDevices] = useState([]);
  const [devicesLoading, setDevicesLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState("ALL"); // ALL | CHECK_REQUIRED | CHECKING | NORMAL
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // detail
  const [detailOpen, setDetailOpen] = useState(false);
  const [detail, setDetail] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [controlLogs, setControlLogs] = useState([]);
  const [savingResults, setSavingResults] = useState({ content: [], totalPages: 0, number: 0 });

  // policy
  const [activePolicy, setActivePolicy] = useState(null);
  const [policyLoading, setPolicyLoading] = useState(false);

  const [counts, setCounts] = useState({
    all: 0,
    checkRequired: 0,
    checking: 0,
    normal: 0,
  });

  const loadCounts = async () => {
    try {
      const [allRes, crRes, cRes, nRes] = await Promise.all([
        getEnergyDeviceList(null, 0, 1),
        getEnergyDeviceList("CHECK_REQUIRED", 0, 1),
        getEnergyDeviceList("CHECKING", 0, 1),
        getEnergyDeviceList("NORMAL", 0, 1),
      ]);

      setCounts({
        all: allRes?.success ? (allRes.data?.totalElements ?? 0) : 0,
        checkRequired: crRes?.success ? (crRes.data?.totalElements ?? 0) : 0,
        checking: cRes?.success ? (cRes.data?.totalElements ?? 0) : 0,
        normal: nRes?.success ? (nRes.data?.totalElements ?? 0) : 0,
      });
    } catch (e) {
      console.error("카운트 로딩 실패:", e);
    }
  };

  const loadDashboard = useCallback(async () => {
    try {
      setDashboardLoading(true);
      const res = await getEnergyDashboard();
      if (res?.success) setDashboard(res.data);
    } catch (e) {
      console.error("대시보드 로딩 실패:", e);
    } finally {
      setDashboardLoading(false);
    }
  }, []);

  const loadDevices = useCallback(
    async (nextStatus = statusFilter, nextPage = page) => {
      try {
        setDevicesLoading(true);
        const statusParam = nextStatus === "ALL" ? null : nextStatus;
        const res = await getEnergyDeviceList(statusParam, nextPage);

        if (res?.success) {
          const pageData = res.data;
          setDevices(pageData?.content ?? []);
          setTotalPages(pageData?.totalPages ?? 0);
          setTotalElements(pageData?.totalElements ?? 0);
          setPage(pageData?.number ?? nextPage);
        }
      } catch (e) {
        console.error("설비 목록 로딩 실패:", e);
      } finally {
        setDevicesLoading(false);
      }
    },
    [statusFilter, page],
  );

  const reloadPolicy = useCallback(async () => {
    try {
      setPolicyLoading(true);
      const res = await getActiveEnergyPolicy();
      if (res?.success) setActivePolicy(res.data);
      else setActivePolicy(null);
    } catch (e) {
      console.error("활성 정책 로딩 실패:", e);
      setActivePolicy(null);
    } finally {
      setPolicyLoading(false);
    }
  }, []);

  const openDeviceDetail = async (deviceId) => {
    try {
      setDetailLoading(true);

      const [detailRes, logRes, savingRes] = await Promise.allSettled([
        getEnergyDeviceDetail(deviceId),
        getEnergyDeviceControlLogs(deviceId),
        getEnergyDeviceSavingResults(deviceId, 0),
      ]);

      if (detailRes.status === "fulfilled" && detailRes.value?.success) {
        setDetail(detailRes.value.data);
      } else {
        setDetail(null);
      }

      if (logRes.status === "fulfilled" && logRes.value?.success) {
        setControlLogs(Array.isArray(logRes.value.data) ? logRes.value.data : []);
      } else {
        setControlLogs([]);
      }

      if (savingRes.status === "fulfilled" && savingRes.value?.success) {
        setSavingResults(savingRes.value.data ?? { content: [], totalPages: 0, number: 0 });
      } else {
        setSavingResults({ content: [], totalPages: 0, number: 0 });
      }

      setDetailOpen(true);
    } catch (e) {
      console.error("상세 조회 실패:", e);
    } finally {
      setDetailLoading(false);
    }
  };

  // actions
  const handleStartCheck = async (deviceId) => {
    try {
      await startEnergyDeviceCheck(deviceId);
    } catch (e) {
      console.error("점검 시작 실패:", e);
    } finally {
      await Promise.all([loadDashboard(), loadDevices(), loadCounts()]);
    }
  };

  const handleCompleteCheck = async (deviceId) => {
    try {
      await completeEnergyDeviceCheck(deviceId);
    } catch (e) {
      console.error("점검 완료 실패:", e);
    } finally {
      await Promise.all([loadDashboard(), loadDevices(), loadCounts()]);
    }
  };

  const handleControl = async (deviceId, operate, reason) => {
    try {
      // optimistic update: 버튼 누르자마자 UI 반영
      setDetail((prev) => (prev?.deviceId === deviceId ? { ...prev, isOperating: operate } : prev));

      const res = await controlEnergyDevice(deviceId, operate, reason || "관리자 수동 제어");
      if (!res?.success) throw new Error("controlEnergyDevice success=false");

      // 모달에서 필요한 데이터 3종을 한 번에 재조회해서 확정 반영
      const [detailRes, logRes, savingRes] = await Promise.allSettled([
        getEnergyDeviceDetail(deviceId),
        getEnergyDeviceControlLogs(deviceId),
        getEnergyDeviceSavingResults(deviceId, 0),
      ]);
      if (detailRes.status === "fulfilled" && detailRes.value?.success) {
        setDetail(detailRes.value.data);
      }

      if (logRes.status === "fulfilled" && logRes.value?.success) {
        setControlLogs(logRes.value.data ?? []);
      } else {
        setControlLogs([]);
      }

      if (savingRes.status === "fulfilled" && savingRes.value?.success) {
        setSavingResults(savingRes.value.data ?? { content: [], totalPages: 0, number: 0 });
      } else {
        setSavingResults({ content: [], totalPages: 0, number: 0 });
      }
    } catch (e) {
      console.error("설비 제어 실패:", e);
      setDetail((prev) =>
        prev?.deviceId === deviceId ? { ...prev, isOperating: !operate } : prev,
      );
      alert("제어에 실패했습니다. (서버/통신 확인)");
    } finally {
      await loadDevices();
      await loadDashboard();
    }
  };

  // filter/page
  const changeStatusFilter = async (nextStatus) => {
    setStatusFilter(nextStatus);
    setPage(0);
    await loadDevices(nextStatus, 0);
  };

  const changePage = async (nextPage) => {
    setPage(nextPage);
    await loadDevices(statusFilter, nextPage);
  };

  // init
  useEffect(() => {
    (async () => {
      await Promise.all([reloadPolicy(), loadDashboard(), loadDevices("ALL", 0)], loadCounts());
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return {
    // dashboard
    dashboard,
    dashboardLoading,

    // list
    devices,
    devicesLoading,
    statusFilter,
    page,
    totalPages,
    totalElements,
    changeStatusFilter,
    changePage,

    // detail
    detailOpen,
    setDetailOpen,
    detail,
    detailLoading,
    controlLogs,
    savingResults,
    openDeviceDetail,

    // policy
    activePolicy,
    policyLoading,
    reloadPolicy,

    // loaders (EnergyPage에서 onSaved에 쓰려고 노출)
    loadDashboard,
    loadDevices,

    // actions
    handleStartCheck,
    handleCompleteCheck,
    handleControl,

    counts,
    loadCounts,
  };
}
