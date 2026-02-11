import React, { useEffect, useState } from "react";
import { getEntranceDoor, getEntranceLog } from "../../api/entranceDoorAPI";
import "../../App.css";
import EntranceControlModal from "./modal/EntranceControlModal";

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
];

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
  const doorControl = (doorId) => {
    setEntrances((prev) =>
      prev.map((e) =>
        e.doorId === doorId ? { ...e, status: e.status === "CLOSED" ? "OPENED" : "CLOSED" } : e,
      ),
    );
  };

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

  return (
    <>
      <div className="card-grid">
        {doorList.map((e) => (
          <div key={e.doorId} className="component">
            <div className="card-title">{e.houseDong} 동</div>
            <div className="card-body">최근 출입 시간: {e.lastAccessTime}</div>
            <div className={e.status === "CLOSED" ? "badege lock" : "badege open"}>
              {e.status === "CLOSED" ? "잠김" : "열림"}
            </div>
            <button className="control-btn" onClick={() => setModalEntrance(e)}>
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

          {/* 테이블 */}
          <div className="table-wrapper">
            <div className="table-scroll">
              <table className="complaint-table">
                <thead>
                  <tr>
                    <th>출입 시간</th>
                    <th>동</th>
                    <th>호수</th>
                    <th>출입 유형</th>
                    <th>상태</th>
                  </tr>
                </thead>
                <tbody>
                  {list.length === 0 ? (
                    <tr>
                      <td colSpan={5} style={{ textAlign: "center", height: "300px" }}>
                        데이터가 없습니다
                      </td>
                    </tr>
                  ) : (
                    list.map((l) => (
                      <tr key={l.accessLogId}>
                        <td>{l.accessedAt}</td>
                        <td>
                          {l.houseDong}동 {l.houseHo}호실
                        </td>
                        <td>{l.accessType}</td>
                        <td>{l.status}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
            {/* 페이지네이션 */}
            <div className="pagination">
              <button disabled={currentPage === 0} onClick={() => goToPage(currentPage - 1)}>
                ◀
              </button>

              {Array.from({ length: pageData?.totalPages || 0 }, (_, i) => (
                <button
                  key={i}
                  onClick={() => goToPage(i)}
                  className={currentPage === i ? "active" : ""}
                >
                  {i + 1}
                </button>
              ))}

              <button
                disabled={currentPage === (pageData?.totalPages || 1) - 1}
                onClick={() => goToPage(currentPage + 1)}
              >
                ▶
              </button>
            </div>
          </div>
        </div>

        <EntranceControlModal
          entrance={modalEntrance}
          onConfirm={(doorId) => {
            doorControl(doorId);
            setModalEntrance(null);
          }}
          onClose={() => setModalEntrance(null)}
        />
      </div>
    </>
  );
};

export default EntranceDoor;
