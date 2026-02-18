package com.nirmit.ride_pooling.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRideRequest {
    @NotNull
    private Long passengerId ;

    @NotNull
    private Double pickupLat ;

    @NotNull
    private Double pickupLng ;

    @NotNull
    private Double dropLat;

    @NotNull
    private Double dropLng;

    @Min(1)
    private Integer seatsRequired = 1 ;

    @Min(0)
    private Integer luggageCount = 0 ;

    @Min(0)
    private Integer maxDetourMinutes ;
}
