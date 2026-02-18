package com.nirmit.ride_pooling.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerSummaryDTO {
    private Long passengerId;
    private Integer pickupOrder;
    private Integer dropOrder;
    private Double price;
}
