package com.powerFind.model.data;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class MaintenanceLog {
    private UUID id;
    private UUID powerbankId;
    private String description;
    private Date maintenanceDate;
}
