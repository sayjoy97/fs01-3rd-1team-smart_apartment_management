import { useState } from "react";
import useEnergyPage from "./components/useEnergyPage";
import EnergyDetailModal from "./components/EnergyDetailModal";
import "./EnergyPage.css";

import { getEnergyDeviceDetail } from "../../api/energyAPI";
import { controlEnergyDevice } from "../../api/energyAPI";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";
import { Button } from "../../components/ui/button";
import { Badge } from "../../components/ui/badge";

import { AlertTriangle, CheckCircle, Clock, Power, Eye } from "lucide-react";

function EnergyPage() {
  const {
    dashboard,
    devices,
    loading,

    statusFilter,
    changeStatusFilter,

    handleStartCheck,
    handleCompleteCheck,

    currentPage,
    totalPages,
    changePage,
    loadDevices,
  } = useEnergyPage();

  const [detailOpen, setDetailOpen] = useState(false);
  const [selectedDetail, setSelectedDetail] = useState(null);

  /* ================================
   🔥 대시보드 안전 매핑
  ================================= */

  const monthlyUsage = dashboard?.monthlyUsageKwh ?? 0;
  const checkRequiredCount = dashboard?.checkRequiredCount ?? 0;
  const checkingCount = dashboard?.checkingCount ?? 0;
  const possibleSavingCost = dashboard?.possibleSavingCost ?? 0;

  /* ================================
     1️⃣ 상태 색상 매핑
  ================================= */

  const getStatusColor = (status) => {
    switch (status) {
      case "CHECK_REQUIRED":
        return "bg-red-100 text-red-700";
      case "CHECKING":
        return "bg-yellow-100 text-yellow-700";
      case "NORMAL":
        return "bg-green-100 text-green-700";
      default:
        return "bg-gray-100 text-gray-700";
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case "CHECK_REQUIRED":
        return <AlertTriangle className="size-4" />;
      case "CHECKING":
        return <Clock className="size-4" />;
      case "NORMAL":
        return <CheckCircle className="size-4" />;
      default:
        return null;
    }
  };

  const openDeviceDetail = async (id) => {
    try {
      const res = await getEnergyDeviceDetail(id);
      setSelectedDetail(res.data);
      setDetailOpen(true);
    } catch (e) {
      console.error("상세 조회 실패", e);
    }
  };
  const handleDeviceControl = async (deviceId, operate) => {
    try {
      await controlEnergyDevice(deviceId, operate, "관리자 수동 제어");

      // 🔥 상세 다시 불러오기
      const res = await getEnergyDeviceDetail(deviceId);
      setSelectedDetail(res.data);

      // 🔥 목록도 다시 불러오기
      loadDevices();
    } catch (error) {
      console.error("설비 제어 실패", error);
    }
  };

  /* ================================
     2️⃣ 렌더링
  ================================= */

  return (
    <div className="energy-page">
      {/* ================================
         상단 제목
      ================================= */}
      <div className="energy-header">
        <h1>에너지 관리</h1>
        <p>설비 분석 및 점검 관리 시스템</p>
      </div>

      {/* ================================
         대시보드 카드
      ================================= */}

      {dashboard && (
        <div className="dashboard-grid">
          <Card>
            <CardHeader>
              <CardTitle>이번 달 총 사용량</CardTitle>
            </CardHeader>
            <CardContent>
              <h2>{monthlyUsage.toLocaleString()} kWh</h2>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>점검 필요 설비 수</CardTitle>
            </CardHeader>
            <CardContent>
              <h2>{checkRequiredCount} 대</h2>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>점검 중 설비 수</CardTitle>
            </CardHeader>
            <CardContent>
              <h2>{checkingCount} 대</h2>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>예상 절감 가능 비용</CardTitle>
            </CardHeader>
            <CardContent>
              <h2>₩{possibleSavingCost.toLocaleString()}</h2>
            </CardContent>
          </Card>
        </div>
      )}

      {/* ================================
         상태 필터
      ================================= */}

      <div className="status-filter">
        <Button
          variant={statusFilter === null ? "default" : "outline"}
          onClick={() => changeStatusFilter(null)}
        >
          전체
        </Button>

        <Button
          variant={statusFilter === "CHECK_REQUIRED" ? "default" : "outline"}
          onClick={() => changeStatusFilter("CHECK_REQUIRED")}
        >
          점검 권장
        </Button>

        <Button
          variant={statusFilter === "CHECKING" ? "default" : "outline"}
          onClick={() => changeStatusFilter("CHECKING")}
        >
          점검 중
        </Button>

        <Button
          variant={statusFilter === "NORMAL" ? "default" : "outline"}
          onClick={() => changeStatusFilter("NORMAL")}
        >
          정상
        </Button>
      </div>

      {/* ================================
     설비 목록
================================= */}

      <div className="device-table">
        {loading ? (
          <p>로딩 중...</p>
        ) : devices.length === 0 ? (
          <p>데이터가 없습니다.</p>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>설비명</th>
                  <th>위치</th>
                  <th>운영</th>
                  <th>상태</th>
                  <th>낭비(kWh)</th>
                  <th>변화율</th>
                  <th>예상 절감액</th>
                  <th>작업</th>
                </tr>
              </thead>

              <tbody>
                {devices.map((device) => (
                  <tr key={device.deviceId}>
                    <td>{device.deviceName}</td>
                    <td>{device.location}</td>

                    <td>
                      <Badge>
                        <Power className="size-3 mr-1" />
                        {device.isOperating ? "ON" : "OFF"}
                      </Badge>
                    </td>

                    <td>
                      <Badge className={getStatusColor(device.deviceStatus)}>
                        {getStatusIcon(device.deviceStatus)}
                        {device.deviceStatus}
                      </Badge>
                    </td>

                    <td>{device.estimatedWasteKwh ?? 0}</td>
                    <td>{device.monthChangeRate ?? 0}%</td>
                    <td>₩{device.estimatedWasteCost?.toLocaleString() ?? 0}</td>

                    <td>
                      <Button size="sm" onClick={() => openDeviceDetail(device.deviceId)}>
                        <Eye className="size-3 mr-1" />
                        상세
                      </Button>

                      {device.deviceStatus === "CHECK_REQUIRED" && (
                        <Button size="sm" onClick={() => handleStartCheck(device.deviceId)}>
                          점검 시작
                        </Button>
                      )}

                      {device.deviceStatus === "CHECKING" && (
                        <Button size="sm" onClick={() => handleCompleteCheck(device.deviceId)}>
                          점검 완료
                        </Button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* 🔥 서버 기반 페이지네이션 */}
            {totalPages > 0 && (
              <div className="pagination">
                <Button disabled={currentPage === 0} onClick={() => changePage(currentPage - 1)}>
                  이전
                </Button>

                <span>
                  {currentPage + 1} / {totalPages}
                </span>

                <Button
                  disabled={currentPage + 1 >= totalPages}
                  onClick={() => changePage(currentPage + 1)}
                >
                  다음
                </Button>
              </div>
            )}
          </>
        )}
      </div>
      <EnergyDetailModal
        open={detailOpen}
        onClose={() => setDetailOpen(false)}
        detail={selectedDetail}
        onControl={handleDeviceControl}
      />
    </div>
  );
}

export default EnergyPage;
