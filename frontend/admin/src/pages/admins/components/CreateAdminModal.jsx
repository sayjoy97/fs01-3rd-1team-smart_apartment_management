import React, {useEffect} from "react";
import styles from "./CreateAdminModal.module.css";
import {X, CheckCircle2, AlertCircle} from "lucide-react";

const CreateAdminModal = ({
  open,
  onClose,
  formData,
  setFormData,
  onSubmit,
  idCheckStatus,
  onCheckDuplicateId,
  passwordCheckStatus,
  onCheckvalidatePassword,
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
    const {name, value} = e.target;
    setFormData((prev) => ({...prev, [name]: value}));
  };

  return (
    <div className={styles.backdrop} onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className={styles.modal}>
        <div className={styles.header}>
          <h2>새 관리자 계정 생성</h2>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={24} />
          </button>
        </div>

        <div className={styles.content}>
          <div className={styles.formGroup}>
            <label>관리자 아이디</label>
            <div className={styles.inputWithBtn}>
              <input
                type="text"
                name="adminLoginId"
                placeholder="아이디를 입력하세요"
                value={formData.adminLoginId}
                onChange={handleChange}
                className={idCheckStatus === "available" ? styles.successInput : ""}
              />
              <button onClick={onCheckDuplicateId} className={styles.actionBtn}>
                중복 확인
              </button>
            </div>
            {idCheckStatus === "available" && (
              <p className={styles.successText}>
                <CheckCircle2 size={14} /> 사용 가능한 아이디입니다.
              </p>
            )}
            {idCheckStatus === "duplicate" && (
              <p className={styles.errorText}>
                <AlertCircle size={14} /> 이미 사용 중인 아이디입니다.
              </p>
            )}
          </div>

          <div className={styles.formGroup}>
            <label>비밀번호</label>
            <input
              type="password"
              name="adminPass"
              placeholder="최소 8자 이상 입력"
              value={formData.adminPass}
              onChange={handleChange}
              onBlur={onCheckvalidatePassword}
            />
          </div>

          <div className={styles.formGroup}>
            <label>비밀번호 확인</label>
            <input
              type="password"
              name="confirmPass"
              placeholder="비밀번호를 다시 입력하세요"
              value={formData.confirmPass}
              onChange={handleChange}
              onBlur={onCheckvalidatePassword}
              className={
                passwordCheckStatus === "valid"
                  ? styles.successInput
                  : passwordCheckStatus === "invalid"
                    ? styles.errorInput
                    : ""
              }
            />
          </div>

          <div className={styles.formGroup}>
            <label>관리자 권한 부여</label>
            <select name="adminRole" value={formData.adminRole} onChange={handleChange}>
              <option value="">권한을 선택하세요</option>
              <option value="ACTING_ADMIN">ACTING_ADMIN (권한 대행 관리자)</option>
              <option value="ADMIN">ADMIN (일반 관리자)</option>
            </select>
          </div>
        </div>

        <div className={styles.footer}>
          <button className={styles.cancelButton} onClick={onClose}>
            취소
          </button>
          <button className={styles.submitButton} onClick={onSubmit}>
            생성하기
          </button>
        </div>
      </div>
    </div>
  );
};

export default CreateAdminModal;
