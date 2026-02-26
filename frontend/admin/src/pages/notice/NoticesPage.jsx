import { useEffect, useState, useCallback } from "react";
import "./NoticePage.css";
import { fixedNoticeList, noticeAllList, searchNocticeList } from "./../../api/noticeAPI";
import { useNavigate } from "react-router-dom";

export function NoticesPage() {
  const [currentPage, setCurrentPage] = useState(1);
  const [noticeList, setNoticeList] = useState([]);
  const [fixedList, setFixedNoticeList] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [searchType, setSearchType] = useState("all");
  const [keyword, setKeyword] = useState("");

  const navigate = useNavigate();
  const PAGE_SIZE = 10; // 게시글 고정 개수
  const BLOCK_SIZE = 5; // 페이지 번호 표시 개수

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

  const fetchNoticeList = useCallback(
    (page, currentKeyword) => {
      const targetKeyword = currentKeyword !== undefined ? currentKeyword : keyword;
      const requestPage = page;

      const handleResponse = (res) => {
        // API 응답 구조에 따른 방어적 코드
        const resultData = res?.data?.data || res?.data;
        if (resultData) {
          setNoticeList(resultData.content || []);
          setTotalPages(resultData.totalPages || 1);
        }
      };

      if (!targetKeyword.trim()) {
        noticeAllList({ page: requestPage, size: PAGE_SIZE })
          .then(handleResponse)
          .catch(console.error);
      } else {
        searchNocticeList({
          search_type: convertSearchType(searchType),
          keyword: targetKeyword,
          page: requestPage,
          size: PAGE_SIZE,
        })
          .then(handleResponse)
          .catch(console.error);
      }
    },
    [searchType, keyword],
  );

  const handleSearch = () => {
    setCurrentPage(1);
    fetchNoticeList(1, keyword);
  };

  useEffect(() => {
    fetchNoticeList(currentPage);
  }, [currentPage, fetchNoticeList]);

  useEffect(() => {
    fixedNoticeList()
      .then((res) => setFixedNoticeList(res.data || []))
      .catch((err) => console.error("고정 게시글 조회 실패: ", err));
  }, []);

  // --- 페이지네이션 로직 ---
  const currentBlock = Math.ceil(currentPage / BLOCK_SIZE);
  const startPage = (currentBlock - 1) * BLOCK_SIZE + 1;
  const endPage = Math.min(startPage + BLOCK_SIZE - 1, totalPages);

  // 빈 행 계산 (항상 10개 높이 유지)
  const emptyRows = Math.max(0, PAGE_SIZE - noticeList.length);

  return (
    <div className="apt-notice-container">
      <div className="apt-notice-header-actions">
        <button className="apt-btn-register" onClick={() => navigate("/notice/write")}>
          <span className="apt-icon">+</span> 공지사항 등록
        </button>
      </div>

      <div className="apt-notice-card">
        <div className="apt-notice-card-header">
          <h2 className="apt-notice-title">공지사항 목록</h2>
        </div>

        <div className="apt-notice-body">
          <div className="apt-search-bar">
            <select
              className="apt-select"
              value={searchType}
              onChange={(e) => setSearchType(e.target.value)}
            >
              <option value="all">전체</option>
              <option value="title">제목</option>
              <option value="adminName">작성자</option>
            </select>
            <div className="apt-input-wrapper">
              <i className="apt-search-icon">🔍</i>
              <input
                type="text"
                className="apt-input"
                placeholder="검색어를 입력하세요..."
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
              />
            </div>
            <button className="apt-btn-search" onClick={handleSearch}>
              검색
            </button>
          </div>

          <div className="apt-table-container">
            <table className="apt-table">
              <thead>
                <tr>
                  <th style={{ width: "60%" }}>제목</th>
                  <th style={{ width: "20%" }}>작성자</th>
                  <th style={{ width: "20%" }}>작성날짜</th>
                </tr>
              </thead>
              <tbody>
                {/* 고정 게시글 */}
                {fixedList.map((notice) => (
                  <tr
                    key={`fixed-${notice.noticeId}`}
                    className="apt-row-fixed"
                    onClick={() => navigate(`/notices/${notice.noticeId}`)}
                  >
                    <td className="apt-td-title">
                      <span className="apt-badge-fixed">고정</span>
                      {notice.noticeTitle}
                    </td>
                    <td className="apt-td-center">{notice.adminName}</td>
                    <td className="apt-td-center">{notice.createdAt?.split("T")[0]}</td>
                  </tr>
                ))}

                {/* 일반 게시글 */}
                {noticeList.map((notice) => (
                  <tr
                    key={notice.noticeId}
                    className="apt-row"
                    onClick={() => navigate(`/notices/${notice.noticeId}`)}
                  >
                    <td className="apt-td-title">{notice.noticeTitle}</td>
                    <td className="apt-td-center">{notice.adminName}</td>
                    <td className="apt-td-center">{notice.createdAt?.split("T")[0]}</td>
                  </tr>
                ))}

                {/* 빈 행 채우기 (높이 고정) */}
                {emptyRows > 0 &&
                  Array.from({ length: emptyRows }).map((_, i) => (
                    <tr key={`empty-${i}`} className="apt-row-empty">
                      <td colSpan={3}>&nbsp;</td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>

          {/* 페이지네이션 (5개씩 블록 이동) */}
          <div className="apt-pagination">
            <button
              className="apt-page-arrow"
              onClick={() => setCurrentPage(1)}
              disabled={currentPage === 1}
            >
              «
            </button>
            <button
              className="apt-page-arrow"
              onClick={() => setCurrentPage(Math.max(1, startPage - 1))}
              disabled={startPage === 1}
            >
              ‹
            </button>

            {Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i).map(
              (page) => (
                <button
                  key={page}
                  onClick={() => setCurrentPage(page)}
                  className={`apt-page-num ${currentPage === page ? "active" : ""}`}
                >
                  {page}
                </button>
              ),
            )}

            <button
              className="apt-page-arrow"
              onClick={() => setCurrentPage(Math.min(totalPages, endPage + 1))}
              disabled={endPage === totalPages}
            >
              ›
            </button>
            <button
              className="apt-page-arrow"
              onClick={() => setCurrentPage(totalPages)}
              disabled={currentPage === totalPages}
            >
              »
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
