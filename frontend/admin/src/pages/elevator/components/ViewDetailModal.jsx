import React, {useCallback, useEffect, useState} from "react";
import {X, Activity, Clock, MapPin, Navigation, Info, Filter} from "lucide-react";
import styles from "./ViewDetailModal.module.css";
import {getElevatorDetail} from "../../../api/elevator/elevatorAPI";
import {toast} from "sonner";

const ViewDetailModal = ({open, close, elevatorId}) => {
  const [loading, setLoading] = useState(false);

  // 엘리베이터 상세 정보
  const [detailElevator, setDetailElevator] = useState({
    elevatorRes: {
      elevatorId: "",
      dong: "",
      hogi: "",
      callMethod: "",
      currentFloor: "",
      direction: "",
      state: "",
      doorStatus: "",
      updatedAt: "",
    },
    elevatorEventLogs: [
      {
        logId: "",
        elevatorId: "",
        eventType: "",
        floor: "",
        message: "",
        createdAt: "",
      },
    ],
  });

  const row = 5;

  const [filters, setFilters] = useState({
    eventType: "",
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // 엘리베이터 상세 정보 조회 함수
  const fetchLogs = useCallback(async () => {
    setLoading(true);
    try {
      const searchParams = {
        eventType: filters.eventType === "" ? null : filters.eventType,
      };
      const pageable = {
        page: page,
        size: row,
        sort: ["createdAt,desc"],
      };
      getElevatorDetail(elevatorId, searchParams, pageable)
        .then((res) => {
          console.log(res);
          const {elevatorRes, elevatorEventLogs} = res.data;
          setDetailElevator({
            elevatorRes: elevatorRes,
            elevatorEventLogs: elevatorEventLogs.content || elevatorEventLogs,
          });
          setTotalPages(res.data.elevatorEventLogs.totalPages);
        })
        .catch((err) => {
          throw err;
        });
    } catch (err) {
      console.error("엘리베이터 상세 정보 조회 실패:", err);
      const {code, message} = err.response.data.error;
      toast.error(message || "엘리베이터 상세 정보 조회에 실패했습니다");
    } finally {
      setLoading(false);
    }
  }, [page, elevatorId, filters]);

  // 모달 오픈 시 본문 스크롤 방지
  useEffect(() => {
    if (open) {
      fetchLogs();
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [open, fetchLogs]);

  // 상태별 텍스트 및 스타일 매핑
  const getStatusInfo = (state) => {
    switch (state) {
      case "IDLE":
        return {label: "정지", class: styles.statusNormal};
      case "MOVING":
        return {label: "이동 중", class: styles.statusNormal};
      case "DOOR_OPEN":
        return {label: "문 열림", class: styles.statusNormal};
      case "REPAIR":
        return {label: "점검 중", class: styles.statusRepair};
      case "ERROR":
        return {label: "고장", class: styles.statusError};
      default:
        return {label: "상태 불명", class: styles.statusUnknown};
    }
  };

  const getEventTypeInfo = (type) => {
    const info = {
      IDLE: "정지",
      MOVE_START: "이동 중",
      ARRIVE: "도착",
      DOOR_CLOSE: "문 열림",
      ERROR: "고장",
      REPAIR: "점검 중",
    };
    return info[type] || "상태 불명";
  };

  const clear = () => {
    setPage(0);
    setFilters({eventType: ""});
  };

  if (!open) return null;

  const status = getStatusInfo(detailElevator.elevatorRes.state);

  const logs = detailElevator.elevatorEventLogs || [];
  const emptyRowsCount = Math.max(0, row - logs.length);

  return (
    <div
      className={styles.backdrop}
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          close();
          clear();
        }
      }}
    >
      <div className={styles.modal}>
        <header className={styles.header}>
          <div className={styles.titleGroup}>
            <div className={styles.headerMain}>
              <Activity className={styles.headerIcon} size={20} />
              <h2>
                {detailElevator.elevatorRes.dong?.replace("D", "")}동{" "}
                {detailElevator.elevatorRes.hogi}호기
              </h2>
              <div className={`${styles.statusBadge} ${status.class}`}>{status.label}</div>
            </div>
            <p className={styles.updateTime}>
              최종 업데이트: {detailElevator.elevatorRes.updatedAt}
            </p>
          </div>
          <button
            className={styles.closeButton}
            onClick={() => {
              close();
              clear();
            }}
          >
            <X size={24} />
          </button>
        </header>

        <div className={styles.mainContent}>
          {/* 1. 실시간 상태 섹션 (슬림한 가로 배치) */}
          <section className={styles.slimStatusSection}>
            <div className={styles.statusItem}>
              <MapPin size={16} />
              <span className={styles.label}>위치</span>
              <span className={styles.value}>{detailElevator.elevatorRes.currentFloor}층</span>
            </div>
            <div className={styles.statusItem}>
              <Navigation size={16} />
              <span className={styles.label}>방향</span>
              <span className={styles.value}>
                {detailElevator.elevatorRes.direction === "UP"
                  ? "상승 ↑"
                  : detailElevator.elevatorRes.direction === "DOWN"
                    ? "하강 ↓"
                    : "정지 -"}
              </span>
            </div>
            <div className={styles.statusItem}>
              <Info size={16} />
              <span className={styles.label}>문 상태</span>
              <span className={styles.value}>
                {detailElevator.elevatorRes.doorStatus === "OPEN" ? "열림" : "닫힘"}
              </span>
            </div>
            <div className={styles.statusItem}>
              <Clock size={16} />
              <span className={styles.label}>방식</span>
              <span className={styles.value}>
                {detailElevator.elevatorRes.callMethod || "자동"}
              </span>
            </div>
          </section>

          {/* 2. 로그 섹션 (헤더와 페이지네이션 고정, 테이블만 스크롤) */}
          <section className={styles.logContainer}>
            <div className={styles.logHeader}>
              <h3>
                <Clock size={18} /> 이벤트 로그
              </h3>
              <div className={styles.filterSection}>
                <Filter size={14} />
                <select
                  value={filters.eventType}
                  onChange={(e) => {
                    setFilters({...filters, eventType: e.target.value});
                    setPage(0);
                  }}
                >
                  <option value="">모든 이벤트</option>
                  <option value="IDLE">정지</option>
                  <option value="MOVE_START">이동 중</option>
                  <option value="ARRIVE">도착</option>
                  <option value="DOOR_OPEN">문 열림</option>
                  <option value="DOOR_CLOSE">문 닫힘</option>
                  <option value="ERROR">고장</option>
                  <option value="REPAIR">점검 중</option>
                </select>
              </div>
            </div>

            <div className={styles.fixedTableWrapper}>
              <table className={styles.table}>
                <thead>
                  <tr>
                    <th>일시</th>
                    <th>유형</th>
                    <th>층수</th>
                    <th>메시지</th>
                  </tr>
                </thead>
                <tbody>
                  {logs.map((log) => (
                    <tr key={log.logId}>
                      <td>{log.createdAt.replace("T", " ")}</td>
                      <td>
                        <span className={`${styles.typeTag} ${styles[log.eventType]}`}>
                          {getEventTypeInfo(log.eventType)}
                        </span>
                      </td>
                      <td>{log.floor === null ? "-" : log.floor + "층"}</td>
                      <td className={styles.messageCell}>{log.message}</td>
                    </tr>
                  ))}
                  {/* 빈 행 채우기 (5개 높이 유지) */}
                  {Array.from({length: emptyRowsCount}).map((_, i) => (
                    <tr key={`empty-${i}`} className={styles.emptyRow}>
                      <td colSpan="4">&nbsp;</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className={styles.pagination}>
              <button disabled={page === 0} onClick={() => setPage(page - 1)}>
                이전
              </button>
              <span>
                {page + 1} / {totalPages}
              </span>
              <button disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>
                다음
              </button>
            </div>
          </section>
        </div>

        <footer className={styles.footer}>
          <button
            className={styles.confirmButton}
            onClick={() => {
              close();
              clear();
            }}
          >
            닫기
          </button>
        </footer>
      </div>
    </div>
  );
};

export default ViewDetailModal;
