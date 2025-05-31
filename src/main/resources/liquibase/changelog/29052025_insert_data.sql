INSERT INTO role (id, role_name)
VALUES ('0e18c6b2-3f1a-41a7-8f9c-47c3b3d7e001', 'Admin'),
       ('d3f2b1c9-8e74-4f59-84ad-3c4db04a1ae6', 'User');

INSERT INTO "user" (id, name, email, phone, registered_on, role_id)
VALUES ('2a30e8d4-5f3c-43c9-a01e-69e5d5f92223', 'Harut', 'harut@example.com', '+37412345678', '2024-01-15',
        '0e18c6b2-3f1a-41a7-8f9c-47c3b3d7e001'),
       ('3b41f9e5-6a4d-74d0-b2f0-70f6e6f03334', 'Andre', 'andre@example.com', '+37498765432', '2024-02-10',
        'd3f2b1c9-8e74-4f59-84ad-3c4db04a1ae6');

INSERT INTO location_group (id, city, district)
VALUES ('4c52f0f6-7b5e-45e1-92b3-81f7f7f1a445', 'Gyumri', 'Gyumri Center'),
       ('5d63a1f7-8c6f-56f2-a4c4-92f8f8f2b556', 'Gyumri', 'Gyumri Industrial Park');

INSERT INTO location (id, location_group_id, address, latitude, longitude)
VALUES ('6e74b2f8-9d7f-67f3-b5d5-a3f9f9f3c667', '4c52f0f6-7b5e-45e1-92b3-81f7f7f1a445', 'Abovyan St 10', 40.789623,
        43.847961),
       ('7f85c3f9-0e8a-78a4-c6e6-b4a0a0b4d778', '4c52f0f6-7b5e-45e1-92b3-81f7f7f1a445', 'Rustaveli St 5', 40.790421,
        43.850203),
       ('8a96d4a0-1f9b-89b5-d7f7-c5b1b1c5e889', '5d63a1f7-8c6f-56f2-a4c4-92f8f8f2b556', 'Industrial Park 3', 40.792150,
        43.854410);

INSERT INTO powerbank (id, model, capacity_mah, status, charge_cycles, last_maintenance, location_id, price_per_minute)
VALUES ('9a07e5a1-2b0c-40d6-e8c8-d6a2c2f9b900', 'Anker PowerCore 10000', 10000, 'AVAILABLE', 20, '2024-05-01',
        '6e74b2f8-9d7f-67f3-b5d5-a3f9f9f3c667', 0.05),
       ('0b18f6b2-3c1d-41e7-f9d9-e7b3d3a7c001', 'Xiaomi Mi 20000', 20000, 'IN_USE', 15, '2024-04-20',
        '6e74b2f8-9d7f-67f3-b5d5-a3f9f9f3c667', 0.07),
       ('1c29a7c3-4d2e-52f8-e0e0-f8c4e4b8d112', 'RAVPower Ace 15000', 15000, 'AVAILABLE', 30, '2024-03-10',
        '7f85c3f9-0e8a-78a4-c6e6-b4a0a0b4d778', 0.06),
       ('2d30b8d4-5e3f-63a9-a1f1-a9d5f5c9e223', 'Aukey Sprint Go 10000', 10000, 'AVAILABLE', 5, '2024-05-15',
        '7f85c3f9-0e8a-78a4-c6e6-b4a0a0b4d778', 0.05),
       ('3e41a9e5-6f4a-74b0-a2c2-b0e6b6d0f334', 'Zendure SuperTank 27000', 27000, 'AVAILABLE', 50, '2024-01-22',
        '8a96d4a0-1f9b-89b5-d7f7-c5b1b1c5e889', 0.10),
       ('4f52a0f6-7a5b-85b1-a3b3-a1f7a7e1a445', 'Samsung EB-P3300', 10000, 'AVAILABLE', 10, '2024-03-18',
        '8a96d4a0-1f9b-89b5-d7f7-c5b1b1c5e889', 0.05),
       ('5a63c1b7-8b6c-96a2-b4c4-b2a8a8f2b556', 'Belkin BoostCharge 20000', 20000, 'IN_USE', 12, '2024-04-05',
        '6e74b2f8-9d7f-67f3-b5d5-a3f9f9f3c667', 0.07),
       ('6a74c2a8-9b7c-a7a3-c5b5-c3a9a9b3a667', 'Sony CP-V20', 20000, 'AVAILABLE', 25, '2024-02-14',
        '7f85c3f9-0e8a-78a4-c6e6-b4a0a0b4d778', 0.07),
       ('7a85b3a9-0a8c-b8b4-c6c6-c4a0a0a4b778', 'Mophie Powerstation XXL', 20000, 'MAINTENANCE', 35, '2024-01-30',
        '8a96d4a0-1f9b-89b5-d7f7-c5b1b1c5e889', 0.09),
       ('8a96b4a0-1a9b-c9c5-d7d7-c5a1a1b5a889', 'Baseus Amblight 30000', 30000, 'AVAILABLE', 8, '2024-05-12',
        '6e74b2f8-9d7f-67f3-b5d5-a3f9f9f3c667', 0.12);

