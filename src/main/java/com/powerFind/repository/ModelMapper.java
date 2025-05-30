package com.powerFind.repository;

import com.powerFind.model.data.Location;
import com.powerFind.model.data.LocationGroup;
import jakarta.annotation.Nonnull;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public final class ModelMapper
{

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
    public com.powerfind.backoffice.model.Location map(Location dataLocation)
    {
        return new com.powerfind.backoffice.model.Location()
                .streetAddress(dataLocation.getAddress())
                .city(Optional.ofNullable(dataLocation.getLocationGroup())
                        .map(LocationGroup::getCity)
                        .orElse(""))
                .district(Optional.ofNullable(dataLocation.getLocationGroup())
                        .map(LocationGroup::getDistrict)
                        .orElse(""));
    }
}
