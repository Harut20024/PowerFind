package com.powerFind.repository;

import com.powerFind.model.data.Location;
import com.powerFind.model.data.LocationAggregate;
import com.powerFind.model.data.LocationGroup;
import com.powerFind.model.domain.*;
import com.powerfind.backoffice.model.NewPowerbankRequest;
import jakarta.annotation.Nonnull;
import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public final class ModelMapper
{

    public static final int CHARGE_CYCLES_DEFAULT = 0;
    public static final String WEAK_VARIANT = "weak";
    public static final String POOR_VARIANT = "poor";
    public static final String BAD_VARIANT = "bad";

    @Nonnull
    public DeviceAggregate map(@Nonnull NewPowerbankRequest request)
    {
        Powerbank powerbank = Powerbank.builder().model(request.getModel())
                .capacityMah(request.getCapacity()).status(PowerbankStatusEnum.AVAILABLE)
                .chargeCycles(CHARGE_CYCLES_DEFAULT)
                .maintenanceNote(request.getMaintenanceNote())
                .pricePerMinute(request.getPricePerMinute())
                .build();

        BatteryHealth batteryHealth = BatteryHealth.builder().voltage(request.getVoltage())
                .healthStatus(getHealth(request.getHealth()))
                .batteryCheckedOn(Timestamp.from(Instant.now()))
                .build();

        com.powerFind.model.domain.Location location = com.powerFind.model.domain.Location.builder()
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        com.powerFind.model.domain.LocationGroup locationGroup = com.powerFind.model.domain.LocationGroup.builder()
                .city(request.getCity())
                .district(request.getDistrict())
                .build();

        return DeviceAggregate.builder().powerbank(powerbank).batteryHealth(batteryHealth)
                .location(location).locationGroup(locationGroup).build();
    }

    private static BatteryHealthStatusEnum getHealth(String request)
    {
        return switch (request.toLowerCase())
        {
            case BAD_VARIANT, POOR_VARIANT, WEAK_VARIANT -> BatteryHealthStatusEnum.BAD;
            default -> BatteryHealthStatusEnum.GOOD;
        };
    }


    @Nonnull
    public LocationGroup map(UUID id, String city, String district)
    {
        LocationGroup group = new LocationGroup();
        group.setId(id);
        group.setCity(city);
        group.setDistrict(district);
        return group;
    }

    @Nonnull
    public Location map(UUID id, UUID groupId, String address, Double latitude, Double longitude)
    {
        Location location = new Location();
        location.setId(id);
        location.setLocationGroupId(groupId);
        location.setAddress(address);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    @Nonnull
    public com.powerfind.backoffice.model.Location map(@Nonnull LocationAggregate dataLocation)
    {
        return new com.powerfind.backoffice.model.Location()
                .streetAddress(dataLocation.getLocation().getAddress())
                .city(Optional.ofNullable(dataLocation.getLocationGroup())
                        .map(LocationGroup::getCity)
                        .orElse(""))
                .district(Optional.ofNullable(dataLocation.getLocationGroup())
                        .map(LocationGroup::getDistrict)
                        .orElse(""));
    }
}
