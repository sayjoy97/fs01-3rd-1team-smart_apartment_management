// 출입문 제어 모달 컴포넌트
export default function EntranceControlModal({ entrance, onConfirm, onClose }) {
  if (!entrance) return null;

  return (
    <div className="modal-bg">
      <div className="modal">
        <h3>출입문 제어</h3>

        <p>
          {entrance.building} {entrance.location}
        </p>

        <button className="btn danger" onClick={() => onConfirm(entrance.id)}>
          상태 변경
        </button>

        <button className="btn" onClick={onClose}>
          취소
        </button>
      </div>
    </div>
  );
}
