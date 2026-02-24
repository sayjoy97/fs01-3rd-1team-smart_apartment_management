import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { noticeDetail, noticeUpdate, noticeDelete, noticeFixedChange } from "../../api/noticeAPI";
import "./NoticeDetail.css";

export function NoticeDetailPage() {
  const { noticeId } = useParams();
  const navigate = useNavigate();

  const [notice, setNotice] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  useEffect(() => {
    noticeDetail(noticeId)
      .then((res) => {
        setNotice(res.data);
        setTitle(res.data.noticeTitle);
        setContent(res.data.noticeContent);
      })
      .catch(console.error);
  }, [noticeId]);

  if (!notice) return <div className="ndp-loading">로딩 중...</div>;

  const handleFixToggle = async () => {
    const action = notice.fixStatus ? "해제" : "고정";
    if (window.confirm(`공지사항을 ${action}하시겠습니까?`)) {
      const res = await noticeFixedChange({ notice_id: notice.noticeId });
      if (res) {
        alert(`공지사항이 ${action}되었습니다.`);
        window.location.reload();
      }
    }
  };

  const handleUpdate = async () => {
    if (window.confirm("수정사항을 저장하시겠습니까?")) {
      const updateData = {
        noticeId: notice.noticeId,
        adminId: 1,
        noticeTitle: title,
        noticeContent: content,
      };
      const res = await noticeUpdate(updateData);
      if (res) {
        alert("수정 완료");
        setEditMode(false);
        window.location.reload();
      }
    }
  };

  const handleDelete = async () => {
    if (window.confirm("정말 삭제하시겠습니까?")) {
      const res = await noticeDelete({ notice_id: notice.noticeId });
      if (res) {
        alert("삭제 완료");
        navigate("/notices");
      }
    }
  };

  return (
    <div className="ndp-wrapper">
      <div className="ndp-card">
        {/* 상단 레이아웃 */}
        <div className="ndp-top-section">
          {/* 1. 왼쪽 핀 */}
          <div className="ndp-pin-wrapper">
            <button
              className={`ndp-pin-button ${notice.fixStatus ? "is-fixed" : ""}`}
              onClick={handleFixToggle}
            >
              <svg
                viewBox="0 0 24 24"
                width="32"
                height="32"
                stroke="currentColor"
                strokeWidth="2"
                fill="none"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M12 2v8" />
                <path d="M5 10h14l-2 7H7l-2-7Z" />
                <path d="M12 17v5" />
              </svg>
            </button>
          </div>

          {/* 2. 중앙 제목 */}
          <div className="ndp-title-wrapper">
            {editMode ? (
              <input
                className="ndp-input-title-center"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
              />
            ) : (
              <h1 className="ndp-title-center">{notice.noticeTitle}</h1>
            )}
          </div>
        </div>

        {/* 3. 오른쪽 수직 정렬 메타 정보 */}
        <div className="ndp-meta-container">
          <div className="ndp-meta-item">작성자: {notice.adminName || "관리사무소"}</div>
          <div className="ndp-meta-item">작성날짜: {notice.createdAt?.split("T")[0]}</div>
          {notice.updatedAt && (
            <div className="ndp-meta-item">수정날짜: {notice.updatedAt?.split("T")[0]}</div>
          )}
        </div>

        <hr className="ndp-divider" />

        {/* 본문 */}
        <div className="ndp-body">
          {editMode ? (
            <textarea
              className="ndp-textarea"
              rows={15}
              value={content}
              onChange={(e) => setContent(e.target.value)}
            />
          ) : (
            <div className="ndp-content-text">{notice.noticeContent}</div>
          )}
        </div>

        {/* 하단 버튼 (목록보기 왼쪽, 수정/삭제 오른쪽) */}
        <div className="ndp-footer">
          <button className="ndp-btn ndp-btn-list" onClick={() => navigate("/notices")}>
            목록보기
          </button>

          <div className="ndp-right-btns">
            {editMode ? (
              <>
                <button className="ndp-btn ndp-btn-outline" onClick={() => setEditMode(false)}>
                  취소
                </button>
                <button className="ndp-btn ndp-btn-primary" onClick={handleUpdate}>
                  저장
                </button>
              </>
            ) : (
              <>
                <button className="ndp-btn ndp-btn-outline" onClick={() => setEditMode(true)}>
                  수정
                </button>
                <button className="ndp-btn ndp-btn-danger" onClick={handleDelete}>
                  삭제
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
