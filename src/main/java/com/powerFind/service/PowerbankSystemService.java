package com.powerFind.service;

import com.powerFind.model.data.Location;
import com.powerFind.model.data.LocationGroup;
import com.powerFind.model.domain.SaveLocationResult;
import com.powerFind.repository.LocationGroupRepository;
import com.powerFind.repository.LocationRepository;
import com.powerFind.repository.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PowerbankSystemService {

    private final LocationGroupRepository locationGroupRepository;
    private final LocationRepository locationRepository;

    public SaveLocationResult saveLocation(String city, String district, String address, Double latitude, Double longitude) {
        if (locationRepository.exists(city, district, address))
        {
            return SaveLocationResult.ALREADY_EXISTS;
        }
        try
        {
            UUID locationGroupId = UUID.randomUUID();

            locationGroupRepository.saveLocationGroup(ModelMapper.map(locationGroupId,
                    city,
                    district));
            locationRepository.saveLocation(ModelMapper.map(UUID.randomUUID(),
                    locationGroupId,
                    address,
                    latitude,
                    longitude));
            return SaveLocationResult.SUCCESS;

        } catch (Exception e) {
            log.error("Error saving location", e);
            return SaveLocationResult.ERROR;
        }
    }
}


