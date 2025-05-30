package com.powerFind.repository;

import com.powerFind.model.data.LocationGroup;
import jakarta.annotation.Nonnull;

public interface LocationGroupRepository
{
    void saveLocationGroup(@Nonnull LocationGroup locationGroup);

}
