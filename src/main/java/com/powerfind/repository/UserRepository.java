package com.powerfind.repository;

import com.powerfind.model.data.User;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository
{

    void save(@Nonnull List<User> users);

    @Nonnull
    Optional<User> get(@Nonnull UUID id);
}
