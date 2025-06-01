package com.powerfind.repository;

import com.powerfind.model.data.MaintenanceLog;
import jakarta.annotation.Nonnull;

public interface MaintenanceLogRepository
{
    void save(@Nonnull MaintenanceLog locations);

}
