package com.powerfind.repository;

import com.powerfind.model.data.BatteryHealth;
import jakarta.annotation.Nonnull;

public interface BatteryHealthRepository
{
    void save(@Nonnull BatteryHealth locations);

}
