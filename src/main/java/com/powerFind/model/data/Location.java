package com.powerFind.model.data;

import lombok.Data;

import java.util.UUID;

@Data
public class Location {
    private UUID id;
    private UUID locationGroupId;
    private String address;
    private double latitude;
    private double longitude;
    private LocationGroup locationGroup;
}
