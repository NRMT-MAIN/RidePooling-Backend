package com.nirmit.ride_pooling.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RideResponseDTO {
    private Long rideId;
    private String status;
    private Long cabId;
    private Integer totalSeatsUsed;
    private Integer totalLuggageUsed;
    private List<PassengerSummaryDTO> passengers;
}
