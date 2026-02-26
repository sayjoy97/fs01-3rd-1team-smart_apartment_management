import React, { useEffect, useState } from "react";
import "../../App.css";
import "./HouseholdManagement.css";
import { houseHoFilter } from "./houseHoFilter";
import { Replace, SearchCheckIcon } from "lucide-react";
import { hoouseManagement, houseAllList, houseDetail } from "../../api/houseAPI";
import HouseholdDetailModal from "./modal/HouseholdDetailModal";
import useMqtt from "../../hook/useMqtt";

// 아파트 동 매핑
const houseDongOptions = [
  { label: "전체", value: "" },
  { label: "101동", value: 101 },
  { label: "102동", value: 102 },
  { label: "103동", value: 103 },
  { label: "104동", value: 104 },
];

// 아파트 호실 매핑
const houseHoOptions = houseHoFilter(4, 2);

const HouseholdMangement = () => {
  // 전체 페이지
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  // 필터
  const [filterHouseHo, setFilterHouseHo] = useState("");
  const [filterHouseDong, setFilterHouseDong] = useState("");

  // 검색
  const [keyword, setKeyword] = useState("");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [houseList, setHouseList] = useState([]);

  // 초기화
  const [clearData, setClearData] = useState("");

  // 세대 데이터 저장
  const [detailData, setDetailData] = useState(null);

  // 모달
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedHouse, setSelectedHouse] = useState(null);

  // 새로고침
  const [reload, setReload] = useState(0);

  // 에러 표시
  const [errorMsg, setErrorMsg] = useState("");

  const { connectStatus, rfidUid, publish } = useMqtt("ws://localhost:9001");

  const handleHouseHoChange = (e) => {
    setFilterHouseHo(e.target.value);
    setCurrentPage(1);
  };

  const handleHouseDongChange = (e) => {
    setFilterHouseDong(e.target.value);
    setCurrentPage(1);
  };

  useEffect(() => {
    houseAllList({
      houseDong: filterHouseDong,
      houseHo: filterHouseHo,
      householderName: searchKeyword,
    })
      .then((res) => {
        console.log("응답: ", res);
        setHouseList(res.data);
      })
      .catch((err) => console.log("세대 정보 조회중 오류 발생", err));
  }, [filterHouseDong, filterHouseHo, searchKeyword, reload]);

  const list = houseList || [];

  // 총 페이지 수 계산
  const totalPages = Math.ceil(houseList.length / itemsPerPage);

  // 현재 페이지 시작
  const startIndex = (currentPage - 1) * itemsPerPage;

  // 현재 페이지 데이터
  const currentItems = list.slice(startIndex, startIndex + itemsPerPage);

  // 제어 함수
  const controlHouseDevice = (device, command) => {
    if (connectStatus !== "connected") return;

    const topic = `jjld/house/000/${device}/control`;
    publish(topic, command);
  };
  // 모달 열기
  const openDetailModal = async (houseId) => {
    try {
      const res = await houseDetail(houseId);
      setErrorMsg("");
      setSelectedHouse(res.data.data);
      setIsModalOpen(true);

      controlHouseDevice("card", "start");
    } catch (e) {
      console.log("세대 상세조회 실패", e);
    }
  };

  // 모달 닫기
  const closeModal = () => {
    controlHouseDevice("card", "stop");

    setIsModalOpen(false);
    setSelectedHouse(null);
  };

  // 저장
  const handleSave = async (houseId, formData) => {
    try {
      setErrorMsg("");
      await hoouseManagement(houseId, formData);
      setReload((r) => r + 1);
      setIsModalOpen(false);
    } catch (e) {
      setErrorMsg(e.message);
    }
  };

  console.log("값", list);

  const falseCnt = list ? list.filter((item) => item.houseStatus === false).length : 0;
  const trueCnt = list ? list.filter((item) => item.houseStatus === true).length : 0;

  return (
    <>
      <div className="component">
        <div className="title">
          <h2>세대 목록</h2>
          <p className="info">
            총 세대 (거주중: {trueCnt} | 공실: {falseCnt})
          </p>
        </div>
        <div className="filter">
          <div className="check">
            <p style={{ marginBottom: "10px" }}>동</p>
            <select
              value={filterHouseDong}
              onChange={handleHouseDongChange}
              style={{ width: "95%", backgroundColor: "var(--background)" }}
            >
              {houseDongOptions.map((ho) => (
                <option key={ho.value} value={ho.value}>
                  {ho.label}
                </option>
              ))}
            </select>
          </div>

          <div className="check">
            <p style={{ marginBottom: "10px" }}>호실</p>
            <select
              value={filterHouseHo}
              onChange={handleHouseHoChange}
              style={{ width: "95%", backgroundColor: "var(--background)" }}
            >
              <option value="">전체</option>
              {houseHoOptions.map((o) => (
                <option key={o.value} value={o.value}>
                  {o.label}
                </option>
              ))}
            </select>
          </div>
          <div className="check">
            <p>세대주</p>
            <div className="search">
              <input
                type="text"
                placeholder="세대주 검색"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                style={{ width: "95%", backgroundColor: "var(--background)" }}
              />
              <button onClick={() => setSearchKeyword(keyword)}>
                <SearchCheckIcon />
              </button>
            </div>
          </div>
          <div className="check">
            <button
              onClick={() => {
                setCurrentPage(1);
                setFilterHouseDong("");
                setFilterHouseHo("");
                setSearchKeyword("");
                setKeyword("");
              }}
              className="clear"
            >
              초기화
            </button>
          </div>
        </div>
        <div className="table-wrapper">
          <div className="table-scroll">
            <table className="complaint-table">
              <thead>
                <tr>
                  <th>동호수</th>
                  <th>(대표)세대주 이름</th>
                  <th>연락처</th>
                  <th>입주일</th>
                  <th style={{ width: "100px" }}>상태</th>
                  <th>관리</th>
                </tr>
              </thead>
              <tbody>
                {currentItems.length === 0 ? (
                  <tr>
                    <td colSpan={6} style={{ textAlign: "center", height: "300px" }}>
                      데이터가 없습니다
                    </td>
                  </tr>
                ) : (
                  <>
                    {currentItems.map((h) => (
                      <tr key={h.houseId}>
                        <td>
                          {h.houseDong}동 {h.houseHo}호
                        </td>
                        <td>{h.householderName}</td>
                        <td>{h.householderPhone?.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3")}</td>
                        <td>{h.moveInAt}</td>
                        <td>
                          <span
                            className={
                              h.houseStatus ? "status-badge occupied" : "status-badge empty"
                            }
                          >
                            {h.houseStatus ? "거주중" : "공실"}
                          </span>
                        </td>
                        <td>
                          <button onClick={() => openDetailModal(h.houseId)}>관리</button>
                        </td>
                      </tr>
                    ))}

                    {/* 부족한 행 채우기 */}
                    {Array.from({ length: Math.max(0, 10 - currentItems.length) }).map((_, i) => (
                      <tr key={`empty-${i}`}>
                        <td>&nbsp;</td>
                        <td></td>
                        <td></td>
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
            <button onClick={() => setCurrentPage((p) => p - 1)} disabled={currentPage === 1}>
              ◀
            </button>

            {(() => {
              const pageSize = 5;
              const total = totalPages;

              const currentGroup = Math.floor((currentPage - 1) / pageSize);

              const start = currentGroup * pageSize + 1;
              const end = Math.min(start + pageSize - 1, total);

              return Array.from({ length: end - start + 1 }, (_, i) => {
                const pageNumber = start + i;

                return (
                  <button
                    key={pageNumber}
                    onClick={() => setCurrentPage(pageNumber)}
                    disabled={currentPage === pageNumber}
                  >
                    {pageNumber}
                  </button>
                );
              });
            })()}

            <button
              onClick={() => setCurrentPage((p) => p + 1)}
              disabled={currentPage === totalPages}
            >
              ▶
            </button>
          </div>
        </div>
        {/* 세대 관리 모달 */}
        {isModalOpen && (
          <HouseholdDetailModal
            data={selectedHouse}
            onSave={handleSave}
            onClose={closeModal}
            rfidUid={rfidUid}
            errorMsg={errorMsg}
          />
        )}
      </div>
    </>
  );
};

export default HouseholdMangement;
