import { useEffect, useState, useCallback } from "react";
import { toast } from "sonner";

import {
  createAdmin,
  getAdminList,
  getAdminsStats,
  updateAdminRole,
  deleteAdmin,
} from "../../api/admin/adminAPI";

import AdminsStatsSection from "./components/AdminsStatsSection";
import AdminList from "./components/AdminList";
import CreateAdminModal from "./components/CreateAdminModal";
import ViewDetailModal from "./components/ViewDetailModal";
import EditRoleModal from "./components/EditRoleModal";
import DeleteAdminModal from "./components/DeleteAdminModal";

import styles from "./AdminsPage.module.css";

export default function AdminsPage() {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isEditRoleModalOpen, setIsEditRoleModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedAdmin, setSelectedAdmin] = useState(null);

  // 관리자 추가 모달 state
  const [idCheckStatus, setIdCheckStatus] = useState("");
  const [passwordCheckStatus, setPasswordCheckStatus] = useState("");

  // 관리자 생성 폼
  const [createAdminForm, setCreateAdminForm] = useState({
    adminLoginId: "",
    adminPass: "",
    confirmPass: "",
    adminRole: "",
  });

  // 관리자 역할 수정 폼
  const [editRoleForm, setEditRoleForm] = useState({
    adminPass: "",
    adminRole: "",
  });

  // 관리자 삭제 폼
  const [deleteAdminForm, setDeleteAdminForm] = useState({
    adminPass: "",
  });

  const [admins, setAdmins] = useState([]);

  const handleCheckDuplicateId = () => {
    if (!createAdminForm.adminLoginId) return toast.error("아이디를 입력해주세요");
    const isDuplicate = admins.some((admin) => admin.adminLoginId === createAdminForm.adminLoginId);
    setIdCheckStatus(isDuplicate ? "duplicate" : "available");
    isDuplicate
      ? toast.error("이미 사용 중인 아이디입니다")
      : toast.success("사용 가능한 아이디입니다");
  };

  const validatePassword = () => {
    if (createAdminForm.adminPass.length < 8) {
      setPasswordCheckStatus("invalid");
      return toast.error("비밀번호는 최소 8자 이상이어야 합니다");
    }
    if (createAdminForm.adminPass !== createAdminForm.confirmPass) {
      setPasswordCheckStatus("invalid");
      return toast.error("비밀번호가 일치하지 않습니다");
    }
    setPasswordCheckStatus("valid");
    return true;
  };

  const handleCreateAdmin = () => {
    if (
      !createAdminForm.adminLoginId ||
      !createAdminForm.adminPass ||
      !createAdminForm.adminRole ||
      idCheckStatus !== "available" ||
      passwordCheckStatus !== "valid"
    ) {
      toast.error("입력 정보를 확인해주세요");
      return;
    }

    createAdmin(createAdminForm)
      .then((res) => {
        console.log("관리자 추가 성공:", res);
        toast.success("관리자가 추가되었습니다");
        setIsCreateModalOpen(false);
        setCreateAdminForm({
          adminLoginId: "",
          adminPass: "",
          confirmPass: "",
          adminRole: "",
        });
        fetchAdmins(); // 새로 추가된 관리자 목록을 다시 불러옴
      })
      .catch((err) => {
        console.error("관리자 추가 실패:", err);
        toast.error("관리자 추가에 실패했습니다");
      });
  };

  const [adminsStats, setAdminsStats] = useState({
    totalAdmins: 0,
    activeAdmins: 0,
    newAdmins: 0,
  });

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [searchCond, setSearchCond] = useState({
    adminLoginId: "",
    adminName: "",
    state: "",
    adminRole: "",
  });

  // API 호출 함수
  useEffect(() => {
    getAdminsStats()
      .then((res) => {
        const statsData = res.data;
        setAdminsStats(statsData);
      })
      .catch((err) => console.error("통계 로딩 실패:", err));
  }, []);

  // 2. 관리자 목록 가져오기 함수 (useCallback으로 메모이제이션)
  const fetchAdmins = useCallback(() => {
    const params = {
      adminLoginId: searchCond.adminLoginId || null,
      adminName: searchCond.adminName || null,
      state: searchCond.state === "" ? null : searchCond.state === "true",
      adminRole: searchCond.adminRole || null,
    };
    const pageable = {
      page: page,
      size: 10,
      sort: ["createdAt,desc"],
    };

    getAdminList(params, pageable)
      .then((res) => {
        const pageData = res.data.data || res.data;
        setAdmins(pageData.content || []);
        setTotalPages(pageData.totalPages || 0);
      })
      .catch((err) => {
        console.error(err);
      });
  }, [page, searchCond]); // 여기서 searchCond를 넣어두면 필터 바뀔 때마다 자동 검색됨

  // 3. 목록 호출 Effect
  useEffect(() => {
    fetchAdmins();
  }, [fetchAdmins]);

  // 검색 버튼 클릭 시 (수동 검색을 원할 경우)
  const handleSearch = () => {
    setPage(0);
    fetchAdmins();
  };

  const handleUpdateAdminRole = () => {
    const adminId = localStorage.getItem("adminId");
    const targetAdminId = selectedAdmin.adminId;

    if (!editRoleForm.adminRole) {
      return toast.error("권한을 선택해주세요");
    }

    if (!editRoleForm.adminPass) {
      return toast.error("비밀번호를 입력해주세요");
    }

    updateAdminRole(adminId, targetAdminId, editRoleForm)
      .then((res) => {
        console.log("관리자 권한 수정 성공:", res);
        toast.success("관리자 권한이 수정되었습니다");
        setEditRoleForm({ adminPass: "", adminRole: "" });
        fetchAdmins();
        setIsEditRoleModalOpen(false);
      })
      .catch((err) => {
        console.error("관리자 권한 수정 실패:", err);
        const { code, message } = err.response.data.error;
        toast.error(message || "관리자 권한 수정에 실패했습니다");
      });
  };

  const handleDeleteAdmin = () => {
    const adminId = localStorage.getItem("adminId");
    const targetAdminId = selectedAdmin.adminId;

    if (!deleteAdminForm.adminPass) {
      return toast.error("비밀번호를 입력해주세요");
    }

    deleteAdmin(adminId, targetAdminId, deleteAdminForm)
      .then((res) => {
        console.log("관리자 삭제 성공:", res);
        toast.success("관리자가 삭제되었습니다");
        setIsDeleteModalOpen(false);
        setDeleteAdminForm({ adminPass: "" });
        fetchAdmins();
      })
      .catch((err) => {
        console.error("관리자 삭제 실패:", err);
        const { code, message } = err.response.data.error;
        toast.error(message || "관리자 삭제에 실패했습니다");
      });
  };

  return (
    <div className={styles.container}>
      <AdminsStatsSection
        adminsStats={adminsStats}
        onClickCreateModalOpen={() => setIsCreateModalOpen(true)}
      />

      <AdminList
        admins={admins}
        searchCond={searchCond}
        setSearchCond={setSearchCond}
        onSearch={handleSearch}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        onViewDetail={(admin) => {
          setSelectedAdmin(admin);
          setIsDetailModalOpen(true);
        }}
        onEditRole={(admin) => {
          setSelectedAdmin(admin);
          setIsEditRoleModalOpen(true);
        }}
        onDelete={(admin) => {
          setSelectedAdmin(admin);
          setIsDeleteModalOpen(true);
        }}
      />

      <CreateAdminModal
        open={isCreateModalOpen}
        onClose={() => {
          setIsCreateModalOpen(false);
          setCreateAdminForm({ adminLoginId: "", adminName: "", adminPass: "", adminRole: "" });
        }}
        formData={createAdminForm}
        setFormData={setCreateAdminForm}
        onSubmit={handleCreateAdmin}
        idCheckStatus={idCheckStatus}
        onCheckDuplicateId={handleCheckDuplicateId}
        passwordCheckStatus={passwordCheckStatus}
        onCheckvalidatePassword={validatePassword}
      />

      <ViewDetailModal
        open={isDetailModalOpen}
        onClose={() => setIsDetailModalOpen(false)}
        admin={selectedAdmin}
      />

      <EditRoleModal
        open={isEditRoleModalOpen}
        onClose={() => {
          setIsEditRoleModalOpen(false);
          setEditRoleForm({ adminPass: "", adminRole: "" });
        }}
        admin={selectedAdmin}
        editRole={editRoleForm}
        setEditRole={setEditRoleForm}
        onSubmit={() => {
          handleUpdateAdminRole();
        }}
      />

      <DeleteAdminModal
        open={isDeleteModalOpen}
        onClose={() => {
          setIsDeleteModalOpen(false);
          setDeleteAdminForm({ adminPass: "" });
        }}
        admin={selectedAdmin}
        deleteForm={deleteAdminForm}
        setDeleteForm={setDeleteAdminForm}
        onSubmit={() => {
          handleDeleteAdmin();
        }}
      />
    </div>
  );
}
