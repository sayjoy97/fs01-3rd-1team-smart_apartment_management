package com.jjld.domain.elevator.entity.Enum;

public enum ElevatorEventType {
    MOVE_START,     // 이동 시작
    ARRIVE,         // 층 도착
    DOOR_OPEN,      // 문 열림
    DOOR_CLOSE,     // 문 닫힘
    ERROR,          // 고장 발생
    REPAIR,         // 점검 시작/진행
    IDLE,           // 점검 완료
}
