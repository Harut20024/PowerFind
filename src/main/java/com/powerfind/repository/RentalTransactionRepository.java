package com.powerfind.repository;

import com.powerfind.model.data.RentalTransaction;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalTransactionRepository
{

    void save(@Nonnull List<RentalTransaction> users);

    @Nonnull
    Optional<RentalTransaction> get(@Nonnull UUID id);
}
