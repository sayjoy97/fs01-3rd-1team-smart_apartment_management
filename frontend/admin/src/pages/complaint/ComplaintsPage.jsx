import React, { useEffect, useState } from "react";
import { complaintAllList, detailView } from "../../api/complaintAPI";
import "./ComplaintsPage.css";
import "../../App.css";
import ComplaintDetailModal from "./modal/ComplaintDetailModal";
import { CATEGORY_LABEL } from "./complaintCategory";

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
  const [pageData, setPageData] = useState(null);
  // 필터
  const [filterCategory, setFilterCategory] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  // 전체 페이지
  const [currentPage, setCurrentPage] = useState(0);
  const itemsPerPage = 10;
  // 탭 토글
  const [activeTab, setActiveTab] = useState("complaints");

  // 모달
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedComplaint, setSelectedComplaint] = useState(null);

  // 카테고리 변경 시 페이지 1로 초기화
  const handleCategoryChange = (e) => {
    setFilterCategory(e.target.value);
    setCurrentPage(0);
  };

  // 민원 상태 변경
  const handleStatusChange = (e) => {
    setFilterStatus(e.target.value);
    setCurrentPage(0);
  };

  // 페이지 변경
  const goToPage = (page) => {
    if (!pageData) return;
    if (page >= 0 && page <= pageData.totalPages) {
      setCurrentPage(page);
    }
  };

  // API 호출
  useEffect(() => {
    // 민원 목록 조회
    complaintAllList({
      category: filterCategory,
      status: filterStatus,
      page: currentPage,
      size: itemsPerPage,
    })
      .then((res) => setPageData(res))
      .catch((err) => console.log("민원 목록 조회 실패: ", err));
  }, [filterCategory, filterStatus, currentPage]);

  const list = pageData?.content || [];

  const openDetailModal = async (complaintId) => {
    try {
      const res = await detailView(complaintId);

      setSelectedComplaint(res.data.data);
      setIsModalOpen(true);
    } catch (e) {
      console.log("상세조회 실패", e);
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedComplaint(null);
  };

  console.log("민원 목록: ", pageData);

  return (
    <>
      {/* 탭 버튼 */}
      <div className="complaint-toggle">
        <button onClick={() => setActiveTab("complaints")}>민원 목록</button>
        <button onClick={() => setActiveTab("ai")}>AI 요약</button>
      </div>

      {/* 본문 */}
      <div className="component">
        <div className="sub">
          <h2>{activeTab === "complaints" ? "민원 목록" : "AI요약"}</h2>
          <p className="info">
            {activeTab === "complaints"
              ? "모든 입주민 민원을 조회합니다"
              : "지정한 기간의 민원 데이터를 AI가 자동 분석합니다."}
          </p>
        </div>

        {activeTab === "complaints" && (
          <>
            {/* 필터 영역 */}
            <div className="filter">
              <div className="check">
                <p style={{ marginBottom: "10px" }}>카테고리</p>
                <select
                  value={filterCategory}
                  onChange={handleCategoryChange}
                  style={{ width: "95%", backgroundColor: "white" }}
                >
                  {categoryOptions.map((o) => (
                    <option key={o.value} value={o.value}>
                      {o.label}
                    </option>
                  ))}
                </select>
              </div>

              <div className="check">
                <p style={{ marginBottom: "10px" }}>상태</p>
                <select
                  value={filterStatus}
                  onChange={handleStatusChange}
                  style={{ width: "95%", backgroundColor: "white" }}
                >
                  {statusOptions.map((o) => (
                    <option key={o.value} value={o.value}>
                      {o.label}
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
                      <th>번호</th>
                      <th>제목</th>
                      <th>카테고리</th>
                      <th>위치</th>
                      <th>접수일</th>
                      <th>상태</th>
                      <th>작업</th>
                    </tr>
                  </thead>

                  <tbody>
                    {list.length === 0 ? (
                      <tr>
                        <td colSpan={7} style={{ textAlign: "center", height: "300px" }}>
                          데이터가 없습니다
                        </td>
                      </tr>
                    ) : (
                      list.map((c) => (
                        <tr key={c.complaintId}>
                          <td>{c.complaintId}</td>
                          <td>{c.title}</td>
                          <td>{CATEGORY_LABEL[c.category] || c.category}</td>
                          <td>
                            {c.houseDong}동 {c.houseHo}호
                          </td>
                          <td>{new Date(c.createAt).toLocaleDateString()}</td>
                          <td>
                            {c.status === "ANSWERED" ? (
                              <p
                                style={{
                                  backgroundColor: "#5a8cb9",
                                  color: "white",
                                  textAlign: "center",
                                }}
                              >
                                답변 완료
                              </p>
                            ) : (
                              <p
                                style={{
                                  backgroundColor: "#c35f5f",
                                  color: "white",
                                  textAlign: "center",
                                }}
                              >
                                대기중
                              </p>
                            )}
                          </td>
                          <td>
                            <button onClick={() => openDetailModal(c.complaintId)}>상세보기</button>
                          </td>
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
          </>
        )}
        {/* 상세보기 */}
        {isModalOpen && <ComplaintDetailModal data={selectedComplaint} onClose={closeModal} />}
      </div>
    </>
  );
};

export default ComplaintsPage;
