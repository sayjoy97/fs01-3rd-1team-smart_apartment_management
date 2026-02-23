import React, {useEffect} from "react";
import {X, Trash2, AlertTriangle, Lock} from "lucide-react";
import styles from "./DeleteAdminModal.module.css";

const DeleteAdminModal = ({open, onClose, admin, deleteForm, setDeleteForm, onSubmit}) => {
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

  const handleChange = (e) => {
    const {name, value} = e.target;
    setDeleteForm((prev) => ({...prev, [name]: value}));
  };

  return (
    <div className={styles.backdrop} onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className={styles.modal}>
        {/* 헤더 */}
        <div className={styles.header}>
          <div className={styles.titleWrapper}>
            <AlertTriangle className={styles.warningIcon} size={20} />
            <h2>관리자 계정 삭제</h2>
          </div>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={24} />
          </button>
        </div>

        {/* 컨텐츠 */}
        <div className={styles.content}>
          <div className={styles.warningBox}>
            <p className={styles.warningTitle}>정말로 삭제하시겠습니까?</p>
            <p className={styles.warningText}>
              삭제된 관리자 계정은 복구할 수 없으며, 해당 계정의 모든 접근 권한이 즉시 소멸됩니다.
            </p>
          </div>

          <div className={styles.targetInfo}>
            <p className={styles.label}>삭제 대상</p>
            <div className={styles.adminCard}>
              <span className={styles.adminName}>{admin.adminName || "이름 없음"}</span>
              <span className={styles.adminId}>{admin.adminLoginId}</span>
              <span className={styles.adminRole}>{admin.adminRole}</span>
            </div>
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="adminPass">
              <Lock size={16} /> 본인 비밀번호 확인
            </label>
            <input
              id="adminPass"
              type="password"
              name="adminPass"
              placeholder="본인의 비밀번호를 입력하세요"
              value={deleteForm.adminPass}
              onChange={handleChange}
              className={styles.input}
              autoFocus
            />
          </div>
        </div>

        {/* 푸터 */}
        <div className={styles.footer}>
          <button className={styles.cancelButton} onClick={onClose}>
            취소
          </button>
          <button className={styles.deleteButton} onClick={onSubmit}>
            <Trash2 size={16} /> 계정 삭제
          </button>
        </div>
      </div>
    </div>
  );
};

export default DeleteAdminModal;
