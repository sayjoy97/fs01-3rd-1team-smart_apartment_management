import React, { useState } from "react";
import "./ComplaintDetailModal.css";
import { CATEGORY_LABEL } from "../complaintCategory";
import { answerWrite } from "../../../api/complaintAPI";

const ComplaintDetailModal = ({ data, onClose }) => {
  if (!data) return null;

  console.log("민원 상세: ", data);

  const [answerText, setAnswerText] = useState("");
  const [recheckModal, setRecheckModal] = useState(false);
  // 답변 저장
  const handleSaveAnswer = async () => {
    try {
      await answerWrite(data.complaintId, 1, answerText);

      alert("답변이 등록되었습니다.");
      onClose();
    } catch (e) {
      alert("답변 등록 실패");
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      {/* 모달 박스*/}
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <h3>민원 상세보기</h3>

        {/* 상세 데이터 출력 */}
        <div className="modal-content">
          <p>
            <b>#</b> {data.complaintId}
          </p>
          <p>
            <b>작성자</b>
            {data.houseHolderEmail}
          </p>
          <p>
            <b>카테고리</b> {CATEGORY_LABEL[data.category]}
          </p>
          <p>
            <b>동호수</b> {data.houseDong}동 {data.houseHo}호
          </p>
          <p>
            <b>접수일</b> {new Date(data.createAt).toLocaleString()}
          </p>
          <p>
            <b>제목</b> {data.title}
          </p>

          <p>
            <b>상태</b> {data.status}
          </p>

          <p>
            <b>내용</b>
          </p>
          <div className="modal-text">{data.content}</div>
          <p>
            <b>요약</b>
          </p>
          {data.summary !== null ? (
            <div className="modal-text">{data.summary}</div>
          ) : (
            <div>AI 요약이 없습니다.</div>
          )}
          <p>
            <b>답변</b>
          </p>
          {data.answer !== null ? (
            <div className="modal-text">{data.answer}</div>
          ) : (
            <textarea
              value={answerText}
              onChange={(e) => setAnswerText(e.target.value)}
              placeholder="답변을 입력하세요"
              rows={4}
              style={{ width: "100%" }}
            ></textarea>
          )}
        </div>

        {/* 하단 버튼 영역 */}
        <div className="modal-footer">
          <p>
            <b>답변일</b> {new Date(data.replyAt).toLocaleString()}
          </p>
          <button onClick={onClose}>닫기</button>
          {!data.answer && <button onClick={handleSaveAnswer}>답변 저장</button>}
        </div>
      </div>
    </div>
  );
};

export default ComplaintDetailModal;
