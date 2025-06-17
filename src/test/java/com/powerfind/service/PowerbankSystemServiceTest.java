package com.powerfind.service;

import com.powerfind.model.data.Location;
import com.powerfind.model.data.LocationAggregate;
import com.powerfind.model.data.LocationGroup;
import com.powerfind.model.domain.*;
import com.powerfind.repository.LocationGroupRepository;
import com.powerfind.repository.LocationRepository;
import com.powerfind.repository.PowerbankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
class PowerbankSystemServiceTest
{

    public static final String USER_ID = "2a30e8d4-5f3c-43c9-a01e-69e5d5f92223";

    public static final String POWERBANK_ID = "9a07e5a1-2b0c-40d6-e8c8-d6a2c2f9b900";
    public static final String USER = "user";
    public static final String ADMIN = "admin";

    @Autowired
    private PowerbankSystemService powerbankSystemService;

    @Autowired
    private PowerbankRepository powerbankRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationGroupRepository locationGroupRepository;

    @Autowired
    private QueryService queryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private PowerbankKafkaProducer kafkaProducer;

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

        jdbcTemplate.update("INSERT INTO role (id, role_name) VALUES (?, ?)",
                UUID.fromString("0e18c6b2-3f1a-41a7-8f9c-47c3b3d7e001"), "Admin");

        jdbcTemplate.update(
                "INSERT INTO \"user\" (id, name, email, phone, registered_on, role_id) VALUES (?, ?, ?, ?, ?, ?)",
                UUID.fromString(USER_ID), "Harut",
                "harut@example.com", "+37412345678",
                Date.valueOf("2024-01-15"),
                UUID.fromString("0e18c6b2-3f1a-41a7-8f9c-47c3b3d7e001"));

        jdbcTemplate.update("INSERT INTO location_group (id, city, district) VALUES (?, ?, ?)",
                UUID.fromString("4c52f0f6-7b5e-45e1-92b3-81f7f7f1a445"), "Gyumri", "Gyumri Center");

        jdbcTemplate.update(
                "INSERT INTO location (id, location_group_id, address, latitude, longitude) VALUES (?, ?, ?, ?, ?)",
                UUID.fromString("6e74b2f8-9d7f-67f3-b5d5-a3f9f9f3c667"),
                UUID.fromString("4c52f0f6-7b5e-45e1-92b3-81f7f7f1a445"), "Abovyan St 10", 40.789623,
                43.847961);

        jdbcTemplate.update(
                "INSERT INTO powerbank (id, model, capacity_mah, status, charge_cycles, last_maintenance, location_id, price_per_minute) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                UUID.fromString(POWERBANK_ID), "Anker PowerCore 10000",
                10000, "AVAILABLE", 20,
                Date.valueOf("2024-05-01"), UUID.fromString("6e74b2f8-9d7f-67f3-b5d5-a3f9f9f3c667"),
                new BigDecimal("0.05"));

        jdbcTemplate.update(
                "INSERT INTO payment (id, user_id, amount, status, timestamp) VALUES (?, ?, ?, ?, ?)",
                UUID.fromString("a1b2c3d4-5e6f-7a8b-9c0d-e1f2a3b4c5d6"),
                UUID.fromString(USER_ID),
                new BigDecimal("15.00"), "COMPLETED", Timestamp.valueOf("2024-01-16 10:15:00"));

        jdbcTemplate.update(
                "INSERT INTO rental_transaction (id, user_id, powerbank_id, start_time, end_time, payment_id) VALUES (?, ?, ?, ?, ?, ?)",
                UUID.fromString("c3d4e5f6-7a8b-9c0d-1e2f-a3b4c5d6e7f8"),
                UUID.fromString(USER_ID),
                UUID.fromString(POWERBANK_ID),
                Timestamp.valueOf("2024-01-16 10:00:00"),
                Timestamp.valueOf("2024-01-16 12:00:00"),
                UUID.fromString("a1b2c3d4-5e6f-7a8b-9c0d-e1f2a3b4c5d6"));

        jdbcTemplate.update(
                "INSERT INTO battery_health (id, powerbank_id, voltage, health_status, checked_on) VALUES (?, ?, ?, ?, ?)",
                UUID.randomUUID(), UUID.fromString(POWERBANK_ID), 3.70,
                "GOOD",
                Timestamp.valueOf("2024-05-01 08:00:00"));

