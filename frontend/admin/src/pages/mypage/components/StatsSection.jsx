import styles from "./StatsSection.module.css";

export default function StatsSection({adminStats, onClickLogHistory}) {
  return (
    <div className={styles.statsGrid}>
      {/* 1. 처리한 민원 */}
      <div className={styles.statsCard}>
        <p className={styles.label}>처리한 민원</p>
        <p className={styles.value}>
          <span className={styles.number}>{adminStats.resolvedComplaintCount}</span> 건
        </p>
      </div>

      {/* 2. 작성한 공지 */}
      <div className={styles.statsCard}>
        <p className={styles.label}>작성한 공지</p>
        <p className={styles.value}>
          <span className={styles.number}>{adminStats.postedNoticeCount}</span> 건
        </p>
      </div>

      {/* 3. 총 근무일 */}
      <div className={styles.statsCard}>
        <p className={styles.label}>총 근무일</p>
        <p className={styles.value}>
          <span className={styles.number}>{adminStats.totalWorkingDays}</span> 일
        </p>
      </div>

      {/* 4. 접속 로그 (버튼 역할) */}
      <div className={`${styles.statsCard} ${styles.actionCard}`} onClick={onClickLogHistory}>
        <div className={styles.actionContent}>
          <p className={styles.label}>시스템 접속 로그</p>
          <p className={styles.actionText}>목록 조회 →</p>
        </div>
      </div>
    </div>
  );
}
