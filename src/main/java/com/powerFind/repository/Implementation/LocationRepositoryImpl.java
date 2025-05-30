package com.powerFind.repository.Implementation;

import com.powerFind.model.data.Location;
import com.powerFind.model.data.LocationGroup;
import com.powerFind.repository.LocationRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository {

    private final NamedParameterJdbcOperations jdbcOperations;


    @Override
    public boolean exists(@Nonnull String city, @Nonnull String district, @Nonnull String address) {
        return Boolean.TRUE.equals(jdbcOperations.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1
                            FROM location_group lg
                            JOIN location l ON lg.id = l.location_group_id
                            WHERE lg.city = :city AND lg.district = :district AND l.address = :address
                        )
                        """,
                Map.of(
                        "city", city,
                        "district", district,
                        "address", address
                ),
                Boolean.class
        ));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLocation(@Nonnull Location location) {
        jdbcOperations.update(
                """
                        INSERT INTO location (id, location_group_id, address, latitude, longitude)
                        VALUES (:id::uuid, :location_group_id::uuid, :address, :latitude, :longitude)
                        ON CONFLICT (id) DO UPDATE SET
                            location_group_id = :location_group_id::uuid,
                            address = :address,
                            latitude = :latitude,
                            longitude = :longitude
                        """,
                new MapSqlParameterSource()
                        .addValue("id", location.getId().toString())
                        .addValue("location_group_id", location.getLocationGroupId().toString())
                        .addValue("address", location.getAddress())
                        .addValue("latitude", location.getLatitude())
                        .addValue("longitude", location.getLongitude())
        );
    }

    @Nonnull
    @Override
    public List<Location> getAllWithGroup() {
        return jdbcOperations.query(
                """
                            SELECT l.id, l.address, l.latitude, l.longitude, l.location_group_id,
                                   lg.city, lg.district
                            FROM location l
                            JOIN location_group lg ON l.location_group_id = lg.id
                        """,
                (rs, rowNum) -> mapWithGroup(rs));
    }

    @Nonnull
    private Location mapWithGroup(ResultSet resultSet) throws SQLException {
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
