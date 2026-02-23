import React, {useState} from "react";
import {Search, Eye, Edit, Trash2} from "lucide-react";
import styles from "./AdminList.module.css";
import {toast} from "sonner";

export default function AdminList({
  admins,
  searchCond,
  setSearchCond,
  onSearch,
  page,
  setPage,
  totalPages,
  onViewDetail,
  onEditRole,
  onDelete,
}) {
  return (
    <div className={styles.mainCard}>
      <div className={styles.cardHeader}>
        <h2 className={styles.cardTitle}>관리자 목록</h2>

        {/* 다중 검색 필터 영역 */}
        <div className={styles.filterContainer}>
          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>아이디</label>
            <input
              type="text"
              placeholder="아이디 검색"
              value={searchCond.adminLoginId}
              onChange={(e) => setSearchCond({...searchCond, adminLoginId: e.target.value})}
              className={styles.filterInput}
            />
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>이름</label>
            <input
              type="text"
              placeholder="이름 검색"
              value={searchCond.adminName}
              onChange={(e) => setSearchCond({...searchCond, adminName: e.target.value})}
              className={styles.filterInput}
            />
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>권한</label>
            <select
              className={styles.filterSelect}
              value={searchCond.adminRole || "all"}
              onChange={(e) =>
                setSearchCond({
                  ...searchCond,
                  adminRole: e.target.value === "all" ? "" : e.target.value,
                })
              }
            >
              <option value="all">전체</option>
              <option value="SUPER_ADMIN">SUPER_ADMIN</option>
              <option value="ACTING_ADMIN">ACTING_ADMIN</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>상태</label>
            <select
              className={styles.filterSelect}
              style={{width: "120px"}}
              value={searchCond.state === "" ? "all" : String(searchCond.state)}
              onChange={(e) =>
                setSearchCond({
                  ...searchCond,
                  state: e.target.value === "all" ? "" : e.target.value,
                })
              }
            >
              <option value="all">전체</option>
              <option value="true">활성</option>
              <option value="false">비활성</option>
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
              <th>아이디</th>
              <th>이름</th>
              <th>권한</th>
              <th>상태</th>
              <th>상세 보기</th>
              <th>권한 수정</th>
              <th>삭제</th>
            </tr>
          </thead>
          <tbody>
            {admins.length > 0 ? (
              admins.map((admin) => (
                <tr key={admin.adminId}>
                  <td>{admin.adminLoginId}</td>
                  <td>{admin.adminName || "이름 없음"}</td>
                  <td>
                    <span className={`${styles.badge} ${styles.roleBadge}`}>{admin.adminRole}</span>
                  </td>
                  <td>
                    <span
                      className={`${styles.badge} ${admin.state ? styles.activeBadge : styles.inactiveBadge}`}
                    >
                      {admin.state ? "활성" : "비활성"}
                    </span>
                  </td>
                  <td>
                    <button className={styles.iconBtn} onClick={() => onViewDetail(admin)}>
                      <Eye size={18} />
                    </button>
                  </td>
                  <td>
                    <button
                      className={`${styles.iconBtn} ${styles.editBtn}`}
                      onClick={() => {
                        if (admin.adminRole === "SUPER_ADMIN") {
                          toast.error(
                            <div>
                              총 관리자(SUPER_ADMIN)의 권한은
                              <br />
                              변경할 수 없습니다.
                            </div>,
                          );
                        } else {
                          onEditRole(admin);
                        }
                      }}
                    >
                      <Edit size={18} />
                    </button>
                  </td>
                  <td>
                    <button
                      className={`${styles.iconBtn} ${styles.deleteBtn}`}
                      onClick={() => {
                        if (admin.adminRole === "SUPER_ADMIN") {
                          toast.error("총 관리자(SUPER_ADMIN)를 삭제할 수 없습니다.");
                        } else {
                          onDelete(admin);
                        }
                      }}
                    >
                      <Trash2 size={18} />
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" className={styles.empty}>
                  등록된 관리자가 없거나 검색 결과가 없습니다.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

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
