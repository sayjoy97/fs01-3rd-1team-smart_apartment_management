package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.ActuatorLog;
import com.jjld.domain.garden.repository.ActuatorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActuatorLogDAOImpl implements ActuatorLogDAO {
    private final ActuatorLogRepository actuatorLogRepository;

    @Override
    public void saveActuatorLog(ActuatorLog actuatorLog) {
        actuatorLogRepository.save(actuatorLog);
    }
}
