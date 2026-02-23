import {useCallback, useEffect, useState} from "react";
import styles from "./LogHistoryModal.module.css";
import {logHistoryList} from "../../../api/admin/adminAPI";

const LogHistoryModal = ({open, onClose}) => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(false);

  const [filters, setFilters] = useState({
    accessType: "",
    success: "",
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const row = 10;

  const clear = () => {
    setPage(0);
    setFilters({
      accessType: "",
      success: "",
    });
  };

  const fetchLogs = useCallback(async () => {
    setLoading(true);
    try {
      const adminId = localStorage.getItem("adminId");

      const searchParams = {
        accessType: filters.accessType || null,
        success: filters.success === "" ? null : filters.success === "true",
      };
      const pageable = {
        page: page,
        size: row,
        sort: "createdAt,desc",
      };

      logHistoryList(adminId, searchParams, pageable)
        .then((res) => {
          setLogs(res.data.content);
          setTotalPages(res.data.totalPages);
        })
        .catch((err) => {
          throw err;
        });
    } catch (err) {
      if (err.response) {
        const {code, message} = err.response.data.error || {};

        if (code === "ADMIN_NOT_FOUND") {
          alert(message);
        } else {
          alert(message || "로그 조회 실패");
        }
      } else {
        alert("서버 연결 실패 또는 CORS 오류");
      }
    } finally {
      setLoading(false);
    }
  }, [page, filters]);

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

  const accessTypeInfo = (type) => {
    const info = {
      LOGIN: "로그인",
      LOGOUT: "로그아웃",
      INITIAL_SETUP: "초기 설정",
    };
    return info[type] || type;
  };

  if (!open) return null;

  return (
    <div
      className={styles.backdrop}
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          onClose();
          clear();
        }
      }}
    >
      <div className={styles.modal}>
        <div className={styles.header}>
          <h2>접속 로그 기록</h2>
          <button
            className={styles.closeButton}
            onClick={() => {
              (onClose(), clear());
            }}
          >
            &times;
          </button>
        </div>

        {/* 필터 섹션 */}
        <div className={styles.filterSection}>
          <select
            value={filters.accessType}
            onChange={(e) => {
              setFilters({...filters, accessType: e.target.value});
              setPage(0);
            }}
          >
            <option value="">모든 유형</option>
            <option value="LOGIN">로그인</option>
            <option value="LOGOUT">로그아웃</option>
            <option value="INITIAL_SETUP">초기 설정</option>
          </select>

          <select
            value={filters.success}
            onChange={(e) => {
              setFilters({...filters, success: e.target.value});
              setPage(0);
            }}
          >
            <option value="">모든 상태</option>
            <option value="true">성공</option>
            <option value="false">실패</option>
          </select>
        </div>

        <div className={styles.content}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>일시</th>
                <th>유형</th>
                <th>상태</th>
                <th>메시지</th>
                <th>IP</th>
              </tr>
            </thead>
            <tbody>
              {logs.length > 0 ? (
                <>
                  {/* 실제 로그 데이터 출력 */}
                  {logs.map((log) => (
                    <tr key={log.historyId}>
                      <td>{log.createdAt.replace("T", " ")}</td>
                      <td>
                        <span className={styles.typeTag}>{accessTypeInfo(log.accessType)}</span>
                      </td>
                      <td>
                        <span
                          className={`${styles.status} ${log.success ? styles.success : styles.fail}`}
                        >
                          {log.success ? "성공" : "실패"}
                        </span>
                      </td>
                      <td>{log.message}</td>
                      <td className={styles.ipText}>{log.ipAddress}</td>
                    </tr>
                  ))}

                  {/* ✅ 빈 행 채우기: 데이터가 10개 미만일 때 부족한 만큼 빈 tr 생성 */}
                  {logs.length < row &&
                    Array.from({length: row - logs.length}).map((_, index) => (
                      <tr key={`empty-${index}`} className={styles.emptyRow}>
                        <td colSpan="5">&nbsp;</td>
                      </tr>
                    ))}
                </>
              ) : (
                /* ✅ 데이터가 아예 없을 때도 10행 유지 */
                Array.from({length: row}).map((_, index) => (
                  <tr key={`all-empty-${index}`} className={styles.emptyRow}>
                    <td colSpan="5">{index === 4 ? "기록이 없습니다." : <>&nbsp;</>}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* 페이지네이션 */}
        <div className={styles.pagination}>
          <button disabled={page === 0} onClick={() => setPage(page - 1)}>
            이전
          </button>
          <span>
            {page + 1} / {totalPages || 1}
          </span>
          <button disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>
            다음
          </button>
        </div>

        <div className={styles.footer}>
          <button
            className={styles.confirmButton}
            onClick={() => {
              (onClose(), clear());
            }}
          >
            닫기
          </button>
        </div>
      </div>
    </div>
  );
};

export default LogHistoryModal;
