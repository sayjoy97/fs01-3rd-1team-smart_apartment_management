package com.jjld.domain.noise.entity.Enum;

public enum ProcessStatus {
//    PENDING,    // 승인 필요
//    APPROVED,   // 승인
//    HOLD        // 보류 셋 다 바꿔야 함.(승인보류완료이런거 말고 대기 중, 관찰 중, 알림 발송으로 !)
    UNPROCESSED, // 처리 필요 상태 (아무처리도 x 상태)
    OBSERVING,   // 관찰 중 상태
    NOTIFIED     // 알림 발송 완료된 상태 (사실상 최종)
}
