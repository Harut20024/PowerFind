package com.powerFind.repository;

import com.powerFind.model.data.Powerbank;
import jakarta.annotation.Nonnull;

import java.util.Optional;
import java.util.UUID;

public interface PowerbankRepository
{

    @Nonnull
    Optional<Powerbank> get(@Nonnull UUID id);

    void updateStatus(@Nonnull UUID id);
}
