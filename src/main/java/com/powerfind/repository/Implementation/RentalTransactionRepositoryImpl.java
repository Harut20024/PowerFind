package com.powerfind.repository.Implementation;

import com.powerfind.model.data.RentalTransaction;
import com.powerfind.repository.RentalTransactionRepository;
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
public class RentalTransactionRepositoryImpl
        implements RentalTransactionRepository
{
    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public void save(@Nonnull List<RentalTransaction> transactions)
    {
        String sql = """
                    INSERT INTO rental_transaction (id, user_id, powerbank_id, start_time, end_time, payment_id)
                    VALUES (:id::uuid, :userId::uuid, :powerbankId::uuid, :startTime, :endTime, :paymentId::uuid)
                    ON CONFLICT (id) DO UPDATE SET
                        user_id = EXCLUDED.user_id,
                        powerbank_id = EXCLUDED.powerbank_id,
                        start_time = EXCLUDED.start_time,
                        end_time = EXCLUDED.end_time,
                        payment_id = EXCLUDED.payment_id
                """;

        @SuppressWarnings("unchecked")
        Map<String, Object>[] batchParams = transactions.stream()
                .map(tx -> {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("id", tx.getId().toString());
                    paramMap.put("userId", tx.getUserId().toString());
                    paramMap.put("powerbankId", tx.getPowerbankId().toString());
                    paramMap.put("startTime", tx.getStartTime());
                    paramMap.put("endTime", tx.getEndTime());
                    paramMap.put("paymentId",
                            tx.getPaymentId() != null ? tx.getPaymentId()
                                    .toString() : null);
                    return paramMap;
                })
                .toArray(Map[]::new);

        jdbcOperations.batchUpdate(sql, batchParams);
    }

    @Nonnull
    @Override
    public Optional<RentalTransaction> get(@Nonnull UUID id)
    {
        try
        {
            return Optional.ofNullable(jdbcOperations.queryForObject(
                    """
                                SELECT id, user_id, powerbank_id, start_time, end_time, payment_id
                                FROM rental_transaction
                                WHERE id = :id::uuid
                            """,
                    Map.of("id", id.toString()),
                    (rs, rowNum) -> map(rs)
            ));
        } catch (DataAccessException e)
        {
            log.warn("No rental transaction found for id: {}. Error: {}", id,
                    e.getMessage());
            return Optional.empty();
        }
    }

    @Nonnull
    private RentalTransaction map(@Nonnull ResultSet rs) throws SQLException
    {
        RentalTransaction tx = new RentalTransaction();
        tx.setId(UUID.fromString(rs.getString("id")));
        tx.setUserId(UUID.fromString(rs.getString("user_id")));
        tx.setPowerbankId(UUID.fromString(rs.getString("powerbank_id")));
        tx.setStartTime(rs.getTimestamp("start_time"));
        tx.setEndTime(rs.getTimestamp("end_time"));
        tx.setPaymentId(UUID.fromString(rs.getString("payment_id")));
        return tx;
    }
}
