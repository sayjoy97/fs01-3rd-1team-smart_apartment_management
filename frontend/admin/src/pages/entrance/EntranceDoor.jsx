import React, { useEffect, useState } from "react";
import { changeStatusDoor, getEntranceDoor, getEntranceLog } from "../../api/entranceDoorAPI";
import "../../App.css";
import EntranceControlModal from "./modal/EntranceControlModal";
import useMqtt from "../../hook/useMqtt";
import "./EntranceDoor.css";
import { FiCheckCircle, FiXCircle } from "react-icons/fi";

// 공동현관 매핑
const entraceDoorOptions = [
  { label: "전체", value: "" },
  { label: "101동", value: 101 },
  { label: "102동", value: 102 },
  { label: "103동", value: 103 },
  { label: "104동", value: 104 },
];

// 출입 타입 매핑
const accessTypeOptions = [
  { label: "전체", value: "" },
  { label: "입주민 카드", value: "RESIDENT_CARD" },
  { label: "공동현관 비밀번호", value: "RESIDENT_PASSWORD" },
  { label: "세대 호출", value: "HOUSE_CALL" },
  { label: "관리자 호출", value: "ADMIN_CALL" },
  { label: "관리자 원격 제어", value: "REMOTE_CONTROL" },
];

// 출입 실패 유무 매핑
const failReasonTypeMap = {
  NONE: "정상 출입",
  WRONG_PASSWORD: "잘못된 비밀번호",
  INVALID_CARD: "유효하지 않은 카드",
  LOST_CARD: "분실 카드",
  EXPIRED_CARD: "만료된 카드",
  SYSTEM_ERROR: "시스템 에러",
  NOT_EXIST_HOUSE: "잘못된 세대 호수 입력",
};

