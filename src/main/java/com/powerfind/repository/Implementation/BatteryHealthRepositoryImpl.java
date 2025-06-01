package com.powerfind.repository.Implementation;

import com.powerfind.model.data.BatteryHealth;
import com.powerfind.repository.BatteryHealthRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BatteryHealthRepositoryImpl implements BatteryHealthRepository

{

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public void save(@Nonnull BatteryHealth batteryHealth)
    {
        String sql = """
                INSERT INTO battery_health (
                    id,
                    powerbank_id,
                    voltage,
                    health_status,
                    checked_on
                ) VALUES (
                    :id::uuid,
                    :powerbankId::uuid,
                    :voltage,
                    :healthStatus,
                    :checkedOn
                )
                """;

        var params = new MapSqlParameterSource()
                .addValue("id", batteryHealth.getId().toString())
                .addValue("powerbankId", batteryHealth.getPowerbankId().toString())
                .addValue("voltage", batteryHealth.getVoltage())
                .addValue("healthStatus", batteryHealth.getHealthStatus())
                .addValue("checkedOn", batteryHealth.getCheckedOn());

        try
        {
            int rows = jdbcOperations.update(sql, params);
            if (rows == 0)
            {
                log.warn("No battery health record inserted for id: {}", batteryHealth.getId());
            } else
            {
                log.info("Battery health record inserted for id: {}", batteryHealth.getId());
            }
        } catch (DataAccessException e)
        {
            log.error("Failed to insert battery health record for id: {}", batteryHealth.getId(),
                    e);
            throw e;
        }
    }
}

