package com.powerFind.repository;

import com.powerFind.model.data.Location;
import com.powerFind.model.data.LocationGroup;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationRepository {

    void saveLocation(@Nonnull Location locations);

    @Nonnull
    List<Location> getAllWithGroup();

    boolean exists(@Nonnull String city, @Nonnull String district, @Nonnull String address);

}
