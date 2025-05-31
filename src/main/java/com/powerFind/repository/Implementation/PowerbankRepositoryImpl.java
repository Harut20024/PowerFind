package com.powerFind.repository.Implementation;

import com.powerFind.model.data.Powerbank;
import com.powerFind.repository.PowerbankRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PowerbankRepositoryImpl implements PowerbankRepository
{
    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public void save(@Nonnull java.util.List<Powerbank> powerbanks)
    {
        String sql = """
                INSERT INTO "powerbank" (
                    id,
                    model,
                    capacity_mah,
                    status,
                    charge_cycles,
                    last_maintenance,
                    location_id
                ) VALUES (
                    :id::uuid,
                    :model,
                    :capacityMah,
                    :status,
                    :chargeCycles,
                    :lastMaintenance,
                    :locationId::uuid
                )
                ON CONFLICT (id) DO UPDATE SET
                    model = EXCLUDED.model,
                    capacity_mah = EXCLUDED.capacity_mah,
                    status = EXCLUDED.status,
                    charge_cycles = EXCLUDED.charge_cycles,
                    last_maintenance = EXCLUDED.last_maintenance,
                    location_id = EXCLUDED.location_id
                """;

        SqlParameterSource[] batchParams = powerbanks.stream()
                .map(pb -> new MapSqlParameterSource()
                        .addValue("id", pb.getId().toString())
                        .addValue("model", pb.getModel())
                        .addValue("capacityMah", pb.getCapacityMah())
                        .addValue("status", pb.getStatus())
                        .addValue("chargeCycles", pb.getChargeCycles())
                        .addValue("lastMaintenance", pb.getLastMaintenance())
                        .addValue("locationId", pb.getLocationId().toString())
                )
                .toArray(MapSqlParameterSource[]::new);

        jdbcOperations.batchUpdate(sql, batchParams);
    }

    @Nonnull
    @Override
    public Optional<Powerbank> get(@Nonnull UUID id)
    {
        try
        {
            return Optional.ofNullable(
                    jdbcOperations.queryForObject(
                            """
                                        SELECT
                                            id,
                                            model,
                                            capacity_mah,
                                            status,
                                            charge_cycles,
                                            last_maintenance,
                                            location_id,
                                            price_per_minute
                                FROM "powerbank"
                                WHERE id = :id::uuid
                            """,
                            java.util.Map.of("id", id.toString()),
                            (rs, rowNum) -> map(rs)
                    )
            );
        } catch (DataAccessException ex)
        {
            log.warn("No powerbank found for id {}: {}", id, ex.getMessage());
            return Optional.empty();
        }
    }

    @Nonnull
    private Powerbank map(@Nonnull ResultSet rs) throws SQLException
    {
        Powerbank powerbank = new Powerbank();
        powerbank.setId(UUID.fromString(rs.getString("id")));
        powerbank.setModel(rs.getString("model"));
        powerbank.setCapacityMah(rs.getInt("capacity_mah"));
        powerbank.setStatus(rs.getString("status"));
        powerbank.setChargeCycles(rs.getInt("charge_cycles"));
        powerbank.setLastMaintenance(rs.getDate("last_maintenance"));
        powerbank.setPricePerMinute(rs.getBigDecimal("price_per_minute"));
        powerbank.setLocationId(UUID.fromString(rs.getString("location_id")));
        return powerbank;
    }
}
