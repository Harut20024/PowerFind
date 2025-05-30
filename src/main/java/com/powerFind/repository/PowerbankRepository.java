package com.powerFind.repository;

import com.powerFind.model.data.Powerbank;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PowerbankRepository
{

    void save(@Nonnull List<Powerbank> users);

    @Nonnull
    Optional<Powerbank> get(@Nonnull UUID id);
}
