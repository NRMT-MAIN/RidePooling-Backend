package com.nirmit.ride_pooling.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponsePassengerDTO {
    private long passengerId ;
    private String name ;
    private String message ;
}
