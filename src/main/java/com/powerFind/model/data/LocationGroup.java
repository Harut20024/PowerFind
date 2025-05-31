package com.powerFind.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationGroup
{
    private UUID id;
    private String city;
    private String district;
}
