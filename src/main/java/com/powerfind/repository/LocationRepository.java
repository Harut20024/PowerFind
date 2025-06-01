package com.powerfind.repository;

import com.powerfind.model.data.Location;
import jakarta.annotation.Nonnull;

public interface LocationRepository
{

    void save(@Nonnull Location locations);

}
