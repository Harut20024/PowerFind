package com.powerfind.repository.Implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.powerfind.model.data.Audit;
import com.powerfind.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;


@Slf4j
@Repository
@RequiredArgsConstructor
public class AuditRepositoryImpl implements AuditRepository
{

    private final NamedParameterJdbcOperations jdbc;

    private static PGobject toPgJson(JsonNode node)
    {
        if (node == null) return null;
        try
        {
            PGobject obj = new PGobject();
            obj.setType("jsonb");
            obj.setValue(node.toString());
            return obj;
        } catch (SQLException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Audit audit)
    {
        String sql = """
                    INSERT INTO audit(id, action, table_name, method, data)
                    VALUES(:id, :act, :tbl, :mth, :dat)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", audit.getId())
                .addValue("act", audit.getAction())
                .addValue("tbl", audit.getTableName())
                .addValue("mth", audit.getMethod())
                .addValue("dat", toPgJson(audit.getData()));

        jdbc.update(sql, params);
    }
}
