package com.powerfind.repository;

import com.powerfind.model.data.LocationGroup;
import jakarta.annotation.Nonnull;

public interface LocationGroupRepository
{
    void save(@Nonnull LocationGroup locationGroup);

}
