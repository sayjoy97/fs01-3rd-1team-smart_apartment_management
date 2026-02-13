import { useState, useEffect } from "react";
import {
  getEnergyDashboard,
  getEnergyDeviceList,
  getEnergyDeviceDetail,
  getEnergyDeviceControlLogs,
  startEnergyDeviceCheck,
  completeEnergyDeviceCheck,
  controlEnergyDevice,
} from "../../../api/energyAPI";

/*
  🔥 이 훅은 EnergyPage 전용 상태/로직을 전부 관리한다.
  UI와 완전히 분리된 상태 관리 레이어다.
*/

export default function useEnergyPage() {
  /* ================================
     1️⃣ 기본 상태
  ================================= */

  const [dashboard, setDashboard] = useState(null);
  const [devices, setDevices] = useState([]);
  const [loading, setLoading] = useState(false);

  const [statusFilter, setStatusFilter] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);

  const [selectedDevice, setSelectedDevice] = useState(null);
  const [controlLogs, setControlLogs] = useState([]);

  const [detailOpen, setDetailOpen] = useState(false);

  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  /* ================================
     2️⃣ 대시보드 로딩
  ================================= */

  const loadDashboard = async () => {
    try {
      const res = await getEnergyDashboard();

      // ApiResponse.success(data)
      if (res.success) {
        setDashboard(res.data);
      }
    } catch (e) {
      console.error("대시보드 로딩 실패:", e);
    }
  };

  /* ================================
     3️⃣ 설비 목록 로딩
  ================================= */

  const loadDevices = async (status = statusFilter, page = currentPage) => {
    try {
      setLoading(true);

      const res = await getEnergyDeviceList(status, page);

      if (res.success) {
        const pageData = res.data;

        setDevices(pageData.content);
        setTotalPages(pageData.totalPages);
        setTotalElements(pageData.totalElements);
        setCurrentPage(pageData.number); // 서버 기준 page
      }
    } catch (e) {
      console.error("설비 목록 로딩 실패:", e);
    } finally {
      setLoading(false);
    }
  };

  /* ================================
     4️⃣ 설비 상세 조회
  ================================= */

  const openDeviceDetail = async (deviceId) => {
    try {
      const detailRes = await getEnergyDeviceDetail(deviceId);
      const logRes = await getEnergyDeviceControlLogs(deviceId);

      if (detailRes.success) {
        setSelectedDevice(detailRes.data);
      }

      if (logRes.success) {
        setControlLogs(logRes.data);
      }

      setDetailOpen(true);
    } catch (e) {
      console.error("상세 조회 실패:", e);
    }
  };

  /* ================================
     5️⃣ 상태 변경
  ================================= */

  const handleStartCheck = async (deviceId) => {
    await startEnergyDeviceCheck(deviceId);
    await loadDevices();
  };

  const handleCompleteCheck = async (deviceId) => {
    await completeEnergyDeviceCheck(deviceId);
    await loadDevices();
  };

  /* ================================
     6️⃣ ON/OFF 제어
  ================================= */

  const handleControl = async (deviceId, operate) => {
    await controlEnergyDevice(deviceId, operate, "관리자 수동 제어");
    await loadDevices();
    setDetailOpen(false);
  };

  /* ================================
     7️⃣ 필터 변경
  ================================= */

  const changeStatusFilter = async (status) => {
    setStatusFilter(status);
    setCurrentPage(0);
    await loadDevices(status, 0);
  };

  const changePage = async (page) => {
    setCurrentPage(page);
    await loadDevices(statusFilter, page);
  };

  /* ================================
     8️⃣ 최초 로딩
  ================================= */

  useEffect(() => {
    const init = async () => {
      await loadDashboard();
      await loadDevices(null, 0);
    };

    init();
  }, []);

  /* ================================
     9️⃣ 반환
  ================================= */

  return {
    dashboard,
    devices,
    loading,

    statusFilter,
    currentPage,
    totalPages,
    totalElements,

    selectedDevice,
    controlLogs,
    detailOpen,

    setDetailOpen,

    // openDeviceDetail,
    handleStartCheck,
    handleCompleteCheck,
    handleControl,

    changeStatusFilter,
    changePage,
    loadDevices,
  };
}
