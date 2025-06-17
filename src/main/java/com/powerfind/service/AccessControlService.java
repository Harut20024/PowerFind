package com.powerfind.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AccessControlService
{
    public static final String ADMIN = "admin";
    public static final String USER = "user";
    public static final String GET_LOCATIONS = "getLocations";
    public static final String GET_POWERBANK = "getPowerbank";

    private final Map<String, List<String>> roleAccessMap = Map.of(
            USER, List.of(GET_POWERBANK, GET_LOCATIONS)
    );

    public boolean isAllowed(String role, String methodName)
    {
        return ADMIN.equalsIgnoreCase(role)
                || Optional.ofNullable(roleAccessMap.get(role))
                .map(methods -> methods.contains(methodName))
                .orElse(false);
    }
}
