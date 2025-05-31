package com.powerFind.model.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationGroup
{
    private String city;
    private String district;
}
