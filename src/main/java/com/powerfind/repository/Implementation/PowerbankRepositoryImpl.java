package com.powerfind.repository.Implementation;

import com.powerfind.model.data.Powerbank;
import com.powerfind.repository.PowerbankRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PowerbankRepositoryImpl implements PowerbankRepository
{
    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public void save(Powerbank powerbank)
    {
        String sql = """
                INSERT INTO powerbank (
                    id,
                    model,
                    capacity_mah,
                    status,
                    charge_cycles,
                    last_maintenance,
                    location_id,
                    price_per_minute
                ) VALUES (
                    :id::uuid,
                    :model,
                    :capacityMah,
                    :status,
                    :chargeCycles,
                    :lastMaintenance,
                    :locationId::uuid,
                    :pricePerMinute
                )
                """;

        var params = new MapSqlParameterSource()
                .addValue("id", powerbank.getId().toString())
                .addValue("model", powerbank.getModel())
                .addValue("capacityMah", powerbank.getCapacityMah())
                .addValue("status", powerbank.getStatus())
                .addValue("chargeCycles", powerbank.getChargeCycles())
                .addValue("lastMaintenance", powerbank.getLastMaintenance())
                .addValue("locationId", powerbank.getLocationId().toString())
                .addValue("pricePerMinute", powerbank.getPricePerMinute());

        int updatedRows = jdbcOperations.update(sql, params);

        if (updatedRows == 0)
        {
            log.warn("Failed to insert powerbank with id: {}", powerbank.getId());
        } else
        {
            log.info("Successfully inserted powerbank with id: {}", powerbank.getId());
        }
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

    @Override
    public void updateStatus(@Nonnull UUID id)
    {
        String sql = """
                UPDATE powerbank
                SET status = 'in_use'
                WHERE id = :id::uuid
                """;

        executeUpdate(sql, Map.of("id", id.toString()))
                .map(success -> id)
                .or(() -> {
                    log.warn("No powerbank found to update status for id: {}", id);
                    return Optional.empty();
                });
    }

    private Optional<Boolean> executeUpdate(String sql, Map<String, String> params)
    {
        return Optional.of(jdbcOperations.update(sql, params))
                .filter(count -> count > 0)
                .map(count -> true);
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
