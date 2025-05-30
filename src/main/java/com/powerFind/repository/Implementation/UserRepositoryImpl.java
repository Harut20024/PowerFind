package com.powerFind.repository.Implementation;

import com.powerFind.model.data.User;
import com.powerFind.repository.UserRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository
{

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(@Nonnull List<User> users)
    {
        jdbcOperations.batchUpdate(
                """
                        INSERT INTO "user" (id, name, email, phone, registered_on, role_id)
                        VALUES (:id::uuid, :name, :email, :phone, :registered_on, :role_id::uuid)
                        ON CONFLICT (id) DO UPDATE SET
                            name = :name,
                            email = :email,
                            phone = :phone,
                            registered_on = :registered_on,
                            role_id = :role_id::uuid
                        """,
                users.stream()
                        .map(user -> new MapSqlParameterSource()
                                .addValue("id", user.getId().toString())
                                .addValue("name", user.getName())
                                .addValue("email", user.getEmail())
                                .addValue("phone", user.getPhone())
                                .addValue("registered_on", user.getRegisteredOn())
                                .addValue("role_id", user.getRoleId()))
                        .toArray(SqlParameterSource[]::new)
        );
    }

    @Nonnull
    @Override
    public Optional<User> get(@Nonnull UUID id)
    {
        try
        {
            return Optional.ofNullable(jdbcOperations.queryForObject(
                    """
                            SELECT id, name, email, phone, registered_on, role_id
                            FROM "user"
                            WHERE id = :id::uuid
                            """,
                    Map.of("id", id.toString()),
                    (rs, rowNum) -> map(rs)
            ));
        } catch (DataAccessException e)
        {
            log.warn("No user found for id: {}. Error: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Nonnull
    private User map(@Nonnull ResultSet resultSet) throws SQLException
    {
        User user = new User();
        user.setId(UUID.fromString(resultSet.getString("id")));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPhone(resultSet.getString("phone"));
        user.setRegisteredOn(resultSet.getDate("registered_on"));
        user.setRoleId((UUID) resultSet.getObject("role_id"));
        return user;
    }
}
