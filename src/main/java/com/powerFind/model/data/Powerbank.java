package com.powerFind.model.data;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
public class Powerbank
{
    private UUID id;
    private String model;
    private int capacityMah;
    private String status;
    private int chargeCycles;
    private Date lastMaintenance;
    private UUID locationId;
    private BigDecimal pricePerMinute;
}