package com.powerFind.repository;

import com.powerFind.model.data.MaintenanceLog;
import jakarta.annotation.Nonnull;

public interface MaintenanceLogRepository
{
    void save(@Nonnull MaintenanceLog locations);

}
