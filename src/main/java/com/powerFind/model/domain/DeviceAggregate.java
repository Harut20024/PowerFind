package com.powerFind.model.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceAggregate
{
    private Powerbank powerbank;
    private BatteryHealth batteryHealth;
    private Location location;
    private LocationGroup locationGroup;
}

