import React, { useState } from "react";
import { noticeWrite } from "../../api/noticeAPI";
import { useNavigate } from "react-router-dom";

import "../../App.css";
import "./NoticeCreate.css";

export function NoticeCreatePage({ onBack, onCreate }) {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    noticeId: 0,
    adminId: 1,
    noticeTitle: "",
    noticeContent: "",
  });

  const [showCancelConfirm, setShowCancelConfirm] = useState(false);
  const [showCreateConfirm, setShowCreateConfirm] = useState(false);

  const currentDate = new Date().toISOString().split("T")[0];

  // 등록 버튼 클릭
  const handleCreate = () => {
    if (!formData.noticeTitle.trim() || !formData.noticeContent.trim()) {
      alert("제목과 내용을 입력해주세요.");
      return;
    }
    setShowCreateConfirm(true);
  };

  // 실제 등록 실행
  const confirmCreate = async () => {
    try {
      await noticeWrite(formData);
      alert("공지사항이 등록되었습니다.");
      navigate("/notice"); // 목록 페이지 이동
    } catch (err) {
      console.error(err);
      alert("등록 실패");
    }
  };

  const confirmCancel = () => {
    setShowCancelConfirm(false);
    navigate("/notice");
  };

  return (
    <div className="notice-create-wrapper">
      <div className="notice-card">
        {/* 제목 */}
        <div className="form-group">
          <label className="form-label">제목</label>
          <input
            className="form-title"
            value={formData.noticeTitle}
            onChange={(e) => setFormData({ ...formData, noticeTitle: e.target.value })}
            placeholder="공지사항 제목을 입력하세요"
          />
        </div>

        {/* 작성 정보 */}
        <div className="meta-info">
          <div>
            작성자: <b>{formData.adminId}</b>
          </div>
          <div>
            작성날짜: <b>{currentDate}</b>
          </div>
        </div>

        <hr />

        {/* 내용 */}
        <div className="form-group">
          <label className="form-label">내용</label>
          <textarea
            className="form-textarea"
            rows={12}
            value={formData.noticeContent}
            onChange={(e) => setFormData({ ...formData, noticeContent: e.target.value })}
            placeholder="공지사항 내용을 입력하세요"
          />
        </div>

        {/* 하단 버튼 */}
        <div className="bottom-bar">
          <button className="btn-outline" onClick={() => navigate("/notice")}>
            목록보기
          </button>

          <div className="right-buttons">
            <button className="btn-outline" onClick={() => setShowCancelConfirm(true)}>
              취소
            </button>
            <button className="btn-primary" onClick={handleCreate}>
              등록
            </button>
          </div>
        </div>
      </div>

      {/* 취소 모달 */}
      {showCancelConfirm && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>작성 취소</h3>
            <p>작성을 취소하시겠습니까? 내용은 저장되지 않습니다.</p>
            <div className="modal-actions">
              <button onClick={() => setShowCancelConfirm(false)}>계속 작성</button>
              <button className="danger" onClick={confirmCancel}>
                취소
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 등록 모달 */}
      {showCreateConfirm && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>공지사항 등록</h3>
            <p>공지사항을 등록하시겠습니까?</p>
            <div className="modal-actions">
              <button onClick={() => setShowCreateConfirm(false)}>취소</button>
              <button className="primary" onClick={confirmCreate}>
                등록
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
