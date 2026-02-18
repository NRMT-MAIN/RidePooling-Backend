package com.nirmit.ride_pooling.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CabResponseDTO {
    private Long id;
    private String driverName;
    private Integer totalSeats;
    private Integer luggageCapacity;
    private Double currentLat;
    private Double currentLng;
    private String status;
}
