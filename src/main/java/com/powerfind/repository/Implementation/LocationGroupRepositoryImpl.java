package com.powerfind.repository.Implementation;

import com.powerfind.model.data.LocationGroup;
import com.powerfind.repository.LocationGroupRepository;
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
public class LocationGroupRepositoryImpl implements LocationGroupRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(@Nonnull LocationGroup locationGroup)
    {
        jdbcOperations.update(
                """
                        INSERT INTO location_group (id, city, district)
                        VALUES (:id::uuid, :city, :district)
                        ON CONFLICT (id) DO UPDATE SET
                            city = :city,
                            district = :district
                        """,
                new MapSqlParameterSource()
                        .addValue("id", locationGroup.getId().toString())
                        .addValue("city", locationGroup.getCity())
                        .addValue("district", locationGroup.getDistrict())
        );
    }
}
