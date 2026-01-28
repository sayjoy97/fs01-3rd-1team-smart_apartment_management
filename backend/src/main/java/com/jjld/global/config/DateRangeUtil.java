package com.jjld.global.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateRangeUtil {

    // 오늘 포함 최근 7일 (날짜 기준)
    public static LocalDate getStartDate7Days() {
        return LocalDate.now().minusDays(6);
    }

    public static LocalDate getEndDate7Days() {
        return LocalDate.now();
    }

    // 오늘 포함 최근 7일 (시간 포함, DB 조회용)
    public static LocalDateTime getStartDateTime7Days() {
        return LocalDate.now().minusDays(6).atStartOfDay();
    }

    public static LocalDateTime getEndDateTime7Days() {
        return LocalDate.now().atTime(LocalTime.MAX);
    }
}