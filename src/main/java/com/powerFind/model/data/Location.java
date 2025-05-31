package com.powerFind.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location
{
    private UUID id;
    private UUID locationGroupId;
    private String address;
    private double latitude;
    private double longitude;
}
