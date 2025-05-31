package com.powerFind.model.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location
{
    private String address;
    private double latitude;
    private double longitude;
}
