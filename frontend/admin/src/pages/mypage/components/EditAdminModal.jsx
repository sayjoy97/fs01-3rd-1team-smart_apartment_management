import {useEffect} from "react";
import styles from "./EditAdminModal.module.css";

export default function EditAdminModal({
  open,
  onClose,
  updateForm,
  setUpdateForm,
  errorState,
  onSave,
}) {
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
  if (!open) return null;

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div className={styles.backdrop} onClick={handleBackdropClick}>
      <div className={styles.modal}>
        {/* 헤더 부분 */}
        <div className={styles.header}>
          <h2>정보 수정</h2>
          <button className={styles.closeButton} onClick={onClose}>
            &times;
          </button>
        </div>

        {/* 컨텐츠 부분 */}
        <div className={styles.content}>
          <div className={styles.inputGroup}>
            <label>이름</label>
            <input
              value={updateForm.adminName}
              onChange={(e) => setUpdateForm({...updateForm, adminName: e.target.value})}
            />
          </div>

          <div className={styles.inputGroup}>
            <label>전화번호</label>
            <input
              value={updateForm.adminPhone}
              onChange={(e) => setUpdateForm({...updateForm, adminPhone: e.target.value})}
            />
          </div>

          <div className={styles.inputGroup}>
            <label>이메일</label>
            <input
              type="email"
              value={updateForm.adminEmail}
              onChange={(e) => setUpdateForm({...updateForm, adminEmail: e.target.value})}
            />
          </div>

          <div className={styles.divider} />

          <div className={styles.inputGroup}>
            <label>현재 비밀번호</label>
            <input
              type="password"
              value={updateForm.currentPassword}
              onChange={(e) => setUpdateForm({...updateForm, currentPassword: e.target.value})}
              className={errorState.currentPassword ? styles.errorInput : ""}
            />
            {errorState.currentPassword && (
              <p className={styles.errorText}>{errorState.currentPassword}</p>
            )}
          </div>

          <div className={styles.inputGroup}>
            <label>새 비밀번호</label>
            <input
              type="password"
              value={updateForm.newPassword}
              onChange={(e) => setUpdateForm({...updateForm, newPassword: e.target.value})}
              className={errorState.newPassword ? styles.errorInput : ""}
            />
            {errorState.newPassword && <p className={styles.errorText}>{errorState.newPassword}</p>}
          </div>

          <div className={styles.inputGroup}>
            <label>새 비밀번호 확인</label>
            <input
              type="password"
              value={updateForm.confirmNewPassword}
              onChange={(e) => setUpdateForm({...updateForm, confirmNewPassword: e.target.value})}
              className={errorState.confirmPassword ? styles.errorInput : ""}
            />
            {errorState.confirmPassword && (
              <p className={styles.errorText}>{errorState.confirmPassword}</p>
            )}
          </div>
        </div>

        {/* 푸터 부분 */}
        <div className={styles.footer}>
          <button className={styles.cancelButton} onClick={onClose}>
            취소
          </button>
          <button className={styles.confirmButton} onClick={onSave}>
            저장
          </button>
        </div>
      </div>
    </div>
  );
}
