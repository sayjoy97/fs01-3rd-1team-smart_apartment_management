import React, { useState } from "react";
import { noticeWrite } from "../../api/noticeAPI";
import { useNavigate } from "react-router-dom";
import "./NoticeCreate.css";

export function NoticeCreatePage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    noticeId: 0,
    adminId: 1,
    noticeTitle: "",
    noticeContent: "",
  });

  const currentDate = new Date().toISOString().split("T")[0];

  // 등록 버튼 클릭 시
  const handleCreate = async () => {
    if (!formData.noticeTitle.trim() || !formData.noticeContent.trim()) {
      alert("제목과 내용을 입력해주세요.");
      return;
    }

    // 브라우저 기본 confirm 팝업 사용
    if (window.confirm("공지사항을 등록하시겠습니까?")) {
      try {
        await noticeWrite(formData);
        alert("공지사항이 등록되었습니다.");
        navigate("/notices");
      } catch (err) {
        console.error(err);
        alert("등록에 실패했습니다.");
      }
    }
  };

  // 작성 취소 버튼 클릭 시
  const handleCancel = () => {
    if (window.confirm("작성을 취소하시겠습니까?\n내용은 저장되지 않습니다.")) {
      navigate("/notices");
    }
  };

  return (
    <div className="ncp-wrapper">
      <div className="ncp-card">
        <div className="ncp-form-group">
          <label className="ncp-label">제목</label>
          <input
            className="ncp-input-title"
            value={formData.noticeTitle}
            onChange={(e) => setFormData({ ...formData, noticeTitle: e.target.value })}
            placeholder="공지사항 제목을 입력하세요"
          />
        </div>

        <div className="ncp-meta-info">
          <span>
            작성자: <b>관리자({formData.adminId})</b>
          </span>
          <span>
            작성날짜: <b>{currentDate}</b>
          </span>
        </div>

        <hr className="ncp-divider" />

        <div className="ncp-form-group">
          <label className="ncp-label">내용</label>
          <textarea
            className="ncp-textarea"
            rows={15}
            value={formData.noticeContent}
            onChange={(e) => setFormData({ ...formData, noticeContent: e.target.value })}
            placeholder="공지사항 내용을 입력하세요"
          />
        </div>

        <div className="ncp-bottom-bar">
          <button className="ncp-btn ncp-btn-outline" onClick={() => navigate("/notices")}>
            목록보기
          </button>

          <div className="ncp-right-group">
            <button className="ncp-btn ncp-btn-outline" onClick={handleCancel}>
              취소
            </button>
            <button className="ncp-btn ncp-btn-primary" onClick={handleCreate}>
              등록
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
