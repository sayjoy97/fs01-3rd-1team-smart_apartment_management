import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { noticeDetail, noticeUpdate, noticeDelete, noticeFixedChange } from "../../api/noticeAPI";

import "../../App.css";
import "./Notices.css";
import "./NoticeDetail.css";

export function NoticeDetailPage() {
  const { noticeId } = useParams();
  const navigate = useNavigate();

  const [notice, setNotice] = useState(null);
  const [editMode, setEditMode] = useState(false);

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  // 상세 조회
  useEffect(() => {
    noticeDetail(noticeId)
      .then((res) => {
        setNotice(res.data);
        setTitle(res.data.noticeTitle);
        setContent(res.data.noticeContent);
      })
      .catch(console.error);
  }, [noticeId]);

  if (!notice) return <div>로딩 중...</div>;

  // 수정
  const handleUpdate = async () => {
    const updateData = {
      noticeId: notice.noticeId,
      adminId: 1, // 필요하면 로그인 정보로 교체
      noticeTitle: title,
      noticeContent: content,
    };

    const res = await noticeUpdate(updateData);
    if (res) {
      alert("수정 완료");
      setEditMode(false);
      window.location.reload(); // 수정 후 새로고침하여 최신 정보 반영
    }
  };

  // 삭제
  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    const res = await noticeDelete({ notice_id: notice.noticeId });
    if (res) {
      alert("삭제 완료");
      navigate("/notices");
    }
  };

  // 고정 상태 변경
  const handleFixToggle = async () => {
    console.log("공지사항 고정 상태변화 시도, noticeId: ", notice.noticeId);

    const res = await noticeFixedChange({ notice_id: notice.noticeId });
    if (res) {
      alert("고정 상태 변경 완료");
      window.location.reload(); // 상태 변경 후 새로고침하여 최신 정보 반영
    }
  };

  return (
    <div className="notice-page">
      <div className="notice-card">
        {/* 제목 */}
        {editMode ? (
          <input
            className="notice-input"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        ) : (
          <h2>{notice.noticeTitle}</h2>
        )}

        <p>작성자: {notice.adminName}</p>
        <p>작성일: {notice.createdAt?.replace("T", " ")}</p>
        <p>고정 여부: {notice.fixStatus ? "고정됨" : "일반"}</p>

        <hr />

        {/* 내용 */}
        {editMode ? (
          <textarea
            className="notice-textarea"
            value={content}
            onChange={(e) => setContent(e.target.value)}
          />
        ) : (
          <div className="notice-content">{notice.noticeContent}</div>
        )}

        <div className="notice-buttons">
          {editMode ? (
            <>
              <button onClick={handleUpdate}>저장</button>
              <button onClick={() => setEditMode(false)}>취소</button>
            </>
          ) : (
            <>
              <button onClick={() => setEditMode(true)}>수정</button>
              <button onClick={handleDelete}>삭제</button>
              <button onClick={handleFixToggle}>
                {notice.fixStatus ? "고정 해제" : "고정하기"}
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
