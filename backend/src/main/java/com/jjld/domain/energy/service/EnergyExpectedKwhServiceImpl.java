package com.jjld.domain.energy.service;

import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyPolicy;
import com.jjld.domain.energy.entity.Enum.CompareBase;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import com.jjld.domain.energy.repository.EnergyUsageSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnergyExpectedKwhServiceImpl implements EnergyExpectedKwhService {
    private final EnergyUsageSummaryRepository usageSummaryRepository;
//    private static final int N = 14; // 최근 14개 평균 (정책/상수화 가능)

    @Override
    public double calculateExpectedKwh(EnergyDevice device, EnergyPolicy policy, PeriodType periodType, LocalDate periodDate, Integer timeslot, double actualKwh) {
        // 1) 기준 데이터 조회 (최근 N개 평균)
        int N = 14; // 예: 최근 14개 평균 (필요시 정책/상수로)
        List<Double> samples;
        switch (policy.getCompareBase()) {
            case DEVICE_PAST_AVG -> samples =
                    usageSummaryRepository.findRecentActualKwhByDeviceBeforeDate(
                            device.getDeviceId(), periodType, timeslot, periodDate, N
                    );
            case DEVICE_TYPE_AVG -> samples =
                    usageSummaryRepository.findRecentActualKwhByDeviceTypeBeforeDate(
                            device.getDeviceType(), periodType, timeslot, periodDate, N
                    );

            case TIME_SLOT_AVG -> {
                // TIME_SLOT_AVG는 "시간대 평균"이 핵심.
                // timeSlot 없으면 타입평균으로 fallback (의미 보존)
                if (timeslot == null) {
                    samples = usageSummaryRepository.findRecentActualKwhByDeviceTypeBeforeDate(
                            device.getDeviceType(), periodType, null, periodDate, N
                    );
                } else {
                    // 같은 타입 + 같은 timeSlot의 TIME_SLOT summary 기반
                    samples = usageSummaryRepository.findRecentActualKwhByDeviceTypeAndTimeSlotBeforeDate(
                            device.getDeviceType(), timeslot, periodDate, N
                    );
                }
            }default -> samples = List.of();}
        if (samples == null || samples.isEmpty()) {
            // 데이터 없으면 안전하게 0 or 최소값. (완성형에서는 “deviceType 기본 baseline” 같은 값도 가능)
            return Math.max(actualKwh * 0.9, 0.1);
        }

        // 더미값 0이나 어쩌다? 0값 들어가면 낭비과대값 될 수 있으니까 방어
        double avg = samples.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        if (avg <= 0.0) {
            return Math.max(actualKwh * 0.9, 0.1);
        }

        return avg;
    }
}
