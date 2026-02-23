import styles from "./ProfileCard.module.css";

export default function ProfileCard({adminInfo, onEdit}) {
  // 전화번호 포맷팅 (데이터가 없을 경우를 대비한 안전장치 추가)
  const adminPhone = adminInfo.adminPhone
    ? adminInfo.adminPhone.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3")
    : "번호 없음";
  return (
    <div className={styles.card}>
      <div className={styles.profileHeader}>
        <div className={styles.avatar}>{adminInfo.adminName?.slice(0, 2)}</div>

        <div className={styles.profileInfo}>
          <div className={styles.nameSection}>
            <h2>{adminInfo.adminName}</h2>
            <span className={styles.badge}>{adminInfo.adminRole}</span>
          </div>
          <p className={styles.officeName}>스마트 아파트 관리사무소</p>
        </div>

        <button className={styles.editButton} onClick={onEdit}>
          정보 수정
        </button>
      </div>

      <div className={styles.divider} />

      <div className={styles.infoGrid}>
        <div className={styles.infoItem}>
          <span className={styles.label}>이메일 주소</span>
          <span className={styles.value}>{adminInfo.adminEmail}</span>
        </div>
        <div className={styles.infoItem}>
          <span className={styles.label}>연락처</span>
          <span className={styles.value}>{adminPhone}</span>
        </div>
        <div className={styles.infoItem}>
          <span className={styles.label}>계정 생성일</span>
          <span className={styles.value}>{adminInfo.createdAt?.split("T")[0]}</span>
        </div>
      </div>
    </div>
  );
}
