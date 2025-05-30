package com.powerFind.repository.Implementation;

import com.powerFind.model.data.Powerbank;
import com.powerFind.repository.PowerbankRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PowerbankRepositoryImpl implements PowerbankRepository
{
    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public void save(@Nonnull List<Powerbank> powerbanks)
    {
        String sql = """
                    INSERT INTO "powerbank" (id, model, capacity_mah, status, charge_cycles, last_maintenance, location_id)
                    VALUES (:id::uuid, :model, :capacityMah, :status, :chargeCycles, :lastMaintenance, :locationId::uuid)
                    ON CONFLICT (id) DO UPDATE SET
                        model = EXCLUDED.model,
                        capacity_mah = EXCLUDED.capacity_mah,
                        status = EXCLUDED.status,
                        charge_cycles = EXCLUDED.charge_cycles,
                        last_maintenance = EXCLUDED.last_maintenance,
                        location_id = EXCLUDED.location_id
                """;

        Map[] batchParams = powerbanks.stream()
                .map(pb -> {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("id", pb.getId().toString());
                    paramMap.put("model", pb.getModel());
                    paramMap.put("capacityMah", pb.getCapacityMah());
                    paramMap.put("status", pb.getStatus());
                    paramMap.put("chargeCycles", pb.getChargeCycles());
                    paramMap.put("lastMaintenance", pb.getLastMaintenance());
                    paramMap.put("locationId", pb.getLocationId().toString());
                    return paramMap;
                })
                .toArray(Map[]::new);

        jdbcOperations.batchUpdate(sql, batchParams);
    }

    @Nonnull
    @Override
    public Optional<Powerbank> get(@Nonnull UUID id)
    {
        try
        {
            return Optional.ofNullable(jdbcOperations.queryForObject(
                    """
                                SELECT id, model, capacity_mah, status, charge_cycles, last_maintenance, location_id,price_per_minute
                                FROM "powerbank"
                                WHERE id = :id::uuid
                            """,
                    Map.of("id", id.toString()),
                    (rs, rowNum) -> map(rs)
            ));
        } catch (DataAccessException e)
        {
            log.warn("No powerbank found for id: {}. Error: {}", id,
                    e.getMessage());
            return Optional.empty();
        }
    }

    @Nonnull
    private Powerbank map(@Nonnull ResultSet resultSet) throws SQLException
    {
        Powerbank powerbank = new Powerbank();
        powerbank.setId(UUID.fromString(resultSet.getString("id")));
        powerbank.setModel(resultSet.getString("model"));
        powerbank.setCapacityMah(resultSet.getInt("capacity_mah"));
        powerbank.setStatus(resultSet.getString("status"));
        powerbank.setChargeCycles(resultSet.getInt("charge_cycles"));
        powerbank.setLastMaintenance(resultSet.getDate("last_maintenance"));
        powerbank.setPricePerMinute(
                resultSet.getBigDecimal("price_per_minute"));
        powerbank.setLocationId(
                UUID.fromString(resultSet.getString("location_id")));
        return powerbank;
    }
}
