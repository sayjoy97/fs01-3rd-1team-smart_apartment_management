import { useEffect, useState } from "react";
import "./ComplaintDetailModal.css";
import { CATEGORY_LABEL } from "../complaintCategory";
import { answerWrite, detailView, summaryRequest } from "../../../api/complaintAPI";
import axios from "axios";

const ComplaintDetailModal = ({ data, onClose, onReplyComplete }) => {
  if (!data) return null;

  console.log("민원 상세: ", data);

  const [answerText, setAnswerText] = useState("");
  const [recheckModal, setRecheckModal] = useState(false);
  const [replyContent, setReplyContent] = useState("");

  // 참조 민원 조회
  const [selectedReferenceComplaint, setSelectedReferenceComplaint] = useState([]);
  const [showReferenceModal, setShowReferenceModal] = useState(false);

  // 민원 요약
  const [summaryStatus, setSummaryStatus] = useState(data.summaryStatus); // 초기 상태
  const [summaryText, setSummaryText] = useState(data.summary || "");

  // 답변 저장
  const handleSaveAnswer = async () => {
    try {
      if (!answerText.trim()) {
        alert("답변을 입력해주세요");
        return;
      }

      await answerWrite(data.complaintId, 1, answerText);

      alert("답변이 등록되었습니다.");
      onReplyComplete();
      onClose();
    } catch (e) {
      alert("답변 등록 실패");
    }
  };

  // 참조 민원
  const handleReferenceDetail = async (complaint) => {
    try {
      const res = await detailView(complaint.complaintId);

      setSelectedReferenceComplaint(res.data);

      setShowReferenceModal(true);
      console.log(res.data);
    } catch (err) {
      console.error("민원참조 실패", err);
    }
  };

  // 요약 상태
  const statusMap = {
    NOT_REQUIRED: { message: "글자 수가 적어 요약이 필요하지 않습니다." },
    WAITING: {
      message:
        "아직 요약이 자동 생성되지 않았습니다. 대기 또는 버튼을 눌러 즉시 요약 요청이 가능합니다.",
      buttonText: "즉시 요약 요청",
    },
    FAILED: {
      message: "요약 처리 중 오류가 발생했습니다. 다시 시도해주세요",
      buttonText: "재요청",
    },
  };

  // 요약 버튼 클릭
  const handleRequestSummary = async (complaintId) => {
    try {
      setSummaryStatus("WAITING");
      await summaryRequest(complaintId);

      setTimeout(async () => {
        const res = await detailView(complaintId);

        const newStatus = res.data?.summaryStatus || res.data?.data?.summaryStatus;
        const newText = res.data?.summary || res.data?.data?.summary;

        if (newStatus) {
          setSummaryStatus(newStatus);
          setSummaryText(newText || "");
        }
      }, 500);
    } catch (error) {
      setSummaryStatus("FAILED");
    }
  };

  const statusInfo = statusMap[summaryStatus] || { message: "" }; // 기본 메시지

  return (
    <>
      <div className="modal-overlay" onClick={onClose}>
        {/* 모달 박스*/}
        {data && (
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <h3 className="modal-title">민원 상세보기</h3>

            {/* 상세 데이터 출력 */}
            <div className="modal-content">
              <div className="modal-section">
                <label>#{data.complaintId}</label>
              </div>
              <div className="modal-info-box">
                <div className="modal-info-row">
                  <div className="modal-info-item">
                    <label>작성자</label>
                    <p>{data.houseHolderEmail}</p>
                  </div>
                  <div className="modal-info-item">
                    <label>동호수</label>
                    <p>
                      {data.houseDong}동 {data.houseHo}호
                    </p>
                  </div>
                </div>
                <div className="modal-info-row">
                  <div className="modal-info-item">
                    <label>카테고리</label>
                    <p>{CATEGORY_LABEL[data.category]}</p>
                  </div>
                  <div className="modal-info-item">
                    <label>접수일</label>
                    <p>{new Date(data.createAt).toLocaleString()}</p>
                  </div>
                </div>
              </div>
              <div className="modal-section">
                <label>제목</label>
                <p className="modal-text">{data.title}</p>
              </div>
              <div className="modal-section">
                <label>내용</label>
                <p className="modal-text">{data.content}</p>
              </div>
              <div className="modal-section">
                <label>참조</label>
                {data.referencedComplaints.length > 0 ? (
                  data.referencedComplaints.map((r) => (
                    <span
                      key={r.complaintId}
                      className="reference-item"
                      onClick={() => handleReferenceDetail(r)}
                    >
                      #{r.complaintId}[{r.title}]
                    </span>
                  ))
                ) : (
                  <span>없음</span>
                )}
              </div>

              <div className="modal-section ai-summary">
                <div className="ai-header">
                  <span className="ai-icon">✨</span>
                  <label>요약</label>
                </div>
                <div className="modal-text" style={{ backgroundColor: "#f3f6ff" }}>
                  {summaryStatus === "COMPLETED"
                    ? summaryText
                    : statusMap[summaryStatus]?.message || ""}
                </div>

                {summaryStatus && statusMap[summaryStatus]?.buttonText && (
                  <button onClick={() => handleRequestSummary(data.complaintId)}>
                    {statusMap[summaryStatus].buttonText}
                  </button>
                )}
              </div>

              <div className="modal-section">
                <label>답변</label>
                {data.answer !== null ? (
                  <div className="answer">
                    {data.answer}
                    <p className="answer-date">답변일: {new Date(data.replyAt).toLocaleString()}</p>
                  </div>
                ) : (
                  <textarea
                    className="answer-input"
                    value={answerText}
                    onChange={(e) => setAnswerText(e.target.value)}
                    placeholder="답변을 입력하세요"
                    rows={4}
                  />
                )}
              </div>
            </div>

            {/* 하단 버튼 영역 */}
            <div className="modal-footer">
              <button onClick={onClose}>닫기</button>
              {!data.answer && (
                <button
                  onClick={handleSaveAnswer}
                  disabled={!answerText.trim()}
                  style={{
                    cursor: answerText.trim() ? "pointer" : "not-allowed",
                    opacity: answerText.trim() ? 1 : 0.5,
                    backgroundColor: "#5a8cb9",
                    color: "white",
                  }}
                >
                  답변 저장
                </button>
              )}
            </div>
          </div>
        )}
      </div>
      {/* 참조 모달 */}
      {showReferenceModal && selectedReferenceComplaint && (
        <div className="modal-overlay" onClick={() => setShowReferenceModal(false)}>
          <div
            className="reference-modal-box"
            onClick={(e) => e.stopPropagation()}
            style={{ backgroundColor: "white" }}
          >
            <h3>제목: {selectedReferenceComplaint.data.title}</h3>
            <p>내용: {selectedReferenceComplaint.data.content}</p>
            <p style={{ color: selectedReferenceComplaint.data.answer ? "green" : "orange" }}>
              관리자 답변: {selectedReferenceComplaint.data.answer ?? "아직 답변이 없습니다."}
            </p>
            {selectedReferenceComplaint.answer && (
              <p>답변일: {selectedReferenceComplaint.replyAt}</p>
            )}

            <button onClick={() => setShowReferenceModal(false)} style={{ color: "white" }}>
              닫기
            </button>
          </div>
        </div>
      )}
    </>
  );
};

export default ComplaintDetailModal;
