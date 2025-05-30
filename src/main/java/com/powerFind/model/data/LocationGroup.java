package com.powerFind.model.data;

import lombok.Data;

import java.util.UUID;

@Data
public class LocationGroup {
    private UUID id;
    private String city;
    private String district;
}
