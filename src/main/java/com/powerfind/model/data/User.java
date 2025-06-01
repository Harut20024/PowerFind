package com.powerfind.model.data;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class User
{
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Date registeredOn;
    private UUID roleId;
}
