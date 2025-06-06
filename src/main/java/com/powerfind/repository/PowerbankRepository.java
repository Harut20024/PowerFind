package com.powerfind.repository;

import com.powerfind.model.data.Powerbank;
import jakarta.annotation.Nonnull;

import java.util.Optional;
import java.util.UUID;

public interface PowerbankRepository
{
    void save(Powerbank powerbank);

    @Nonnull
    Optional<Powerbank> get(@Nonnull UUID id);

    void updateStatus(@Nonnull UUID id);
}
