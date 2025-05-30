package com.powerFind.repository.Implementation;

import com.powerFind.model.data.Location;
import com.powerFind.repository.LocationRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

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

}
