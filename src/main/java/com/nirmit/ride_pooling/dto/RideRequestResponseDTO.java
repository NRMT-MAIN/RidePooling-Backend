package com.nirmit.ride_pooling.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RideRequestResponseDTO {
    private Long requestId;
    private String status;
    private Long rideId;
    private Double estimatedPrice;
    private String message;
}