const EntranceDoor = () => {
  const [pageData, setPageData] = useState(null);

  // 공동 현관 목록 상태
  const [entrances, setEntrances] = useState([]);

  // 출입 기록 상태
  const [logs, setLogs] = useState("");

  // 전체 페이지
  const [currentPage, setCurrentPage] = useState(0);
  const itemsPerPage = 10;

  // 필터
  const [filterHouseDong, setFilterHouseDong] = useState("");
  const [filterAccessType, setFilterAccessType] = useState("");

  const [doorList, setDoorList] = useState([]);

  const [logList, setLogList] = useState([]);

  const { connectStatus, imageSrc, setImageState, publish } = useMqtt("ws://localhost:9001");
  // 필터 조회 시 페이지 1로 초기화
  const handleHouseDongChange = (e) => {
    setFilterHouseDong(e.target.value);
    setCurrentPage(0);
  };

  const handleAccessTypeChange = (e) => {
    setFilterAccessType(e.target.value);
    setCurrentPage(0);
  };

  // 페이지 변경
  const goToPage = (page) => {
    if (!pageData) return;
    if (page >= 0 && page <= pageData.totalPages) {
      setCurrentPage(page);
    }
  };

  const list = pageData?.content || [];

  // 모달
  const [modalEntrance, setModalEntrance] = useState(null);

  useEffect(() => {
    getEntranceDoor()
      .then((res) => {
        console.log("공동현관 목록 응답: ", res);
        setDoorList(res.data);
      })
      .catch((err) => console.error("공동현관 목록 조회 실패: ", err));
  }, []);

  useEffect(() => {
    getEntranceLog({
      houseDong: filterHouseDong,
      accessType: filterAccessType,
      page: currentPage,
      size: itemsPerPage,
    })
      .then((res) => {
        console.log("출입기록 응답: ", res);
        setPageData(res);
      })
      .catch((err) => console.log("공동현관 출입 기록 조회중 오류 발생", err));
  }, [filterHouseDong, filterAccessType, currentPage]);

  const controlDevice = (dong, device, command) => {
    if (connectStatus !== "connected") return;

    const topic = `jjld/entrance/${dong}/${device}/control`;
    publish(topic, command);
  };

  // 모달 열기
  const openModal = (entrance) => {
    setImageState("");
    setModalEntrance(entrance);
    controlDevice(entrance.houseDong, "cam", "start");
  };

  // 모달 닫기
  const closeModal = () => {
    if (modalEntrance) {
      console.log(modalEntrance);
      controlDevice(modalEntrance.houseDong, "cam", "stop");
    }

    setImageState("");
    setModalEntrance(null);
  };

  const handleDoorStatusChange = async (doorId, newStatus) => {
    try {
      await changeStatusDoor(doorId, newStatus);

      const [doorRes, logRes] = await Promise.all([
        getEntranceDoor(),
        getEntranceLog({
          houseDong: filterHouseDong,
          accessType: filterAccessType,
          page: currentPage,
          size: itemsPerPage,
        }),
      ]);

      setDoorList(doorRes.data || []);

      setPageData({ ...logRes });

      const doorArray = doorRes.data || [];
      setModalEntrance((prev) => {
        if (!prev) return null;
        const updated = doorArray.find((e) => String(e.doorId) === String(doorId));
        return updated ? { ...prev, ...updated } : prev;
      });

      alert("원격 제어를 성공했습니다.");
    } catch (err) {
      console.error("제어 에러:", err);
    }
  };

  return (
    <>
      <div className="card-grid">
        {doorList.map((e) => (
          <div key={e.doorId} className="component">
            <div className="row">
              <div className="card-title">{e.houseDong} 동</div>
              <div className={e.status === "CLOSED" ? "badge lock" : "badge open"}>
                {e.status === "CLOSED" ? "잠김" : "열림"}
              </div>
            </div>
            <div className="card-body">
              최근 open: {e.lastAccessTime ? new Date(e.lastAccessTime).toLocaleString() : "-"}
            </div>

            <button className="control-btn" onClick={() => openModal(e)}>
              원격 제어
            </button>
          </div>
        ))}
        {/* 출입 기록 */}
        <div className="component">
          <h2>출입 기록</h2>
          {/* 필터 */}
          <div className="filter">
            <div className="check">
              <p style={{ marginBottom: "10px" }}>동 선택</p>
              <select
                value={filterHouseDong}
                onChange={handleHouseDongChange}
                style={{ width: "95%", backgroundColor: "var(--background)" }}
              >
                {entraceDoorOptions.map((door) => (
                  <option key={door.value} value={door.value}>
                    {door.label}
                  </option>
                ))}
              </select>
            </div>
            <div className="check">
              <p style={{ marginBottom: "10px" }}>출입 유형</p>
              <select
                value={filterAccessType}
                onChange={handleAccessTypeChange}
                style={{ width: "95%", backgroundColor: "var(--background)" }}
              >
                {accessTypeOptions.map((a) => (
                  <option key={a.value} value={a.value}>
                    {a.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="table-wrapper">
            <div className="table-scroll">
              <table className="complaint-table">
                <thead>
                  <tr>
                    <th>출입 시간</th>
                    <th>동 / 호수</th>
                    <th>출입 유형</th>
                    <th>상태</th>
                  </tr>
                </thead>
                <tbody>
                  {list.length === 0 ? (
                    <tr>
                      <td colSpan={4} style={{ textAlign: "center", height: "300px" }}>
                        데이터가 없습니다
                      </td>
                    </tr>
                  ) : (
                    <>
                      {list.map((l) => (
                        <tr key={l.accessLogId}>
                          <td>{new Date(l.accessedAt).toLocaleString()}</td>
                          <td>
                            {l.houseDong}동 {!l.houseHo ? "" : `${l.houseHo} 호실`}
                          </td>
                          <td>
                            {accessTypeOptions.find((o) => o.value === l.accessType)?.label || "-"}
                          </td>
                          <td
                            className={
                              l.failReason && l.failReason !== "NONE"
                                ? "fail-reason error"
                                : "fail-reason"
                            }
                          >
                            {l.failReason === "NONE" ? (
                              <div className="row-icon">
                                <FiCheckCircle className="icon" />
                                {failReasonTypeMap[l.failReason]}
                              </div>
                            ) : (
                              <div className="row-icon">
                                <FiXCircle className="icon" />
                                {failReasonTypeMap[l.failReason] || "-"}
                              </div>
                            )}
                          </td>
                        </tr>
                      ))}

                      {/* 부족한 행 */}
                      {Array.from({ length: 10 - list.length }).map((_, index) => (
                        <tr key={`empty-${index}`}>
                          <td>&nbsp;</td>
                          <td></td>
                          <td></td>
                          <td></td>
                        </tr>
                      ))}
                    </>
                  )}
                </tbody>
              </table>
            </div>
            {/* 페이지네이션 */}
            <div className="pagination">
              <button disabled={currentPage === 0} onClick={() => goToPage(currentPage - 1)}>
                ◀
              </button>

              {(() => {
                const total = pageData?.totalPages || 0;
                const pageSize = 5; // 고정 개수

                const currentGroup = Math.floor(currentPage / pageSize);

                const start = currentGroup * pageSize;
                const end = Math.min(start + pageSize, total);

                return Array.from({ length: end - start }, (_, i) => {
                  const pageIndex = start + i;

                  return (
                    <button
                      key={pageIndex}
                      onClick={() => goToPage(pageIndex)}
                      className={currentPage === pageIndex ? "active" : ""}
                    >
                      {pageIndex + 1}
                    </button>
                  );
                });
              })()}

              <button
                disabled={currentPage === (pageData?.totalPages || 1) - 1}
                onClick={() => goToPage(currentPage + 1)}
              >
                ▶
              </button>
            </div>
          </div>
        </div>

        {modalEntrance && (
          <EntranceControlModal
            entrance={modalEntrance}
            imageSrc={imageSrc}
            publish={publish}
            onConfirm={handleDoorStatusChange}
            onClose={closeModal}
          />
        )}
      </div>
    </>
  );
};

export default EntranceDoor;
