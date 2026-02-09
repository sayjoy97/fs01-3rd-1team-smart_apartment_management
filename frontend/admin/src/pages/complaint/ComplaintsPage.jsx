import React, { useEffect, useState } from "react";
import { complaintAllList } from "../../api/complaintAPI";
import "./ComplaintsPage.css";
import "../../App.css";

// 카테고리 매핑
const categoryOptions = [
  { label: "전체", value: "" },
  { label: "엘리베이터", value: "ELEVATOR" },
  { label: "정원", value: "GARDEN" },
  { label: "소음", value: "NOISE" },
  { label: "주차", value: "PARKING" },
  { label: "기타", value: "OTHER" },
];

// 민원 상태 매핑
const statusOptions = [
  { label: "전체", value: "" },
  { label: "답변완료", value: "ANSWERED" },
  { label: "대기중", value: "WAITING" },
];

const ComplaintsPage = () => {
  const [complaintList, getComplaintList] = useState([]);
  const [showComplaintModal, setShowComplaintModal] = useState(false);
  const [filterCategory, setFilterCategory] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const [activeTab, setActiveTab] = useState("complaints");

  // 카테고리 변경 시 페이지 1로 초기화
  const handleCategoryChange = (e) => {
    setFilterCategory(e.target.value);
    setCurrentPage(1);
  };

  // 민원 상태 변경
  const handleStatusChange = (e) => {
    setFilterStatus(e.target.value);
    setCurrentPage(1);
  };

  // 페이지 변경
  const goToPage = (page) => {
    if (page >= 1 && page <= currentPage) setCurrentPage(page);
  };

  // API 호출
  useEffect(() => {
    // 민원 목록 조회
    complaintAllList({
      category: filterCategory,
      status: filterStatus,
      page: currentPage - 1,
      size: itemsPerPage,
    })
      .then((res) => getComplaintList(res.content || []))
      .catch((err) => console.log("민원 목록 조회 실패: ", err));

    // 민원 상세 모달 열기
    const openDetailModal = (complaint) => {
      setShowComplaintModal({ complaint });
    };
  }, [filterCategory, filterStatus, currentPage]);

  console.log("민원 목록: ", complaintList);

  return (
    <>
      <div className="complaint-toggle">
        <button
          onClick={() => setActiveTab("complaints")}
          style={{
            background: activeTab === "complaints" ? "#007bff" : "#e0e0e0",
            color: activeTab === "complaints" ? "#fff" : "#000",
          }}
        >
          민원 목록
        </button>
        <button
          onClick={() => setActiveTab("ai")}
          style={{
            background: activeTab === "ai" ? "#17a2b8" : "#e0e0e0",
            color: activeTab === "ai" ? "#fff" : "#000",
          }}
        >
          AI 요약
        </button>
      </div>

      {/* 민원 목록 */}
      <div className="component">
        <h2 className="sub-title">민원 목록</h2>
        <p className="info">모든 입주민 민원을 조회하고 관리합니다</p>
        {activeTab === "complaints" && (
          <>
            <div className="filter">
              <div className="category">
                <p>카테고리</p>
                <select value={filterCategory} onChange={handleCategoryChange}>
                  {categoryOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </div>
              <div className="status">
                <p>상태</p>
                <select value={filterStatus} onChange={handleStatusChange}>
                  {statusOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* 민원 목록 */}
            <ul>
              {complaintList.length === 0 ? (
                <li>데이터가 없습니다.</li>
              ) : (
                complaintList.map((c) => <li key={c.complaintId}>{c.title}</li>)
              )}
            </ul>
          </>
        )}
      </div>
    </>
  );
};

export default ComplaintsPage;
