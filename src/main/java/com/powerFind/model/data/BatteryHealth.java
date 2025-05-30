package com.powerFind.model.data;

import lombok.Data;

import java.security.Timestamp;
import java.util.UUID;

@Data
public class BatteryHealth {
    private UUID id;
    private UUID powerbankId;
    private double voltage;
    private String healthStatus;
    private Timestamp checkedOn;
}