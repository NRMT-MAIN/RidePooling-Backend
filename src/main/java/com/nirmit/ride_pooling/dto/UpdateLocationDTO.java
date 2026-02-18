package com.nirmit.ride_pooling.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateLocationDTO {
    @NotNull
    private Double lat;

    @NotNull
    private Double lng;
}
