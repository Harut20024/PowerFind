package com.powerFind.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationAggregate
{
    private Location location;
    private LocationGroup locationGroup;
}
