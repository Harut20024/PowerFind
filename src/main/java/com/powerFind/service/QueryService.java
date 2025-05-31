package com.powerFind.service;


import com.powerFind.model.data.Location;
import com.powerFind.model.data.LocationGroup;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryService
{
    private final NamedParameterJdbcOperations jdbcOperations;

    public boolean exists(@Nonnull String city, @Nonnull String district,
                          @Nonnull String address)
    {
        return Boolean.TRUE.equals(jdbcOperations.queryForObject("""
                        SELECT EXISTS (
                            SELECT 1
                            FROM location_group lg
                            JOIN location l ON lg.id = l.location_group_id
                            WHERE lg.city = :city AND lg.district = :district AND l.address = :address
                        )
                        """,
                Map.of("city", city, "district", district, "address", address),
                Boolean.class));
    }

    @Nonnull
    public List<Location> getAllWithGroup()
    {
        return jdbcOperations.query("""
                    SELECT l.id, l.address, l.latitude, l.longitude, l.location_group_id,
                           lg.city, lg.district
                    FROM location l
                    JOIN location_group lg ON l.location_group_id = lg.id
                """, (rs, rowNum) -> mapWithGroup(rs));
    }


    boolean existsUserAndPowerbank(UUID userId, UUID powerbankId)
    {
        return Boolean.TRUE.equals(jdbcOperations.queryForObject("""
                        SELECT
                            EXISTS (SELECT 1 FROM "user" WHERE id = :userId)
                            AND
                            EXISTS (SELECT 1 FROM powerbank WHERE id = :powerbankId)
                        """,
                Map.of("userId", userId, "powerbankId", powerbankId),
                Boolean.class));
    }

    @Nonnull
    private Location mapWithGroup(ResultSet resultSet) throws SQLException
    {
        LocationGroup group = new LocationGroup();
        group.setId(UUID.fromString(resultSet.getString("location_group_id")));
        group.setCity(resultSet.getString("city"));
        group.setDistrict(resultSet.getString("district"));

        Location location = new Location();
        location.setId(UUID.fromString(resultSet.getString("id")));
        location.setLocationGroupId(group.getId());
        location.setAddress(resultSet.getString("address"));
        location.setLatitude(resultSet.getDouble("latitude"));
        location.setLongitude(resultSet.getDouble("longitude"));
        location.setLocationGroup(group);

        return location;
    }
}
