package com.powerFind.controller.backoffice;

import com.powerFind.repository.LocationRepository;
import com.powerFind.repository.ModelMapper;
import com.powerFind.service.PowerbankSystemService;
import com.powerfind.backoffice.api.BackofficeApi;
import com.powerfind.backoffice.model.EditResponse;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;


@RestController
@RequiredArgsConstructor
public class BackofficeController implements BackofficeApi
{

    private final LocationRepository locationRepository;

    private final PowerbankSystemService powerbankSystemService;

    @Override
    public ResponseEntity<List<com.powerfind.backoffice.model.Location>> getLocation()
    {

        return Optional.of(locationRepository.getAllWithGroup()).filter(not(List::isEmpty)).map(list -> list.stream().map(ModelMapper::map).collect(Collectors.toList())).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Override
    public ResponseEntity<EditResponse> postLocation(@Nonnull String city, @Nonnull String district, @Nonnull String address, @Nonnull Double latitude, @Nonnull Double longitude)
    {


        return switch (powerbankSystemService.saveLocation(city, district, address, latitude, longitude))
        {
            case SUCCESS ->
                    ResponseEntity.status(HttpStatus.CREATED).body(new EditResponse().message("Location saved successfully"));
            case ALREADY_EXISTS ->
                    ResponseEntity.status(HttpStatus.CONFLICT).body(new EditResponse().message("Location already exists"));
            case ERROR ->
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EditResponse().message("Failed to save location due to server error"));
        };
    }

    @Override
    public ResponseEntity<EditResponse> getPowerbank(@Nonnull UUID userId, @Nonnull Integer requestedDurationMinutes, @Nonnull UUID powerbankId)
    {
        return null;
    }
}