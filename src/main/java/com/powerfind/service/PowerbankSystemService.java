package com.powerfind.service;

import com.powerfind.backoffice.model.Location;
import com.powerfind.model.data.*;
import com.powerfind.model.domain.DeviceAggregate;
import com.powerfind.model.domain.PaymentEnum;
import com.powerfind.model.domain.SaveLocationResultEnum;
import com.powerfind.repository.Implementation.*;
import com.powerfind.repository.LocationGroupRepository;
import com.powerfind.repository.LocationRepository;
import com.powerfind.repository.ModelMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
    private final MaintenanceLogRepositoryImpl maintenanceLogRepository;
    private final PaymentRepositoryImpl paymentRepository;
    private final BatteryHealthRepositoryImpl batteryHealthRepository;
    private final PowerbankRepositoryImpl powerbankRepository;
    private final RentalTransactionRepositoryImpl rentalTransactionRepository;
    private final PowerbankKafkaProducer kafkaProducer;
    private final QueryService queryService;

    @Transactional(rollbackFor = Exception.class)
    public SaveLocationResultEnum saveLocation(
            @Nonnull String city,
            @Nonnull String district,
            @Nonnull String address,
            @Nonnull Double latitude,
            @Nonnull Double longitude)
    {
        if (queryService.exists(city, district, address))
        {
            return SaveLocationResultEnum.ALREADY_EXISTS;
        }
        try
        {
            UUID locationGroupId = UUID.randomUUID();

            locationGroupRepository.save(
                    ModelMapper.map(locationGroupId,
                            city,
                            district));
            locationRepository.save(ModelMapper.map(UUID.randomUUID(),
                    locationGroupId,
                    address,
                    latitude,
                    longitude));
            return SaveLocationResultEnum.SUCCESS;

        } catch (Exception e)
        {
            log.error("Error saving location", e);
            return SaveLocationResultEnum.ERROR;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<List<Location>> getLocations()
    {
        return Optional.of(queryService.getAllWithGroup())
                .filter(not(List::isEmpty))
                .map(list -> list.stream()
                        .map(ModelMapper::map)
                        .collect(Collectors.toList()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<UUID> savePowerbank(@Nonnull DeviceAggregate deviceAggregate)
    {
        UUID powerbankId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        UUID locationGroupId = UUID.randomUUID();

        saveLocations(deviceAggregate.getLocation(), deviceAggregate.getLocationGroup(),
                locationGroupId, locationId);

        savePowerbank(deviceAggregate.getPowerbank(), deviceAggregate.getBatteryHealth(),
                powerbankId, locationId);

        Optional.ofNullable(deviceAggregate.getPowerbank().getMaintenanceNote()).ifPresent(
                description -> maintenanceLogRepository.save(new MaintenanceLog(
                        UUID.randomUUID(),
                        powerbankId,
                        description,
                        Date.from(Instant.now())
                ))
        );

        kafkaProducer.send(powerbankId.toString(),
                "Powerbank saved with ID: " + powerbankId + ", Model: " +
                        deviceAggregate.getPowerbank().getModel());

        return Optional.of(powerbankId);
    }

    private void savePowerbank(com.powerfind.model.domain.Powerbank powerbank,
                               com.powerfind.model.domain.BatteryHealth batteryHealth,
                               UUID powerbankId, UUID locationId)
    {
        powerbankRepository.save(new Powerbank(
                powerbankId,
                powerbank.getModel(),
                powerbank.getCapacityMah(),
                powerbank.getStatus().name(),
                powerbank.getChargeCycles(),
                Date.from(Instant.now()),
                locationId,
                powerbank.getPricePerMinute()
        ));

        batteryHealthRepository.save(new com.powerfind.model.data.BatteryHealth(
                UUID.randomUUID(),
                powerbankId,
                batteryHealth.getVoltage(),
                batteryHealth.getHealthStatus().name(),
                batteryHealth.getBatteryCheckedOn()
        ));
    }

    private void saveLocations(com.powerfind.model.domain.Location location,
                               com.powerfind.model.domain.LocationGroup locationGroup,
                               UUID locationGroupId, UUID locationId)
    {
        locationGroupRepository.save(new LocationGroup(
                locationGroupId,
                locationGroup.getCity(),
                locationGroup.getDistrict()
        ));

        locationRepository.save(new com.powerfind.model.data.Location(
                locationId,
                locationGroupId,
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude()
        ));
    }


    @Transactional(rollbackFor = Exception.class)
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
                        Instant presentТime = Instant.now();

                        paymentRepository.save(new Payment(
                                paymentId,
                                userId,
                                totalAmount,
                                PaymentEnum.PENDING,
                                Timestamp.from(presentТime)
                        ));

                        Timestamp endTime = (requestedDurationMinutes != null)
                                ? Timestamp.from(
                                presentТime.plus(requestedDurationMinutes, ChronoUnit.MINUTES))
                                : null;

                        rentalTransactionRepository.save(List.of(new RentalTransaction(
                                UUID.randomUUID(),
                                userId,
                                powerbankId,
                                Timestamp.from(presentТime),
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


