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
      await Promise.all([loadDashboard(), loadDevices()]);
    }
  };

  const handleCompleteCheck = async (deviceId) => {
    try {
      await completeEnergyDeviceCheck(deviceId);
    } catch (e) {
      console.error("점검 완료 실패:", e);
    } finally {
      await Promise.all([loadDashboard(), loadDevices()]);
    }
  };

  const handleControl = async (deviceId, operate, reason) => {
    try {
      await controlEnergyDevice(deviceId, operate, reason || "관리자 수동 제어");

      // 상세창 열려있으면 상세 refresh
      if (detailOpen) {
        const res = await getEnergyDeviceDetail(deviceId);
        if (res?.success) setDetail(res.data);
      }
    } catch (e) {
      console.error("설비 제어 실패:", e);
    } finally {
      await loadDevices();
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
      await Promise.all([reloadPolicy(), loadDashboard(), loadDevices("ALL", 0)]);
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
  };
}
