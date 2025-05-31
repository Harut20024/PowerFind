package com.powerFind.model.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Powerbank
{
    private String model;
    private int capacityMah;
    private PowerbankStatusEnum status;
    private int chargeCycles;
    private String maintenanceNote;
    private BigDecimal pricePerMinute;
}
