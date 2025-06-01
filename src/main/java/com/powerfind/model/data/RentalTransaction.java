package com.powerfind.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalTransaction
{
    private UUID id;
    private UUID userId;
    private UUID powerbankId;
    private Timestamp startTime;
    private Timestamp endTime;
    private UUID paymentId;
}
