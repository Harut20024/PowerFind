package com.powerfind.repository;

import com.powerfind.model.data.Payment;
import jakarta.annotation.Nonnull;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository
{
    void save(@Nonnull Payment payments);

    @Nonnull
    Optional<Payment> get(@Nonnull UUID id);
}
