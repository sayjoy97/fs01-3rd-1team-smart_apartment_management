import React, {useState, useEffect} from "react";
import {X, AlertCircle, CheckCircle2, Settings2} from "lucide-react";
import styles from "./EditStateModal.module.css";

const EditStateModal = ({open, close, onSubmit, elevator, state, setState}) => {
  // 상태 목록 정의
  const stateOptions = [
    {
      value: "IDLE",
      label: "정상 대기",
      description: "엘리베이터가 정상적으로 운행 가능한 상태입니다.",
      color: styles.idle,
    },
    {
      value: "REPAIR",
      label: "점검 중",
      description: "정기 점검 또는 수리 중으로 이용이 제한됩니다.",
      color: styles.repair,
    },
    {
      value: "ERROR",
      label: "고장",
      description: "긴급 고장 발생으로 즉각적인 조치가 필요한 상태입니다.",
      color: styles.error,
    },
  ];

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

  return (
    <div className={styles.backdrop} onClick={(e) => e.target === e.currentTarget && close()}>
      <div className={styles.modal}>
        <header className={styles.header}>
          <div className={styles.titleGroup}>
            <Settings2 className={styles.headerIcon} size={20} />
            <div>
              <h2>상태 변경 설정</h2>
              {elevator && (
                <p className={styles.subtitle}>
                  {elevator.dong.replace("D", "")}동 {elevator.hogi}호기
                </p>
              )}
            </div>
          </div>
          <button className={styles.closeButton} onClick={close}>
            <X size={24} />
          </button>
        </header>

        <div className={styles.content}>
          <div className={styles.alertBox}>
            <AlertCircle size={18} />
            <p>상태를 변경하면 입주민 앱 및 관제 시스템에 실시간으로 반영됩니다.</p>
          </div>

          <div className={styles.stateGrid}>
            {stateOptions.map((option) => (
              <label
                key={option.value}
                className={`${styles.stateCard} ${state === option.value ? styles.selected : ""} ${option.color}`}
              >
                <input
                  type="radio"
                  name="elevatorState"
                  value={option.value}
                  checked={state === option.value}
                  onChange={(e) => setState(e.target.value)}
                  className={styles.hiddenInput}
                />
                <div className={styles.cardHeader}>
                  <span className={styles.radioCircle}></span>
                  <span className={styles.stateLabel}>{option.label}</span>
                </div>
                <p className={styles.stateDescription}>{option.description}</p>
                {state === option.value && <CheckCircle2 className={styles.checkIcon} size={20} />}
              </label>
            ))}
          </div>
        </div>

        <footer className={styles.footer}>
          <button className={styles.cancelButton} onClick={close}>
            취소
          </button>
          <button
            className={styles.submitButton}
            onClick={() => onSubmit(state)}
            disabled={elevator && elevator.state === state}
          >
            상태 저장하기
          </button>
        </footer>
      </div>
    </div>
  );
};

export default EditStateModal;
