package com.powerFind.model.data;

import lombok.Data;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Time;
import java.util.UUID;

@Data
public class Payment {
    private UUID id;
    private UUID userId;
    private BigDecimal amount;
    private String status;
    private Timestamp timestamp;
}
