import styles from "./ElevatorsStatsSection.module.css";
import {Building, AlertTriangle, Wrench, Plus} from "lucide-react";

export default function ElevatorsStatsSection({elevatorsStats, onClickCreateModalOpen}) {
  return (
    <div className={styles.statsGrid}>
      <div className={styles.statsCard}>
        <div>
          <p className={styles.statsLabel}>전체 대수</p>
          <p className={styles.statsValue}>{elevatorsStats.totalElevators}</p>
        </div>
        <div className={`${styles.iconBox} ${styles.blue}`}>
          <Building className={styles.logoIcon} />
        </div>
      </div>
      <div className={styles.statsCard}>
        <div>
          <p className={styles.statsLabel}>고장</p>
          <p className={styles.statsValue}>{elevatorsStats.errorElevators}</p>
        </div>
        <div className={`${styles.iconBox} ${styles.red}`}>
          <AlertTriangle className={styles.logoIcon} />
        </div>
      </div>
      <div className={styles.statsCard}>
        <div>
          <p className={styles.statsLabel}>점검중</p>
          <p className={styles.statsValue}>{elevatorsStats.repairElevators}</p>
        </div>
        <div className={`${styles.iconBox} ${styles.orange}`}>
          <Wrench className={styles.logoIcon} />
        </div>
      </div>
      <div
        className={styles.statsCard}
        onClick={onClickCreateModalOpen}
        style={{cursor: "pointer"}}
      >
        <div>
          <p className={styles.statsLabel}>엘리베이터 추가</p>
          <p className={styles.statsValue}>&nbsp;</p>
        </div>
        <div className={`${styles.iconBox} ${styles.blue}`}>
          <Plus className={styles.logoIcon} />
        </div>
      </div>
    </div>
  );
}
