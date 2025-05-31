package com.powerFind.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceLog
{
    private UUID id;
    private UUID powerbankId;
    private String description;
    private Date maintenanceDate;
}
