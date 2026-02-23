import React from "react";
import {X, UserCog, Lock, User, Phone, Mail, CheckCircle2} from "lucide-react";
import styles from "./InitialSetupModal.module.css";

const InitialSetupModal = ({open, onClose, onSubmit, formData, setFormData}) => {
  if (!open) return null;

  const handleChange = (e) => {
    const {name, value} = e.target;
    setFormData((prev) => ({...prev, [name]: value}));
  };

  // 비밀번호 일치 여부 확인
  const isPasswordMatch =
    formData.newPassword && formData.newPassword === formData.confirmNewPassword;

  return (
    <div className={styles.backdrop}>
      <div className={styles.modal}>
        <header className={styles.header}>
          <div className={styles.iconWrapper}>
            <UserCog size={24} className={styles.headerIcon} />
          </div>
          <div className={styles.headerText}>
            <h2 className={styles.title}>초기 설정</h2>
            <p className={styles.subtitle}>최초 로그인 시 관리자 정보를 설정해야 합니다.</p>
          </div>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={20} />
          </button>
        </header>

        <form onSubmit={onSubmit} className={styles.content}>
          <div className={styles.scrollArea}>
            {/* 관리자 이름 */}
            <div className={styles.fieldGroup}>
              <label className={styles.label}>
                <User size={14} /> 이름
              </label>
              <input
                type="text"
                name="adminName"
                placeholder="관리자 실명을 입력하세요"
                value={formData.adminName}
                onChange={handleChange}
                className={styles.input}
                required
              />
            </div>

            {/* 새 비밀번호 */}
            <div className={styles.fieldGroup}>
              <label className={styles.label}>
                <Lock size={14} /> 새 비밀번호
              </label>
              <input
                type="password"
                name="newPassword"
                placeholder="8자 이상의 비밀번호"
                value={formData.newPassword}
                onChange={handleChange}
                className={styles.input}
                required
              />
            </div>

            {/* 비밀번호 확인 */}
            <div className={styles.fieldGroup}>
              <label className={styles.label}>
                <CheckCircle2 size={14} /> 비밀번호 확인
              </label>
              <input
                type="password"
                name="confirmNewPassword"
                placeholder="비밀번호 재입력"
                value={formData.confirmNewPassword}
                onChange={handleChange}
                className={`${styles.input} ${isPasswordMatch ? styles.inputSuccess : ""}`}
                required
              />
              {formData.confirmNewPassword && (
                <p className={isPasswordMatch ? styles.successText : styles.errorText}>
                  {isPasswordMatch ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다."}
                </p>
              )}
            </div>

            {/* 연락처 */}
            <div className={styles.fieldGroup}>
              <label className={styles.label}>
                <Phone size={14} /> 연락처
              </label>
              <input
                type="tel"
                name="adminPhone"
                placeholder="010-0000-0000"
                value={formData.adminPhone}
                onChange={handleChange}
                className={styles.input}
                required
              />
            </div>

            {/* 이메일 */}
            <div className={styles.fieldGroup}>
              <label className={styles.label}>
                <Mail size={14} /> 이메일
              </label>
              <input
                type="email"
                name="adminEmail"
                placeholder="example@email.com"
                value={formData.adminEmail}
                onChange={handleChange}
                className={styles.input}
                required
              />
            </div>
          </div>

          <footer className={styles.footer}>
            <button type="submit" className={styles.submitButton} disabled={!isPasswordMatch}>
              설정 완료 및 시작하기
            </button>
          </footer>
        </form>
      </div>
    </div>
  );
};

export default InitialSetupModal;