INSERT INTO payment (id, user_id, amount, status, timestamp)
VALUES ('a1b2c3d4-5e6f-7a8b-9c0d-e1f2a3b4c5d6', '2a30e8d4-5f3c-43c9-a01e-69e5d5f92223', 15.00, 'COMPLETED',
        '2024-01-16 10:15:00'),
       ('b2c3d4e5-6f7a-8b9c-0d1e-f2a3b4c5d6e7', '3b41f9e5-6a4d-74d0-b2f0-70f6e6f03334', 25.50, 'COMPLETED',
        '2024-02-11 14:30:00');

INSERT INTO rental_transaction (id, user_id, powerbank_id, start_time, end_time, payment_id)
VALUES ('c3d4e5f6-7a8b-9c0d-1e2f-a3b4c5d6e7f8', '2a30e8d4-5f3c-43c9-a01e-69e5d5f92223',
        '0b18f6b2-3c1d-41e7-f9d9-e7b3d3a7c001', '2024-01-16 10:00:00', '2024-01-16 12:00:00',
        'a1b2c3d4-5e6f-7a8b-9c0d-e1f2a3b4c5d6'),
       ('d4e5f6a7-8b9c-0d1e-2f3a-b4c5d6e7f8a9', '3b41f9e5-6a4d-74d0-b2f0-70f6e6f03334',
        '9a07e5a1-2b0c-40d6-e8c8-d6a2c2f9b900', '2024-02-11 09:00:00', '2024-02-11 11:30:00',
        'b2c3d4e5-6f7a-8b9c-0d1e-f2a3b4c5d6e7');

INSERT INTO battery_health (id, powerbank_id, voltage, health_status, checked_on)
VALUES ('e5f6a7b8-9c0d-1e2f-3a4b-c5d6e7f8a9b0', '9a07e5a1-2b0c-40d6-e8c8-d6a2c2f9b900', 3.70, 'GOOD',
        '2024-05-01 08:00:00'),
       ('f6a7b8c9-0d1e-2f3a-4b5c-d6e7f8a9b0c1', '1c29a7c3-4d2e-52f8-e0e0-f8c4e4b8d112', 3.60, 'GOOD',
        '2024-03-10 09:30:00'),
       ('aa1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d', '0b18f6b2-3c1d-41e7-f9d9-e7b3d3a7c001', 3.75, 'GOOD',
        '2024-04-25 10:00:00'),
       ('bb2c3d4e-5f6a-7b8c-9d0e-1f2a3b4c5d6e', '2d30b8d4-5e3f-63a9-a1f1-a9d5f5c9e223', 3.65, 'GOOD',
        '2024-05-16 11:15:00'),
       ('cc3d4e5f-6a7b-8c9d-0e1f-2a3b4c5d6e7f', '3e41a9e5-6f4a-74b0-a2c2-b0e6b6d0f334', 3.55, 'GOOD',
        '2024-02-20 09:45:00'),
       ('dd4e5f6a-7b8c-9d0e-1f2a-3b4c5d6e7f8a', '4f52a0f6-7a5b-85b1-a3b3-a1f7a7e1a445', 3.80, 'GOOD',
        '2024-03-20 14:30:00'),
       ('ee5f6a7b-8c9d-0e1f-2a3b-4c5d6e7f8a9b', '5a63c1b7-8b6c-96a2-b4c4-b2a8a8f2b556', 3.70, 'BAD',
        '2024-04-10 16:20:00'),
       ('ff6a7b8c-9d0e-1f2a-3b4c-5d6e7f8a9b0c', '6a74c2a8-9b7c-a7a3-c5b5-c3a9a9b3a667', 3.65, 'GOOD',
        '2024-02-18 12:10:00'),
       ('a07b8c9d-0e1f-2a3b-4c5d-6e7f8a9b0c1d', '7a85b3a9-0a8c-b8b4-c6c6-c4a0a0a4b778', 3.50, 'BAD',
        '2024-01-31 13:40:00'),
       ('b18c9d0e-1f2a-3b4c-5d6e-7f8a9b0c1d2e', '8a96b4a0-1a9b-c9c5-d7d7-c5a1a1b5a889', 3.85, 'BAD',
        '2024-05-13 15:25:00');

INSERT INTO maintenance_log (id, powerbank_id, description, maintenance_date)
VALUES ('a7b8c9d0-1e2f-3a4b-5c6d-e7f8a9b0c1d2', '3e41a9e5-6f4a-74b0-a2c2-b0e6b6d0f334', 'Replaced worn cable',
        '2024-01-22'),
       ('b8c9d0e1-2f3a-4b5c-6d7e-f8a9b0c1d2e3', '7a85b3a9-0a8c-b8b4-c6c6-c4a0a0a4b778', 'Firmware update',
        '2024-01-30');
