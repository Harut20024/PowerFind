package com.powerFind.model.data;

import lombok.Data;

import java.security.Timestamp;
import java.util.UUID;

@Data
public class RentalTransaction {
    private UUID id;
    private UUID userId;
    private UUID powerbankId;
    private Timestamp startTime;
    private Timestamp endTime;
    private UUID paymentId;
}
