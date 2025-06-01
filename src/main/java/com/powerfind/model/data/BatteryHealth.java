package com.powerfind.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatteryHealth
{
    private UUID id;
    private UUID powerbankId;
    private double voltage;
    private String healthStatus;
    private Timestamp checkedOn;
}