package com.powerFind.repository;

import com.powerFind.model.data.BatteryHealth;
import jakarta.annotation.Nonnull;

public interface BatteryHealthRepository
{
    void save(@Nonnull BatteryHealth locations);

}
