package com.jjld.domain.alarm.entity;

import com.jjld.domain.alarm.entity.Enum.AlarmType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alarm")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;  // PK

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ELEVATOR','GARDEN')")
    private AlarmType alarmType;  // 알람 타입

    @Column(nullable = false)
    private Boolean isRead;  // 읽음 여부 (true=읽음)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성일

    @Column(nullable = false, length = 200)
    private String alarmTitle;  // 알람 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String alarmContent;  // 알람 내용

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private Boolean deleteYN;  // 삭제 여부 (true=삭제)
}
