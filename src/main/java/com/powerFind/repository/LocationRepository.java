package com.powerFind.repository;

import com.powerFind.model.data.Location;
import jakarta.annotation.Nonnull;

public interface LocationRepository
{

    void save(@Nonnull Location locations);

}
