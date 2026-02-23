import React from "react";
import {X, Mail, User, Lock, CheckCircle2, ArrowRight, ShieldCheck} from "lucide-react";
import styles from "./FindPasswordModal.module.css";

const FindPasswordModal = ({
  open,
  onClose,
  onFindPass,
  onChangePass,
  formData,
  setFormData,
  onCheckEmail,
  setOnCheckEmail,
  changePassForm,
  setPassForm,
}) => {
  if (!open) return null;

  // 이메일 유효성 검사
  const isEmailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.adminEmail);

  // 비밀번호 유효성 검사 (8자 이상 & 일치 여부)
  const isPasswordValid = changePassForm?.newPassword.length >= 8;
  const isPasswordMatch = changePassForm?.newPassword === changePassForm?.confirmNewPassword;

  const handleInputChange = (e) => {
    const {name, value} = e.target;
    setFormData((prev) => ({...prev, [name]: value}));
  };

  const handlePassChange = (e) => {
    const {name, value} = e.target;
    setPassForm((prev) => ({...prev, [name]: value}));
  };

  return (
    <div className={styles.backdrop} onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className={styles.modal}>
        <header className={styles.header}>
          <div className={styles.titleWrapper}>
            <ShieldCheck className={styles.titleIcon} size={22} />
            <h2>비밀번호 찾기</h2>
          </div>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={24} />
          </button>
        </header>

        <div className={styles.content}>
          {/* STEP 1: 아이디 및 이메일 확인 */}
          {!onCheckEmail ? (
            <form onSubmit={onFindPass} className={styles.stepForm}>
              <p className={styles.description}>가입 시 등록한 아이디와 이메일을 입력해주세요.</p>

              <div className={styles.fieldGroup}>
                <label className={styles.label}>
                  <User size={14} /> 아이디
                </label>
                <input
                  type="text"
                  name="adminLoginId"
                  placeholder="아이디를 입력하세요"
                  value={formData.adminLoginId}
                  onChange={handleInputChange}
                  className={styles.input}
                  required
                />
              </div>

              <div className={styles.fieldGroup}>
                <label className={styles.label}>
                  <Mail size={14} /> 이메일
                </label>
                <input
                  type="email"
                  name="adminEmail"
                  placeholder="example@email.com"
                  value={formData.adminEmail}
                  onChange={handleInputChange}
                  className={`${styles.input} ${formData.adminEmail && !isEmailValid ? styles.inputError : ""}`}
                  required
                />
                {formData.adminEmail && !isEmailValid && (
                  <p className={styles.errorText}>올바른 이메일 형식이 아닙니다.</p>
                )}
              </div>

              <button
                type="submit"
                className={styles.submitButton}
                disabled={!formData.adminLoginId || !isEmailValid}
              >
                계정 확인 <ArrowRight size={18} />
              </button>
            </form>
          ) : (
            /* STEP 2: 새 비밀번호 설정 */
            <form onSubmit={onChangePass} className={styles.stepForm}>
              <div className={styles.successBadge}>
                <CheckCircle2 size={16} /> 계정이 확인되었습니다. 새 비밀번호를 설정하세요.
              </div>

              <div className={styles.fieldGroup}>
                <label className={styles.label}>
                  <Lock size={14} /> 새 비밀번호
                </label>
                <input
                  type="password"
                  name="newPassword"
                  placeholder="8자 이상 입력"
                  value={changePassForm.newPassword}
                  onChange={handlePassChange}
                  className={styles.input}
                  required
                />
              </div>

              <div className={styles.fieldGroup}>
                <label className={styles.label}>
                  <CheckCircle2 size={14} /> 비밀번호 확인
                </label>
                <input
                  type="password"
                  name="confirmNewPassword"
                  placeholder="비밀번호 재입력"
                  value={changePassForm.confirmNewPassword}
                  onChange={handlePassChange}
                  className={styles.input}
                  required
                />
                {changePassForm.confirmNewPassword && (
                  <p className={isPasswordMatch ? styles.successText : styles.errorText}>
                    {isPasswordMatch ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다."}
                  </p>
                )}
              </div>

              <button
                type="submit"
                className={styles.changeButton}
                disabled={!isPasswordValid || !isPasswordMatch}
              >
                비밀번호 변경 완료
              </button>

              <button
                type="button"
                className={styles.backButton}
                onClick={() => setOnCheckEmail(false)}
              >
                이전 단계로
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

export default FindPasswordModal;
