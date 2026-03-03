package com.jjld.domain.elevator.service;

public interface ElevatorEventLogService {
    void createLog(int dongValue, int hogi, String payload);
}
