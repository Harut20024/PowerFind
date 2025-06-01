package com.powerfind.model.domain;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class BatteryHealth
{
    private double voltage;
    private BatteryHealthStatusEnum healthStatus;
    private Timestamp batteryCheckedOn;
}
