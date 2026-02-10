import React, { useEffect, useState } from "react";
import "../../App.css";
import "./HouseholdManagement.css";
import { houseHoFilter } from "./houseHoFilter";
import { SearchCheckIcon } from "lucide-react";
import { houseAllList } from "../../api/houseAPI";

// 아파트 동 매핑
const houseDongOptions = [
  { label: "전체", value: "" },
  { label: "101동", value: 101 },
  { label: "201동", value: 201 },
  { label: "301동", value: 301 },
  { label: "401동", value: 401 },
];

// 아파트 호실 매핑
const houseHoOptions = houseHoFilter(4, 2);

const HouseholdMangement = () => {
  // 전체 페이지
  const [currentPage, setCurrentPage] = useState(0);

  // 필터
  const [filterHouseHo, setFilterHouseHo] = useState("");
  const [filterHouseDong, setFilterHouseDong] = useState("");

  // 검색
  const [keyword, setKeyword] = useState("");
  const [searchKeyword, setSearchKeyword] = useState("");

  // 초기화
  const [clearData, setClearData] = useState("");
  const handleHouseHoChange = (e) => {
    setFilterHouseHo(e.target.value);
    setCurrentPage(0);
  };

  const handleHouseDongChange = (e) => {
    setFilterHouseDong(e.target.value);
    setCurrentPage(0);
  };

  useEffect(() => {
    houseAllList({
      houseDong: filterHouseDong,
      houseHo: filterHouseHo,
      houseHolderName: searchKeyword,
    })
      .then((res) => {
        console.log("응답: ", res);
        houseAllList(res);
      })
      .catch((err) => console.log("세대 정보 조회중 오류 발생", err));
  }, [filterHouseDong, filterHouseHo, searchKeyword]);

  console.log("세대 정보: ");

  return (
    <>
      <div className="component">
        <div className="title">
          <h2>세대 목록</h2>
          <p className="info">총 세대 (거주중: , 공실: )</p>
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
            <p style={{ marginBottom: "10px" }}>세대주</p>
            <div className="search">
              <input
                type="text"
                placeholder="세대주 검색"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                style={{ width: "95%", backgroundColor: "var(--background)" }}
              />
              <button onClick={() => setKeyword(searchKeyword)}>
                <SearchCheckIcon />
              </button>
            </div>
          </div>
          <div className="check">
            <button onClick={() => setClearData} className="clear">
              초기화
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default HouseholdMangement;
