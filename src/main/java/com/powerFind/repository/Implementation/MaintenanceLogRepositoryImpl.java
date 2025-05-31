package com.powerFind.repository.Implementation;

import com.powerFind.model.data.MaintenanceLog;
import com.powerFind.repository.MaintenanceLogRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MaintenanceLogRepositoryImpl implements MaintenanceLogRepository
{

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public void save(@Nonnull MaintenanceLog maintenanceLog)
    {
        String sql = """
                    INSERT INTO maintenance_log (
                        id, powerbank_id, description, maintenance_date
                    )
                    VALUES (
                        :id::uuid, :powerbankId::uuid, :description, :maintenanceDate
                    )
                    ON CONFLICT (id) DO UPDATE SET
                        powerbank_id = :powerbankId::uuid,
                        description = :description,
                        maintenance_date = :maintenanceDate
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", maintenanceLog.getId().toString())
                .addValue("powerbankId", maintenanceLog.getPowerbankId().toString())
                .addValue("description", maintenanceLog.getDescription())
                .addValue("maintenanceDate", maintenanceLog.getMaintenanceDate());

        jdbcOperations.update(sql, params);
    }
}
