package com.powerFind.model.data;

import com.powerFind.model.domain.PaymentEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment
{
    private UUID id;
    private UUID userId;
    private BigDecimal amount;
    private PaymentEnum status;
    private Timestamp timestamp;
}
