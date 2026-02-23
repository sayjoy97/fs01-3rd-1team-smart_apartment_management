package com.jjld.domain.energy.service;

import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyPolicy;
import com.jjld.domain.energy.entity.Enum.PeriodType;

import java.time.LocalDate;

public interface EnergyExpectedKwhService {
    double calculateExpectedKwh(
            EnergyDevice device, EnergyPolicy policy, PeriodType periodType, LocalDate periodDate, Integer timeslot, double actualKwh);
}