        jdbcTemplate.update(
                "INSERT INTO maintenance_log (id, powerbank_id, description, maintenance_date) VALUES (?, ?, ?, ?)",
                UUID.randomUUID(), UUID.fromString(POWERBANK_ID),
                "Initial check",
                Date.valueOf("2024-05-01"));
    }


    @Test
    void saveLocation()
    {
        UUID northParkGroupId = UUID.randomUUID();

        locationGroupRepository.save(new LocationGroup(
                northParkGroupId,
                "Gyumri",
                "Gyumri North Park"
        ));

        locationRepository.save(new Location(
                UUID.randomUUID(),
                northParkGroupId,
                "North Park 12",
                40.795000,
                43.860000
        ));

        List<LocationAggregate> allWithGroup = queryService.getAllWithGroup();

        assertEquals(2, allWithGroup.size());

        assertEquals("Abovyan St 10", allWithGroup.getFirst().getLocation().getAddress());
        assertEquals("Gyumri", allWithGroup.getFirst().getLocationGroup().getCity());
        assertEquals("Gyumri Center", allWithGroup.getFirst().getLocationGroup().getDistrict());

        assertEquals("North Park 12", allWithGroup.get(1).getLocation().getAddress());
        assertEquals("Gyumri", allWithGroup.get(1).getLocationGroup().getCity());
        assertEquals("Gyumri North Park", allWithGroup.get(1).getLocationGroup().getDistrict());

    }

    @Test
    void getLocations()
    {
        List<LocationAggregate> allWithGroup = queryService.getAllWithGroup();

        assertEquals(1, allWithGroup.size());

        assertEquals("Abovyan St 10", allWithGroup.getFirst().getLocation().getAddress());
        assertEquals("Gyumri", allWithGroup.getFirst().getLocationGroup().getCity());
        assertEquals("Gyumri Center", allWithGroup.getFirst().getLocationGroup().getDistrict());
    }

    @Test
    void savePowerbank()
    {
        DeviceAggregate deviceAggregate = DeviceAggregate.builder()
                .powerbank(Powerbank.builder()
                        .model("Anker PowerCore 26800")
                        .capacityMah(26800)
                        .status(PowerbankStatusEnum.AVAILABLE)
                        .chargeCycles(0)
                        .pricePerMinute(new BigDecimal("0.08"))
                        .maintenanceNote("Initial setup check")
                        .build())
                .batteryHealth(BatteryHealth.builder()
                        .voltage(3.85)
                        .healthStatus(BatteryHealthStatusEnum.BAD)
                        .batteryCheckedOn(Timestamp.from(Instant.now()))
                        .build())
                .location(com.powerfind.model.domain.Location.builder()
                        .address("New Gyumri Park 15")
                        .latitude(40.800000)
                        .longitude(43.870000)
                        .build())
                .locationGroup(com.powerfind.model.domain.LocationGroup.builder()
                        .city("Gyumri")
                        .district("New Park District")
                        .build())
                .build();

        Optional<UUID> savedIdOptional = powerbankSystemService.savePowerbank(ADMIN,
                deviceAggregate);

        assertTrue(savedIdOptional.isPresent(), "Saved powerbank ID should be present");

        UUID savedId = savedIdOptional.get();

        var savedPowerbank = powerbankRepository.get(savedId).orElseThrow();
        assertEquals("Anker PowerCore 26800", savedPowerbank.getModel());
        assertEquals(26800, savedPowerbank.getCapacityMah());

        verify(kafkaProducer, times(1))
                .send(anyString(), contains("Powerbank saved with ID"));
    }

    @Test
    void getPowerbank()
    {

        com.powerfind.model.data.Powerbank powerbank = powerbankSystemService.getPowerbank(
                USER,
                UUID.fromString(USER_ID),
                2,
                UUID.fromString(POWERBANK_ID)
        ).orElseThrow(() -> new IllegalStateException("Powerbank not found!"));

        assertEquals("Anker PowerCore 10000", powerbank.getModel());
        assertEquals(10000, powerbank.getCapacityMah());
        assertEquals("AVAILABLE", powerbank.getStatus());
        assertEquals(20, powerbank.getChargeCycles());
    }
}