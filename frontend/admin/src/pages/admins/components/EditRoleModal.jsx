import React, {useEffect} from "react";
import {X, ShieldAlert, Lock, UserCog} from "lucide-react";
import styles from "./EditRoleModal.module.css";

const EditRoleModal = ({open, onClose, admin, editRole, setEditRole, onSubmit}) => {
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
    setEditRole((prev) => ({...prev, [name]: value}));
  };

  return (
    <div className={styles.backdrop} onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className={styles.modal}>
        <div className={styles.header}>
          <div className={styles.titleWrapper}>
            <ShieldAlert className={styles.alertIcon} size={20} />
            <h2>관리자 권한 수정</h2>
          </div>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={24} />
          </button>
        </div>

        <div className={styles.content}>
          <div className={styles.targetInfo}>
            <p className={styles.infoLabel}>변경 대상 관리자</p>
            <div className={styles.infoBox}>
              <span className={styles.targetName}>{admin.adminName}</span>
              <span className={styles.targetId}>({admin.adminLoginId})</span>
            </div>
          </div>

          <div className={styles.formGroup}>
            <label>
              <UserCog size={16} /> 변경할 권한
            </label>
            <select
              name="adminRole"
              value={editRole.adminRole}
              onChange={handleChange}
              className={styles.select}
            >
              <option value="">권한을 선택하세요</option>
              <option value="ACTING_ADMIN">ACTING_ADMIN</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>

          <div className={styles.formGroup}>
            <label>
              <Lock size={16} /> 본인 비밀번호 확인
            </label>
            <input
              type="password"
              name="adminPass"
              placeholder="본인의 비밀번호를 입력하세요"
              value={editRole.adminPass}
              onChange={handleChange}
              className={styles.input}
            />
            <p className={styles.helperText}>
              중요 설정 변경을 위해 본인의 비밀번호 확인이 필요합니다.
            </p>
          </div>
        </div>

        <div className={styles.footer}>
          <button className={styles.cancelButton} onClick={onClose}>
            취소
          </button>
          <button className={styles.submitButton} onClick={onSubmit}>
            권한 변경 적용
          </button>
        </div>
      </div>
    </div>
  );
};

export default EditRoleModal;
