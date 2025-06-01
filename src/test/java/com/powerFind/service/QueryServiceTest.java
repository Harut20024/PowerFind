package com.powerFind.service;

import com.powerFind.model.data.LocationAggregate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class QueryServiceTest
{

    private final UUID userId = UUID.randomUUID();
    private final UUID powerbankId = UUID.randomUUID();
    @Autowired
    private QueryService queryService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setupDatabase()
    {
        jdbcTemplate.execute("DELETE FROM maintenance_log");
        jdbcTemplate.execute("DELETE FROM rental_transaction");
        jdbcTemplate.execute("DELETE FROM payment");
        jdbcTemplate.execute("DELETE FROM battery_health");
        jdbcTemplate.execute("DELETE FROM powerbank");
        jdbcTemplate.execute("DELETE FROM location");
        jdbcTemplate.execute("DELETE FROM location_group");
        jdbcTemplate.execute("DELETE FROM \"user\"");
        jdbcTemplate.execute("DELETE FROM role");
    }

    @Test
    void exists()
    {
        UUID locationGroupId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();

        jdbcTemplate.update(
                "INSERT INTO location_group (id, city, district) VALUES (?, ?, ?)",
                locationGroupId, "Gyumri", "Gyumri Center");

        jdbcTemplate.update(
                "INSERT INTO location (id, location_group_id, address, latitude, longitude) VALUES (?, ?, ?, ?, ?)",
                locationId, locationGroupId, "Abovyan St 10", 40.789623, 43.847961);

        assertTrue(queryService.exists("Gyumri", "Gyumri Center", "Abovyan St 10"));
    }

    @Test
    void getAllWithGroup()
    {
        UUID groupId1 = UUID.randomUUID();
        UUID groupId2 = UUID.randomUUID();
        UUID locationId1 = UUID.randomUUID();
        UUID locationId2 = UUID.randomUUID();

        jdbcTemplate.update(
                "INSERT INTO location_group (id, city, district) VALUES (?, ?, ?)",
                groupId1, "Gyumri", "Gyumri Center");

        jdbcTemplate.update(
                "INSERT INTO location (id, location_group_id, address, latitude, longitude) VALUES (?, ?, ?, ?, ?)",
                locationId1, groupId1, "Abovyan St 10", 40.789623, 43.847961);

        jdbcTemplate.update(
                "INSERT INTO location_group (id, city, district) VALUES (?, ?, ?)",
                groupId2, "Gyumri", "Gyumri Industrial Park");

        jdbcTemplate.update(
                "INSERT INTO location (id, location_group_id, address, latitude, longitude) VALUES (?, ?, ?, ?, ?)",
                locationId2, groupId2, "Rustaveli St 5", 40.789623, 43.847961);

        List<LocationAggregate> allWithGroup = queryService.getAllWithGroup();
        assertEquals(2, allWithGroup.size(), "Should return two location groups");
    }

    @Test
    void existsUserAndPowerbank()
    {
        UUID roleId = UUID.randomUUID();
        UUID locationGroupId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();

        jdbcTemplate.update(
                "INSERT INTO role (id, role_name) VALUES (?, ?)",
                roleId, "Admin");

        jdbcTemplate.update(
                "INSERT INTO \"user\" (id, name, email, phone, registered_on, role_id) VALUES (?, ?, ?, ?, ?, ?)",
                userId, "Harut", "harut@example.com", "+37412345678", Date.valueOf("2024-01-15"),
                roleId);

        jdbcTemplate.update(
                "INSERT INTO location_group (id, city, district) VALUES (?, ?, ?)",
                locationGroupId, "Gyumri", "Gyumri Center");

        jdbcTemplate.update(
                "INSERT INTO location (id, location_group_id, address, latitude, longitude) VALUES (?, ?, ?, ?, ?)",
                locationId, locationGroupId, "Abovyan St 10", 40.789623, 43.847961);

        jdbcTemplate.update(
                "INSERT INTO powerbank (id, model, capacity_mah, status, charge_cycles, last_maintenance, location_id, price_per_minute) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                powerbankId, "Anker PowerCore 10000", 10000, "AVAILABLE", 20,
                Date.valueOf("2024-05-01"), locationId, new BigDecimal("0.05"));

        assertTrue(queryService.existsUserAndPowerbank(userId, powerbankId));
    }
}
