import React from "react";
import {Search, Eye, Edit, Trash2} from "lucide-react";
import styles from "./ElevatorList.module.css";

export default function ElevatorList({
  elevators,
  searchCond,
  setSearchCond,
  onSearch,
  page,
  setPage,
  totalPages,
  onViewDetail,
  onEditState,
  onDelete,
}) {
  // 상태별 뱃지 스타일 매핑
  const getStateBadgeClass = (state) => {
    switch (state) {
      case "IDLE":
      case "MOVING":
      case "DOOR_OPEN":
        return styles.activeBadge; // 정상
      case "REPAIR":
        return styles.warningBadge; // 점검 중
      case "ERROR":
        return styles.dangerBadge; // 고장
      default:
        return styles.inactiveBadge;
    }
  };

  const getStateLabel = (state) => {
    switch (state) {
      case "IDLE":
      case "MOVING":
      case "DOOR_OPEN":
        return "정상";
      case "REPAIR":
        return "점검 중";
      case "ERROR":
        return "고장";
      default:
        return "알 수 없음";
    }
  };

  const handleChange = (e) => {
    const {name, value} = e.target;

    // 호기(hogi) 입력 시 숫자만 허용
    if (name === "hogi") {
      const onlyNumber = value.replace(/[^0-9]/g, "");
      setSearchCond((prev) => ({...prev, [name]: onlyNumber}));
      return;
    }

    setSearchCond((prev) => ({...prev, [name]: value === "all" ? "" : value}));
  };

  return (
    <div className={styles.mainCard}>
      <div className={styles.cardHeader}>
        <h2 className={styles.cardTitle}>엘리베이터 목록</h2>

        {/* 검색 필터 영역 */}
        <div className={styles.filterContainer}>
          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>동 선택</label>
            <select
              id="dong"
              name="dong"
              className={styles.filterSelect}
              value={searchCond.dong || "all"}
              onChange={handleChange}
            >
              <option value="all">전체 동</option>
              <option value="D101">101동</option>
              <option value="D102">102동</option>
              <option value="D103">103동</option>
              <option value="D104">104동</option>
            </select>
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>호기</label>
            <input
              id="hogi"
              name="hogi"
              type="text"
              placeholder="호기 번호 입력"
              value={searchCond.hogi || ""}
              onChange={handleChange}
              className={styles.filterInput}
            />
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>상태</label>
            <select
              id="state"
              name="state"
              className={styles.filterSelect}
              value={searchCond.state || "all"}
              onChange={handleChange}
            >
              <option value="all">전체 상태</option>
              <option value="NORMAL">정상</option>
              <option value="REPAIR">점검 중</option>
              <option value="ERROR">고장</option>
            </select>
          </div>

          <button onClick={onSearch} className={styles.searchButton}>
            <Search size={16} /> 검색
          </button>
        </div>
      </div>

      <div className={styles.tableContainer}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>동</th>
              <th>호기</th>
              <th>현재 상태</th>
              <th>상세 보기</th>
              <th>상태 수정</th>
              <th>삭제</th>
            </tr>
          </thead>
          <tbody>
            {elevators && elevators.length > 0 ? (
              elevators.map((ev) => (
                <tr key={ev.elevatorId}>
                  <td>{ev.dong.replace("D", "")}동</td>
                  <td>{ev.hogi}호기</td>
                  <td>
                    <span className={`${styles.badge} ${getStateBadgeClass(ev.state)}`}>
                      {getStateLabel(ev.state)}
                    </span>
                  </td>
                  <td>
                    <button className={styles.iconBtn} onClick={() => onViewDetail(ev.elevatorId)}>
                      <Eye size={18} />
                    </button>
                  </td>
                  <td>
                    <button
                      className={`${styles.iconBtn} ${styles.editBtn}`}
                      onClick={() => onEditState(ev)}
                    >
                      <Edit size={18} />
                    </button>
                  </td>
                  <td>
                    <button
                      className={`${styles.iconBtn} ${styles.deleteBtn}`}
                      onClick={() => onDelete(ev)}
                    >
                      <Trash2 size={18} />
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" className={styles.empty}>
                  등록된 엘리베이터가 없거나 검색 결과가 없습니다.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* 페이지네이션 */}
      <div className={styles.pagination}>
        <button className={styles.pageBtn} disabled={page === 0} onClick={() => setPage(page - 1)}>
          이전
        </button>
        <span className={styles.pageInfo}>
          {page + 1} / {totalPages || 1}
        </span>
        <button
          className={styles.pageBtn}
          disabled={page >= totalPages - 1}
          onClick={() => setPage(page + 1)}
        >
          다음
        </button>
      </div>
    </div>
  );
}
