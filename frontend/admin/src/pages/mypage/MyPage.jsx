import {useEffect, useState} from "react";
import styles from "./MyPage.module.css";
import {getAdminInfo, adminInfoUpdate} from "../../api/admin/adminAPI";

import ProfileCard from "./components/ProfileCard";
import StatsSection from "./components/StatsSection";
import EditAdminModal from "./components/EditAdminModal";
import LogHistoryModal from "./components/LogHistoryModal";

export default function MyPage() {
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isLogHistoryOpen, setIsLogHistoryOpen] = useState(false);

  const [adminInfo, setAdminInfo] = useState({
    adminLoginId: "",
    adminName: "",
    adminPhone: "",
    adminEmail: "",
    adminRole: "",
    createdAt: "",
  });

  const [adminStats, setAdminStats] = useState({
    resolvedComplaintCount: 0,
    postedNoticeCount: 0,
    totalWorkingDays: 0,
    historyRes: [],
  });

  const [updateForm, setUpdateForm] = useState({
    adminName: "",
    adminPhone: "",
    adminEmail: "",
    currentPassword: "",
    newPassword: "",
    confirmNewPassword: "",
  });

  const [errorState, setErrorState] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
    general: "",
  });

  useEffect(() => {
    getAdminInfo(localStorage.getItem("adminId"))
      .then((res) => {
        console.log("Admin Info Response:", res.data);
        setAdminInfo(res.data.adminRes);
        setAdminStats({
          resolvedComplaintCount: res.data.resolvedComplaintCount,
          postedNoticeCount: res.data.postedNoticeCount,
          totalWorkingDays: res.data.totalWorkingDays,
          historyRes: res.data.historyRes,
        });
      })
      .catch((err) => {
        const {code, message} = err.response.data.error;

        if (code === "ADMIN_NOT_FOUND") {
          alert(message);
        } else {
          alert("알 수 없는 오류가 발생했습니다.");
        }
      });
  }, []);

  const validateForm = () => {
    const errors = {
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
      general: "",
    };

    if (!updateForm.adminName || !updateForm.adminPhone || !updateForm.adminEmail) {
      errors.general = "모든 항목을 입력해주세요.";
    }

    if (updateForm.newPassword || updateForm.confirmNewPassword) {
      if (!updateForm.currentPassword) {
        errors.currentPassword = "현재 비밀번호를 입력해주세요.";
      }

      if (updateForm.newPassword.length < 8) {
        errors.newPassword = "새 비밀번호는 최소 8자 이상이어야 합니다.";
      }

      if (updateForm.newPassword !== updateForm.confirmNewPassword) {
        errors.confirmPassword = "비밀번호가 일치하지 않습니다.";
      }
    }

    setErrorState(errors);

    return Object.values(errors).every((v) => v === "");
  };

  const handleSaveInfo = () => {
    if (!validateForm()) {
      alert("입력한 정보를 확인해주세요.");
      return;
    }

    const adminId = localStorage.getItem("adminId");

    adminInfoUpdate(adminId, updateForm)
      .then((res) => {
        setAdminInfo({
          ...adminInfo,
          adminName: updateForm.adminName,
          adminPhone: updateForm.adminPhone,
          adminEmail: updateForm.adminEmail,
        });

        updateForm.currentPassword = "";
        updateForm.newPassword = "";
        updateForm.confirmNewPassword = "";

        alert(res.data);

        setIsEditModalOpen(false);
      })
      .catch((err) => {
        const {code, message} = err.response.data.error;

        if (code === "ADMIN_NOT_FOUND") {
          alert(message);
        } else if (code === "CURRENT_PASSWORD_MISMATCH") {
          setErrorState({...errorState, currentPassword: message});
          alert(message);
        } else if (code === "SAME_AS_OLD_PASSWORD") {
          setErrorState({...errorState, newPassword: message});
          alert(message);
        } else {
          alert("알 수 없는 오류가 발생했습니다.");
        }
      });
  };

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>마이페이지</h1>

      <ProfileCard
        adminInfo={adminInfo}
        onEdit={() => {
          setUpdateForm({
            ...updateForm,
            adminName: adminInfo.adminName,
            adminPhone: adminInfo.adminPhone,
            adminEmail: adminInfo.adminEmail,
          });
          setIsEditModalOpen(true);
        }}
      />

      <StatsSection adminStats={adminStats} onClickLogHistory={() => setIsLogHistoryOpen(true)} />

      <EditAdminModal
        open={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        updateForm={updateForm}
        setUpdateForm={setUpdateForm}
        errorState={errorState}
        onSave={handleSaveInfo}
      />

      <LogHistoryModal open={isLogHistoryOpen} onClose={() => setIsLogHistoryOpen(false)} />
    </div>
  );
}
