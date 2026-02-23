import React, {useEffect} from "react";
import {X, AlertTriangle, Trash2, Lock} from "lucide-react";
import styles from "./DeleteElevatorModal.module.css";

const DeleteElevatorModal = ({
  open,
  close,
  onDelete,
  elevator,
  deleteElevatorForm,
  setDeleteElevatorForm,
}) => {
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

  const handleChange = (e) => {
    setDeleteElevatorForm({
      ...deleteElevatorForm,
      adminPass: e.target.value,
    });
  };

  const handleConfirm = () => {
    if (!deleteElevatorForm.adminPass.trim()) {
      alert("관리자 비밀번호를 입력해주세요.");
      return;
    }
    onDelete();
  };

  return (
    <div className={styles.backdrop} onClick={(e) => e.target === e.currentTarget && close()}>
      <div className={styles.modal}>
        <header className={styles.header}>
          <div className={styles.titleGroup}>
            <div className={styles.warningIconCircle}>
              <AlertTriangle className={styles.warningIcon} size={20} />
            </div>
            <div>
              <h2>엘리베이터 삭제</h2>
              <p className={styles.subtitle}>
                ID: {elevator.elevatorId}번 자산을 시스템에서 삭제합니다.
              </p>
            </div>
          </div>
          <button className={styles.closeButton} onClick={close}>
            <X size={24} />
          </button>
        </header>

        <div className={styles.content}>
          <div className={styles.dangerAlert}>
            <p>
              <strong>주의:</strong> 삭제된 엘리베이터 데이터와 관련 로그 기록은 복구할 수 없습니다.
              정말로 삭제하시겠습니까?
            </p>
          </div>

          <div className={styles.inputGroup}>
            <label htmlFor="adminPass">
              <Lock size={14} /> 관리자 비밀번호 확인
            </label>
            <input
              id="adminPass"
              type="password"
              placeholder="비밀번호를 입력하세요"
              value={deleteElevatorForm.adminPass}
              onChange={handleChange}
              className={styles.input}
              onKeyDown={(e) => e.key === "Enter" && handleConfirm()}
              autoFocus
            />
          </div>
        </div>

        <footer className={styles.footer}>
          <button className={styles.cancelButton} onClick={close}>
            취소
          </button>
          <button
            className={styles.deleteButton}
            onClick={handleConfirm}
            disabled={!deleteElevatorForm.adminPass}
          >
            <Trash2 size={16} />
            영구 삭제
          </button>
        </footer>
      </div>
    </div>
  );
};

export default DeleteElevatorModal;
