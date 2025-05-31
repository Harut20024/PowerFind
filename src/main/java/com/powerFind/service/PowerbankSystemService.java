package com.powerFind.service;

import com.powerFind.model.data.Payment;
import com.powerFind.model.data.Powerbank;
import com.powerFind.model.data.RentalTransaction;
import com.powerFind.model.domain.SaveLocationResult;
import com.powerFind.repository.Implementation.PaymentRepositoryImpl;
import com.powerFind.repository.Implementation.PowerbankRepositoryImpl;
import com.powerFind.repository.Implementation.RentalTransactionRepositoryImpl;
import com.powerFind.repository.LocationGroupRepository;
import com.powerFind.repository.LocationRepository;
import com.powerFind.repository.ModelMapper;
import com.powerfind.backoffice.model.Location;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
@Slf4j
public class PowerbankSystemService
{

    private final LocationGroupRepository locationGroupRepository;
    private final LocationRepository locationRepository;
    private final PaymentRepositoryImpl paymentRepository;
    private final PowerbankRepositoryImpl powerbankRepository;
    private final RentalTransactionRepositoryImpl rentalTransactionRepository;
    private final QueryService queryService;

    public SaveLocationResult saveLocation(@Nonnull String city,
                                           @Nonnull String district,
                                           @Nonnull String address,
                                           @Nonnull Double latitude,
                                           @Nonnull Double longitude)
    {
        if (queryService.exists(city, district, address))
        {
            return SaveLocationResult.ALREADY_EXISTS;
        }
        try
        {
            UUID locationGroupId = UUID.randomUUID();

            locationGroupRepository.saveLocationGroup(
                    ModelMapper.map(locationGroupId,
                            city,
                            district));
            locationRepository.saveLocation(ModelMapper.map(UUID.randomUUID(),
                    locationGroupId,
                    address,
                    latitude,
                    longitude));
            return SaveLocationResult.SUCCESS;

        } catch (Exception e)
        {
            log.error("Error saving location", e);
            return SaveLocationResult.ERROR;
        }
    }

    @Nullable
    public Optional<List<Location>> getLocations()
    {
        return Optional.of(queryService.getAllWithGroup())
                .filter(not(List::isEmpty))
                .map(list -> list.stream()
                        .map(ModelMapper::map)
                        .collect(Collectors.toList()));
    }

    @Nonnull
    public Optional<Powerbank> getPowerbank(
            @Nonnull UUID userId,
            @Nullable Integer requestedDurationMinutes,
            @Nonnull UUID powerbankId
    )
    {
        try
        {
            return Optional.of(queryService.existsUserAndPowerbank(userId, powerbankId))
                    .filter(Boolean::booleanValue)
                    .flatMap(ignored -> powerbankRepository.get(powerbankId))
                    .map(powerbank -> {
                        BigDecimal totalAmount = (requestedDurationMinutes != null && powerbank.getPricePerMinute() != null)
                                ? powerbank.getPricePerMinute()
                                .multiply(BigDecimal.valueOf(requestedDurationMinutes))
                                : null;

                        UUID paymentId = UUID.randomUUID();
                        Instant now = Instant.now();

                        paymentRepository.save(new Payment(
                                paymentId,
                                userId,
                                totalAmount,
                                "pending",
                                Timestamp.from(now)
                        ));

                        Timestamp endTime = (requestedDurationMinutes != null)
                                ? Timestamp.from(
                                now.plus(requestedDurationMinutes, ChronoUnit.MINUTES))
                                : null;

                        rentalTransactionRepository.save(List.of(new RentalTransaction(
                                UUID.randomUUID(),
                                userId,
                                powerbankId,
                                Timestamp.from(now),
                                endTime,
                                paymentId
                        )));

                        powerbankRepository.updateStatus(powerbankId);

                        return powerbank;
                    });
        } catch (Exception e)
        {
            log.error("Database error during getPowerbank: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

}


