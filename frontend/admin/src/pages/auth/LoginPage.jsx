import {useState} from "react";
import {useNavigate} from "react-router-dom";
import styles from "./LoginPage.module.css";
import {Building2, LogIn} from "lucide-react";

import {initialSetupAdmin, login, logout, findPass, changePass} from "../../api/admin/adminAPI";

import InitialSetupModal from "./components/InitialSetupModal";
import FindPasswordModal from "./components/FindPasswordModal";

export default function LoginPage() {
  const navigate = useNavigate();

  const [isSetupModalOpen, setIsSetupModalOpen] = useState(false);
  const [isFindPassModalOpen, setIsFindPassModalOpen] = useState(false);

  const [onCheckInitialSetup, setOnCheckInitialSetup] = useState(false);
  const [onCheckEmail, setOnCheckEmail] = useState(false);

  const [adminform, setAdminForm] = useState({
    adminLoginId: "",
    adminPass: "",
  });

  const [initialSetupForm, setInitialSetupForm] = useState({
    adminName: "",
    newPassword: "",
    confirmNewPassword: "",
    adminPhone: "",
    adminEmail: "",
  });

  const [findPassForm, setFindPassForm] = useState({
    adminLoginId: "",
    adminEmail: "",
  });

  const [changePassForm, setChangePassForm] = useState({
    adminLoginId: "",
    newPassword: "",
    confirmNewPassword: "",
  });

  const handleChange = (e) => {
    const {name, value} = e.target;
    setAdminForm((prev) => ({...prev, [name]: value}));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    login(adminform)
      .then((res) => {
        if (!res || !res.data.loginAdminRes) {
          alert("로그인 응답이 올바르지 않습니다.");
          return;
        }

        localStorage.setItem("accessToken", res.data.accessToken);
        localStorage.setItem("roles", JSON.stringify(res.data.roles));
        localStorage.setItem("adminId", res.data.loginAdminRes.adminId);

        if (res.data.loginAdminRes.isFirstLogin) {
          setIsSetupModalOpen(true);
        } else {
          navigate("/");
        }
      })
      .catch((err) => {
        if (err.response) {
          const {code, message} = err.response.data.error || {};
          alert(message || "로그인 실패");
        } else {
          alert("서버 연결 실패 또는 CORS 오류");
        }
      });
  };

  const handleInitialSetup = (e) => {
    e.preventDefault();

    const adminId = localStorage.getItem("adminId");

    // 1. API 호출 (예: updateInitialAdminInfo)
    initialSetupAdmin(adminId, initialSetupForm)
      .then((res) => {
        // 성공 시
        setOnCheckInitialSetup(true);
        setIsSetupModalOpen(false);
        navigate("/"); // 대시보드로 이동
      })
      .catch((err) => {
        console.error("초기 설정 오류:", err);
        if (err.response) {
          const {code, message} = err.response.data.error || {};
          alert(message || "초기 설정 실패");
        } else {
          alert("설정 중 오류가 발생했습니다. 보안을 위해 다시 로그인해 주세요.");

          // 2. 오류 시 로그아웃 처리 및 리셋
          logout().finally(() => {
            localStorage.clear();
            setIsSetupModalOpen(false);
            setAdminForm({adminLoginId: "", adminPass: ""}); // 로그인 폼 초기화
            setInitialSetupForm({
              adminName: "",
              newPassword: "",
              confirmNewPassword: "",
              adminPhone: "",
              adminEmail: "",
            }); // 초기 설정 폼 초기화
            navigate("/login");
          });
        }
      });
  };

  const handleFindPass = (e) => {
    e.preventDefault();
    // findPass API 호출
    findPass(findPassForm)
      .then((res) => {
        // 성공 시 이메일 확인 상태를 true로 변경하여 다음 단계(비밀번호 입력)로 전환
        setOnCheckEmail(true);
        // 비밀번호 변경 폼에 아이디 미리 세팅
        setChangePassForm((prev) => ({...prev, adminLoginId: findPassForm.adminLoginId}));
      })
      .catch((err) => {
        const {code, message} = err.response.data.error || {};
        alert(message || "일치하는 계정 정보가 없습니다.");
      });
  };

  const handleChangePass = (e) => {
    e.preventDefault();
    // changePass API 호출
    changePass(changePassForm)
      .then((res) => {
        alert("비밀번호가 변경되었습니다. 새 비밀번호로 로그인해주세요.");
        setIsFindPassModalOpen(false);
        setOnCheckEmail(false);
        // 폼 초기화 로직 추가...
      })
      .catch((err) => {
        alert("비밀번호 변경에 실패했습니다.");
      });
  };

  return (
    <div className={styles.container}>
      <div className={styles.loginBox}>
        <header className={styles.header}>
          <div className={styles.logoWrapper}>
            <div className={styles.logoCircle}>
              <Building2 className={styles.logoIcon} />
            </div>
          </div>
          <h2 className={styles.title}>아파트 관리 시스템</h2>
          <p className={styles.subtitle}>단지 관리자 로그인</p>
        </header>

        <form className={styles.content} onSubmit={handleSubmit}>
          <div className={styles.fieldGroup}>
            <label htmlFor="adminLoginId" className={styles.label}>
              아이디
            </label>
            <input
              id="adminLoginId"
              type="text"
              name="adminLoginId"
              placeholder="아이디를 입력하세요"
              value={adminform.adminLoginId}
              onChange={handleChange}
              className={styles.input}
              required
            />
          </div>
          <div className={styles.fieldGroup}>
            <label htmlFor="adminPass" className={styles.label}>
              비밀번호
            </label>
            <input
              id="adminPass"
              type="password"
              name="adminPass"
              placeholder="비밀번호를 입력하세요"
              value={adminform.adminPass}
              onChange={handleChange}
              className={styles.input}
              required
            />
          </div>

          <button type="submit" className={styles.loginButton}>
            <LogIn className={styles.loginIcon} size={18} />
            로그인
          </button>

          <div className={styles.footer}>
            <button
              type="button"
              className={styles.findPassButton}
              onClick={() => setIsFindPassModalOpen(true)}
            >
              비밀번호를 잊으셨나요?
            </button>
          </div>

          <div className={styles.testInfo}>
            <p>
              테스트 계정: <strong>admin01</strong> / <strong>11111111</strong>
            </p>
          </div>
        </form>
      </div>

      <InitialSetupModal
        open={isSetupModalOpen}
        onClose={() => setIsSetupModalOpen(false)}
        onSubmit={handleInitialSetup}
        formData={initialSetupForm}
        setFormData={setInitialSetupForm}
        setOnCheckInitialSetup={setOnCheckInitialSetup}
      />

      <FindPasswordModal
        open={isFindPassModalOpen}
        onClose={() => setIsFindPassModalOpen(false)}
        onFindPass={handleFindPass}
        onChangePass={handleChangePass}
        formData={findPassForm}
        setFormData={setFindPassForm}
        onCheckEmail={onCheckEmail}
        setOnCheckEmail={setOnCheckEmail}
        changePassForm={changePassForm}
        setPassForm={setChangePassForm}
      />
    </div>
  );
}
