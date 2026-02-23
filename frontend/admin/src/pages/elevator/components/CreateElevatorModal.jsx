import React from "react";
import {X, Building, Hash, PlusCircle} from "lucide-react";
import styles from "./CreateElevatorModal.module.css";
import {toast} from "sonner";
import {useEffect} from "react";

const CreateElevatorModal = ({
  open,
  close,
  onSubmit,
  createElevatorForm,
  setCreateElevatorForm,
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

  const userRole = localStorage.getItem("roles");

  if (!(userRole.includes("ROLE_SUPER_ADMIN") || userRole.includes("ROLE_ACTING_ADMIN"))) {
    toast.error("접근 권한이 없습니다");
    close();
  }

  const handleChange = (e) => {
    const {name, value} = e.target;

    // 호기(hogi) 입력 시 숫자만 허용
    if (name === "hogi") {
      const onlyNumber = value.replace(/[^0-9]/g, "");
      setCreateElevatorForm((prev) => ({...prev, [name]: onlyNumber}));
      return;
    }

    setCreateElevatorForm((prev) => ({...prev, [name]: value}));
  };

  const dongOptions = [
    {label: "101동", value: "D101"},
    {label: "102동", value: "D102"},
    {label: "103동", value: "D103"},
    {label: "104동", value: "D104"},
  ];

  return (
    <div className={styles.backdrop} onClick={(e) => e.target === e.currentTarget && close()}>
      <div className={styles.modal}>
        <header className={styles.header}>
          <div className={styles.titleWrapper}>
            <PlusCircle className={styles.icon} size={20} />
            <h2>새 엘리베이터 등록</h2>
          </div>
          <button className={styles.closeButton} onClick={close}>
            <X size={24} />
          </button>
        </header>

        <form
          className={styles.content}
          onSubmit={(e) => {
            e.preventDefault();
            onSubmit();
          }}
        >
          <div className={styles.fieldGroup}>
            <label htmlFor="dong">
              <Building size={16} /> 해당 동 선택
            </label>
            <select
              id="dong"
              name="dong"
              value={createElevatorForm.dong}
              onChange={handleChange}
              className={styles.select}
              required
            >
              <option value="">동을 선택하세요</option>
              {dongOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </select>
          </div>

          <div className={styles.fieldGroup}>
            <label htmlFor="hogi">
              <Hash size={16} /> 호기 입력
            </label>
            <input
              id="hogi"
              type="text"
              name="hogi"
              placeholder="숫자만 입력 (예: 1)"
              value={createElevatorForm.hogi}
              onChange={handleChange}
              className={styles.input}
              required
            />
            <p className={styles.helperText}>숫자로 구성된 엘리베이터 고유 호기를 입력하세요.</p>
          </div>

          <footer className={styles.footer}>
            <button type="button" className={styles.cancelButton} onClick={close}>
              취소
            </button>
            <button type="submit" className={styles.submitButton}>
              등록하기
            </button>
          </footer>
        </form>
      </div>
    </div>
  );
};

export default CreateElevatorModal;
