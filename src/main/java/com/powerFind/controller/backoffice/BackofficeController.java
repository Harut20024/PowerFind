package com.powerFind.controller.backoffice;

import com.powerFind.service.PowerbankSystemService;
import com.powerfind.backoffice.api.PublicApi;
import com.powerfind.backoffice.model.EditResponse;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class BackofficeController implements PublicApi
{

    private final PowerbankSystemService powerbankSystemService;

    @Override
    public ResponseEntity<List<com.powerfind.backoffice.model.Location>> getLocation()
    {
        return Objects.requireNonNull(powerbankSystemService.getLocations())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Override
    public ResponseEntity<EditResponse> postLocation(@Nonnull String city,
                                                     @Nonnull String district,
                                                     @Nonnull String address,
                                                     @Nonnull Double latitude,
                                                     @Nonnull Double longitude)
    {


        return switch (powerbankSystemService.saveLocation(city, district,
                address, latitude, longitude))
        {
            case SUCCESS -> ResponseEntity.status(HttpStatus.CREATED)
                    .body(new EditResponse().message(
                            "Location saved successfully"));
            case ALREADY_EXISTS -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new EditResponse().message(
                            "Location already exists"));
            case ERROR ->
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new EditResponse().message(
                                    "Failed to save location due to server error"));
        };
    }

    @Override
    public ResponseEntity<EditResponse> getPowerbank(
            @Nonnull UUID userId,
            @Nonnull UUID powerbankId,
            @Nullable Integer requestedDurationMinutes
    )
    {
        return powerbankSystemService.getPowerbank(userId,
                        requestedDurationMinutes, powerbankId)
                .map(powerbank -> ResponseEntity.ok(
                        new EditResponse().message(
                                "Powerbank retrieved successfully")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new EditResponse().message(
                                "Powerbank not found")));
    }

}