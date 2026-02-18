package com.nirmit.ride_pooling.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCabRequestDTO {
    @NotBlank
    private String driverName;

    @Min(1)
    private Integer totalSeats;

    @Min(0)
    private Integer luggageCapacity;

    @NotNull
    private Double currentLat;

    @NotNull
    private Double currentLng;
}
