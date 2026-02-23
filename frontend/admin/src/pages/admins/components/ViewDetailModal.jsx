import React, {useEffect} from "react";
import {X, User, Shield, Mail, Phone, Calendar} from "lucide-react";
import styles from "./ViewDetailModal.module.css";

const ViewDetailModal = ({open, onClose, admin}) => {
  // 모달 오픈 시 본문 스크롤 방지
  useEffect(() => {
    if (open) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [open]);
  if (!open || !admin) return null;

  const adminPhone = admin.adminPhone
    ? admin.adminPhone.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3")
    : "번호 없음";

  return (
    <div className={styles.backdrop} onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className={styles.modal}>
        {/* 헤더 */}
        <div className={styles.header}>
          <h2>관리자 상세 정보</h2>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={24} />
          </button>
        </div>

        {/* 컨텐츠 */}
        <div className={styles.content}>
          {/* 상단 프로필 요약 */}
          <div className={styles.profileSummary}>
            <div className={styles.avatar}>
              <User size={40} />
            </div>
            <div className={styles.summaryText}>
              <h3>{admin.adminName || "미등록"}</h3>
              <span className={styles.roleBadge}>{admin.adminRole}</span>
            </div>
            <div
              className={`${styles.statusBadge} ${admin.state ? styles.active : styles.inactive}`}
            >
              {admin.state ? "활성 계정" : "비활성 계정"}
            </div>
          </div>

          {/* 상세 정보 그리드 */}
          <div className={styles.infoGrid}>
            <div className={styles.infoItem}>
              <div className={styles.label}>
                <Shield size={16} /> 아이디
              </div>
              <div className={styles.value}>{admin.adminLoginId}</div>
            </div>

            <div className={styles.infoItem}>
              <div className={styles.label}>
                <Mail size={16} /> 이메일
              </div>
              <div className={styles.value}>{admin.adminEmail || "미등록"}</div>
            </div>

            <div className={styles.infoItem}>
              <div className={styles.label}>
                <Phone size={16} /> 연락처
              </div>
              <div className={styles.value}>{adminPhone || "미등록"}</div>
            </div>

            <div className={styles.infoItem}>
              <div className={styles.label}>
                <Calendar size={16} /> 생성일
              </div>
              <div className={styles.value}>{admin.createdAt.replace("T", " ") || "-"}</div>
            </div>
          </div>
        </div>

        {/* 푸터 */}
        <div className={styles.footer}>
          <button className={styles.confirmButton} onClick={onClose}>
            확인
          </button>
        </div>
      </div>
    </div>
  );
};

export default ViewDetailModal;
