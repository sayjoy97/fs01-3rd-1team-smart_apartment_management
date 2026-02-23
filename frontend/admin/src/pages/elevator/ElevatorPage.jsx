import {useCallback, useEffect, useState} from "react";
import {AlertTriangle, Wrench, Play, DoorOpen} from "lucide-react";
// import {ImageWithFallback} from "../../components/figma/ImageWithFallback";
import {toast} from "sonner";
import {
  createElevator,
  deleteElevator,
  getElevatorDetail,
  getElevatorList,
  getElevatorsStats,
  updateElevatorState,
} from "../../api/elevator/elevatorAPI";
import ElevatorsStatsSection from "./components/ElevatorsStatsSection";
import CreateElevatorModal from "./components/CreateElevatorModal";
import ElevatorList from "./components/ElevatorList";
import ViewDetailModal from "./components/ViewDetailModal";
import EditStateModal from "./components/EditStateModal";
import DeleteElevatorModal from "./components/DeleteElevatorModal";

export function ElevatorPage() {
  const [isCreateElevatorModalOpen, setIsCreateElevatorModalOpen] = useState(false);
  const [isElevatorDetailModalOpen, setIsElevatorDetailModalOpen] = useState(false);
  const [isEditStateModalOpen, setIsEditStateModalOpen] = useState(false);
  const [isDeleteElevatorModalOpen, setIsDeleteElevatorModalOpen] = useState(false);

  // 엘리베이터 아이디를 저장
  const [selectedElevatorId, setSelectedElevatorId] = useState();
  // 엘리베이터를 저장
  const [selectedElevator, setSelectedElevator] = useState("");
  // 엘리베이터 리스트를 저장
  const [elevators, setElevators] = useState([]);

  // 엘리베이터 통계 정보
  const [elevatorsStats, setElevatorsStats] = useState({
    totalElevators: 0,
    errorElevators: 0,
    repairElevators: 0,
  });

  // 엘리베이터 통계 정보 조회 API
  useEffect(() => {
    getElevatorsStats()
      .then((res) => {
        setElevatorsStats(res.data);
        console.log("엘리베이터 통계 정보:", res.data);
      })
      .catch((err) => {
        console.error("엘리베이터 통계 정보 조회 실패:", err);
        const {code, message} = err.response.data.error;
        toast.error(message || "엘리베이터 통계 정보 조회에 실패했습니다");
      });
  }, []);

  // 엘리베이터 등록 폼
  const [createElevatorForm, setCreateElevatorForm] = useState({
    dong: "",
    hogi: "",
  });

  // 엘리베이터 등록 함수
  const handleCreateElevator = () => {
    const adminId = localStorage.getItem("adminId");
    if (!createElevatorForm.dong || !createElevatorForm.hogi) {
      return toast.error("모든 항목을 입력해주세요");
    }
    createElevator(adminId, createElevatorForm)
      .then((res) => {
        toast.success("엘리베이터가 등록되었습니다");
        setIsCreateElevatorModalOpen(false);
        setCreateElevatorForm({dong: "", hogi: ""});
        fetchElevators();
      })
      .catch((err) => {
        console.error("엘리베이터 등록 실패:", err);
        const {code, message} = err.response.data.error;
        toast.error(message || "엘리베이터 등록에 실패했습니다");
      });
  };

  // 엘리베이터 목록 조회 폼
  const [searchCond, setSearchCond] = useState({
    dong: "",
    hogi: "",
    state: "",
  });

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // 엘리베이터 목록 가져오기 함수 (useCallback으로 메모이제이션)
  const fetchElevators = useCallback(() => {
    const searchParams = {
      dong: searchCond.dong || null,
      hogi: searchCond.hogi || null,
      state: searchCond.state === "" ? null : searchCond.state,
    };
    const pageable = {
      page: page,
      size: 10,
      sort: ["dong,asc", "hogi,asc"],
    };

    getElevatorList(searchParams, pageable)
      .then((res) => {
        const pageData = res.data.data || res.data;
        setElevators(pageData.content || []);
        setTotalPages(pageData.totalPages || 0);
      })
      .catch((err) => {
        console.error(err);
      });
  }, [page, searchCond]); // 여기서 searchCond를 넣어두면 필터 바뀔 때마다 자동 검색됨

  // 목록 호출 Effect
  useEffect(() => {
    fetchElevators();
  }, [fetchElevators]);

  // 엘리베이터 상태
  const [elevatorState, setElevatorState] = useState();

  // 엘리베이터 상태 변경 함수
  const handleEditState = () => {
    updateElevatorState(selectedElevator.elevatorId, elevatorState)
      .then((res) => {
        toast.success("엘리베이터 상태가 성공적으로 변경되었습니다.");
        setIsEditStateModalOpen(false);
        setSelectedElevator();
        fetchElevators();
      })
      .catch((err) => {
        const {code, message} = err.response.data.error;
        toast.error(message || "상태 변경에 실패했습니다.");
      });
  };

  // 관리자 비밀번호
  const [deleteElevatorForm, setDeleteElevatorForm] = useState({
    adminPass: "",
  });

  // 엘리베이터 삭제 함수
  const handleDeleteElevator = () => {
    const adminId = localStorage.getItem("adminId");
    deleteElevator(selectedElevator.elevatorId, adminId, deleteElevatorForm)
      .then((res) => {
        toast.success("엘리베이터가 삭제되었습니다.");
        setIsDeleteElevatorModalOpen(false);
        setSelectedElevatorId();
        setDeleteElevatorForm({adminPass: ""});
        fetchElevators();
      })
      .catch((err) => {
        const {code, message} = err.response.data.error;
        toast.error(message || "엘리베이터 삭제에 실패했습니다.");
      });
  };

  // 광고 등록 폼
  const [createAdvertisementForm, setCreateAdvertisementForm] = useState({
    advertisementTitle: "",
    advertisementContent: "",
    advertisementStartDate: "",
    advertisementEndDate: "",
  });

  // 광고 등록 함수
  const handleCreateAdvertisement = () => {
    const adminId = localStorage.getItem("adminId");
  };

  return (
    <div className="space-y-6">
      {/* 통계 카드 */}
      <ElevatorsStatsSection
        elevatorsStats={elevatorsStats}
        onClickCreateModalOpen={() => setIsCreateElevatorModalOpen(true)}
      />
      {/* 엘리베이터 카드 영역 */}
      <ElevatorList
        elevators={elevators}
        searchCond={searchCond}
        setSearchCond={setSearchCond}
        onSearch={fetchElevators}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        onViewDetail={(elevatorId) => {
          setSelectedElevatorId(elevatorId);
          setIsElevatorDetailModalOpen(true);
        }}
        onEditState={(elevator) => {
          setSelectedElevator(elevator);
          setIsEditStateModalOpen(true);
        }}
        onDelete={(elevator) => {
          setSelectedElevator(elevator);
          setIsDeleteElevatorModalOpen(true);
        }}
      />
      <CreateElevatorModal
        open={isCreateElevatorModalOpen}
        close={() => {
          setIsCreateElevatorModalOpen(false);
          setCreateElevatorForm({
            dong: "",
            hogi: "",
          });
        }}
        onSubmit={handleCreateElevator}
        createElevatorForm={createElevatorForm}
        setCreateElevatorForm={setCreateElevatorForm}
      />
      <ViewDetailModal
        open={isElevatorDetailModalOpen}
        close={() => {
          setIsElevatorDetailModalOpen(false);
          setSelectedElevatorId();
        }}
        elevatorId={selectedElevatorId}
      />
      <EditStateModal
        open={isEditStateModalOpen}
        close={() => {
          setIsEditStateModalOpen(false);
          setSelectedElevator();
          setElevatorState();
        }}
        onSubmit={handleEditState}
        elevator={selectedElevator}
        state={elevatorState}
        setState={setElevatorState}
      />
      <DeleteElevatorModal
        open={isDeleteElevatorModalOpen}
        close={() => {
          setIsDeleteElevatorModalOpen(false);
          setDeleteElevatorForm({adminPass: ""});
          setSelectedElevator();
        }}
        onDelete={handleDeleteElevator}
        elevator={selectedElevator}
        deleteElevatorForm={deleteElevatorForm}
        setDeleteElevatorForm={setDeleteElevatorForm}
      />
      {/* <CreateAdvertisementModal
        open={isCreateAdvertisementModalOpen}
        close={() => {
          setIsCreateAdvertisementModalOpen(false);
          setCreateAdvertisementForm({
            advertisementTitle: "",
            advertisementContent: "",
            advertisementStartDate: "",
            advertisementEndDate: "",
          });
        }}
        onSubmit={handleCreateAdvertisement}
        createAdvertisementForm={createAdvertisementForm}
        setCreateAdvertisementForm={setCreateAdvertisementForm}
      /> */}
    </div>
  );
}

export default ElevatorPage;
