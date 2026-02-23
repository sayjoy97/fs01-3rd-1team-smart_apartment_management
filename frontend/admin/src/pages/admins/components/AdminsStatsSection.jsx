import styles from "./AdminsStatsSection.module.css";
import {UserCircle, Shield, Plus} from "lucide-react";

export default function AdminsStatsSection({adminsStats, onClickCreateModalOpen}) {
  return (
    <div className={styles.statsGrid}>
      <div className={styles.statsCard}>
        <div>
          <p className={styles.statsLabel}>전체 관리자</p>
          <p className={styles.statsValue}>{adminsStats.totalAdmins}</p>
        </div>
        <div className={`${styles.iconBox} ${styles.blue}`}>
          <UserCircle className={styles.logoIcon} />
        </div>
      </div>
      <div className={styles.statsCard}>
        <div>
          <p className={styles.statsLabel}>활성 계정</p>
          <p className={styles.statsValue}>{adminsStats.activeAdmins}</p>
        </div>
        <div className={`${styles.iconBox} ${styles.green}`}>
          <Shield className={styles.logoIcon} />
        </div>
      </div>
      <div className={styles.statsCard}>
        <div>
          <p className={styles.statsLabel}>신규 관리자</p>
          <p className={styles.statsValue}>{adminsStats.newAdmins}</p>
        </div>
        <div className={`${styles.iconBox} ${styles.puple}`}>
          <Plus className={styles.logoIcon} />
        </div>
      </div>
      <div
        className={styles.statsCard}
        onClick={onClickCreateModalOpen}
        style={{cursor: "pointer"}}
      >
        <div>
          <p className={styles.statsLabel}>관리자 추가</p>
          <p className={styles.statsValue}>&nbsp;</p>
        </div>
        <div className={`${styles.iconBox} ${styles.orenge}`}>
          <Plus className={styles.logoIcon} />
        </div>
      </div>
    </div>
  );
}
