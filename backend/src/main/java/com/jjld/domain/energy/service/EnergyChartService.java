package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dto.CategorySliceDTO;
import com.jjld.domain.energy.dto.PatternPointDTO;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import com.jjld.domain.energy.repository.EnergyUsageSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnergyChartService {
    private final EnergyUsageSummaryRepository energyUsageSummaryRepository;

    public List<PatternPointDTO> getPattern(String period, Long deviceId, LocalDate baseDate) {
        PeriodType periodType = PeriodType.valueOf(period);
        LocalDate date = (baseDate == null) ? LocalDate.now() : baseDate;

        switch (periodType) {
            case TIME_SLOT:
                return getTimeSlotPattern(date, deviceId);
            case DAILY:
                return getDailyPattern(date, deviceId, 14);
            case MONTHLY:
                return getMonthlyPattern(date, deviceId, 6);
            default:
                // TIME_SLOT/DAILY/MONTHLY만 사용
                return List.of();
        }
    }

    private List<PatternPointDTO> getTimeSlotPattern(LocalDate date, Long deviceId) {
        List<Object[]> rows = energyUsageSummaryRepository.sumTimeSlotByDate(date, deviceId);

        // 0~23 기본값 0으로 깔기(그래프가 끊기지 않게)
        Map<Integer, double[]> map = new HashMap<>();
        for (int h = 0; h < 24; h++) map.put(h, new double[]{0.0, 0.0});

        for (Object[] r : rows) {
            Integer timeSlot = (Integer) r[0];
            double actual = ((Number) r[1]).doubleValue();
            double expected = ((Number) r[2]).doubleValue();
            if (timeSlot != null) map.put(timeSlot, new double[]{actual, expected});
        }

        List<PatternPointDTO> result = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            double[] v = map.get(h);
            result.add(new PatternPointDTO(h + "시", v[0], v[1]));
        }
        return result;
    }

    private List<PatternPointDTO> getDailyPattern(LocalDate baseDate, Long deviceId, int days) {
        LocalDate start = baseDate.minusDays(days - 1);
        LocalDate end = baseDate;

        List<Object[]> rows = energyUsageSummaryRepository.sumDailyBetween(start, end, deviceId);

        // 날짜별 기본값 0 세팅
        Map<LocalDate, double[]> map = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            LocalDate d = start.plusDays(i);
            map.put(d, new double[]{0.0, 0.0});
        }

        for (Object[] r : rows) {
            LocalDate d = (LocalDate) r[0];
            double actual = ((Number) r[1]).doubleValue();
            double expected = ((Number) r[2]).doubleValue();
            map.put(d, new double[]{actual, expected});
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd");
        List<PatternPointDTO> result = new ArrayList<>();
        for (Map.Entry<LocalDate, double[]> e : map.entrySet()) {
            double[] v = e.getValue();
            result.add(new PatternPointDTO(e.getKey().format(fmt), v[0], v[1]));
        }
        return result;
    }

    private List<PatternPointDTO> getMonthlyPattern(LocalDate baseDate, Long deviceId, int months) {
        LocalDate endMonth = baseDate.withDayOfMonth(1);
        LocalDate startMonth = endMonth.minusMonths(months - 1);

        List<Object[]> rows = energyUsageSummaryRepository.sumMonthlyBetween(startMonth, endMonth, deviceId);

        // 월별 기본값 0
        Map<LocalDate, double[]> map = new LinkedHashMap<>();
        for (int i = 0; i < months; i++) {
            LocalDate m = startMonth.plusMonths(i);
            map.put(m, new double[]{0.0, 0.0});
        }

        for (Object[] r : rows) {
            LocalDate m = (LocalDate) r[0];
            double actual = ((Number) r[1]).doubleValue();
            double expected = ((Number) r[2]).doubleValue();
            map.put(m, new double[]{actual, expected});
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        List<PatternPointDTO> result = new ArrayList<>();
        for (Map.Entry<LocalDate, double[]> e : map.entrySet()) {
            double[] v = e.getValue();
            result.add(new PatternPointDTO(e.getKey().format(fmt), v[0], v[1]));
        }
        return result;
    }

    public List<CategorySliceDTO> getCategory(LocalDate monthStart, Long deviceId) {
        LocalDate m = (monthStart == null) ? LocalDate.now().withDayOfMonth(1) : monthStart.withDayOfMonth(1);

        List<Object[]> rows = energyUsageSummaryRepository.sumMonthlyByDeviceType(m, deviceId);

        double total = rows.stream()
                .mapToDouble(r -> ((Number) r[1]).doubleValue())
                .sum();

        List<CategorySliceDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            Object deviceType = r[0]; // enum
            double value = ((Number) r[1]).doubleValue();
            double pct = (total <= 0) ? 0.0 : (value / total * 100.0);
            result.add(new CategorySliceDTO(String.valueOf(deviceType), value, pct));
        }
        return result;
    }
}
