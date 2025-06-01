package com.powerfind.repository.Implementation;

import com.powerfind.model.data.Payment;
import com.powerfind.model.domain.PaymentEnum;
import com.powerfind.repository.PaymentRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository
{

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public void save(@Nonnull Payment payment)
    {
        String sql = """
                    INSERT INTO payment (id, user_id, amount, status, timestamp)
                    VALUES (:id::uuid, :userId::uuid, :amount, :status, :timestamp)
                    ON CONFLICT (id) DO UPDATE SET
                        user_id = EXCLUDED.user_id,
                        amount = EXCLUDED.amount,
                        status = EXCLUDED.status,
                        timestamp = EXCLUDED.timestamp
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", payment.getId().toString());
        params.put("userId", payment.getUserId().toString());
        params.put("amount", payment.getAmount());
        params.put("status", payment.getStatus().name());
        params.put("timestamp", payment.getTimestamp());

        jdbcOperations.update(sql, params);
    }

    @Nonnull
    @Override
    public Optional<Payment> get(@Nonnull UUID id)
    {
        try
        {
            return Optional.ofNullable(jdbcOperations.queryForObject(
                    """
                                SELECT id, user_id, amount, status, timestamp
                                FROM payment
                                WHERE id = :id::uuid
                            """,
                    Map.of("id", id.toString()),
                    (rs, rowNum) -> map(rs)
            ));
        } catch (DataAccessException e)
        {
            log.warn("No payment found for id: {}. Error: {}", id,
                    e.getMessage());
            return Optional.empty();
        }
    }

    @Nonnull
    private Payment map(@Nonnull ResultSet rs) throws SQLException
    {
        Payment payment = new Payment();
        payment.setId(UUID.fromString(rs.getString("id")));
        payment.setUserId(UUID.fromString(rs.getString("user_id")));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setStatus(PaymentEnum.fromString(rs.getString("status"), PaymentEnum.PENDING));
        payment.setTimestamp(rs.getTimestamp("timestamp"));
        return payment;
    }
}
