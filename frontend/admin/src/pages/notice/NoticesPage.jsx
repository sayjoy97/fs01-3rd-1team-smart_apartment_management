import { use, useEffect, useState } from "react";

import "../../App.css";
import "./Notices.css";
import { fixedNoticeList, noticeAllList, searchNocticeList } from "./../../api/noticeAPI";
import { useNavigate } from "react-router-dom";

export function NoticesPage() {
  const [currentPage, setCurrentPage] = useState(1); // 페이지 상태
  const [noticeList, setNoticeList] = useState([]); // 전체 공지사항 리스트
  const [fixedList, setFixedNoticeList] = useState([]); // 고정 공지사항 리스트
  const [totalPages, setTotalPages] = useState(1); // 전체 페이지 수
  const [searchType, setSearchType] = useState(""); // 검색 유형
  const [keyword, setKeyword] = useState(""); // 검색어

  const navigate = useNavigate();

  const noticeDetail = (notice_id) => {
    navigate(`/notices/${notice_id}`);
  };

  // 페이지 이동
  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  // 페이지 번호 리스트 생성
  const getPageNumbers = () => {
    const pages = [];
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i);
    }
    return pages;
  };

  const handleSearch = () => {
    setCurrentPage(1); // 검색하면 항상 1페이지부터
    fetchNoticeList(1);
  };

  const fetchNoticeList = (page) => {
    const size = 10;

    // 검색어 없으면 전체 조회
    if (!keyword.trim()) {
      noticeAllList({ page, size })
        .then((res) => {
          setNoticeList(res.data.content || []);
          setTotalPages(res.data.totalPages || 1);
        })
        .catch(console.error);
      return;
    }

    // 검색 API 호출
    searchNocticeList({
      search_type: convertSearchType(searchType),
      keyword,
      page,
      size,
    })
      .then((res) => {
        setNoticeList(res.data.content || []);
        setTotalPages(res.data.totalPages || 1);
      })
      .catch(console.error);
  };

  const convertSearchType = (type) => {
    switch (type) {
      case "title":
        return "notice_title";
      case "adminName":
        return "admin_name";
      default:
        return "all";
    }
  };

  useEffect(() => {
    fetchNoticeList(currentPage);

    // 고정 게시글 목록 조회
    fixedNoticeList()
      .then((res) => {
        console.log("고정 게시글 목록조회 성공: ", res.data || []);
        setFixedNoticeList(res.data);
      })
      .catch((err) => console.log("고정 게시글 조회 실패: ", err));
  }, [currentPage]);

  return (
    <div className="notice-page">
      {/* 공지사항 등록 버튼 */}
      <div className="notice-page-header">
        <button className="notice-btn notice-btn-primary" onClick={() => navigate("/notice/write")}>
          <span className="notice-icon">+</span>
          공지사항 등록
        </button>
      </div>

      <div className="notice-card">
        <div className="notice-card-header">
          <h3 className="notice-card-title">공지사항 목록</h3>
        </div>

        <div className="notice-card-content">
          {/* 검색 영역 */}
          <div className="notice-search-row">
            <select
              className="notice-select"
              value={searchType}
              onChange={(e) => setSearchType(e.target.value)}
            >
              <option value="all">전체</option>
              <option value="title">제목</option>
              <option value="adminName">작성자</option>
            </select>

            <div className="notice-search-input-wrap">
              <input
                type="text"
                placeholder="검색어를 입력하세요..."
                className="notice-input"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
              />
            </div>

            <button className="notice-btn notice-btn-blue" onClick={handleSearch}>
              검색
            </button>
          </div>

          {/* 통합 테이블 */}
          <div className="notice-table-wrap">
            <table className="notice-table">
              <thead>
                <tr>
                  <th>제목</th>
                  <th>작성자</th>
                  <th>작성날짜</th>
                </tr>
              </thead>

              <tbody>
                {/* 고정 공지 먼저 */}
                {fixedList.map((notice) => (
                  <tr
                    key={notice.noticeId}
                    className="notice-row-fixed"
                    onClick={() => noticeDetail(notice.noticeId)}
                    style={{ cursor: "pointer" }}
                  >
                    <td className="notice-title">📌 {notice.noticeTitle}</td>
                    <td>{notice.adminName}</td>
                    <td>{notice.createdAt?.replace("T", " ")}</td>
                  </tr>
                ))}

                {/* 일반 공지 */}
                {noticeList.length === 0 ? (
                  <tr>
                    <td colSpan={3} className="notice-empty">
                      등록된 공지사항이 없습니다.
                    </td>
                  </tr>
                ) : (
                  noticeList.map((notice) => (
                    <tr
                      key={notice.noticeId}
                      className="notice-row"
                      onClick={() => noticeDetail(notice.noticeId)}
                      style={{ cursor: "pointer" }}
                    >
                      <td className="notice-title">{notice.noticeTitle}</td>
                      <td>{notice.adminName}</td>
                      <td>{notice.createdAt?.replace("T", " ")}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
          {/* 페이지네이션 */}
          {totalPages > 1 && (
            <div className="notice-pagination">
              {/* 이전 버튼 */}
              <button
                className="notice-page-btn"
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 1}
              >
                ‹
              </button>

              {/* 페이지 숫자 */}
              {getPageNumbers().map((page) => (
                <button
                  key={page}
                  onClick={() => handlePageChange(page)}
                  className={`notice-page-btn ${currentPage === page ? "active" : ""}`}
                >
                  {page}
                </button>
              ))}

              {/* 다음 버튼 */}
              <button
                className="notice-page-btn"
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages}
              >
                ›
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
