package com.powerFind.repository;

import com.powerFind.model.data.Location;
import jakarta.annotation.Nonnull;

public interface LocationRepository
{

    void saveLocation(@Nonnull Location locations);

}
