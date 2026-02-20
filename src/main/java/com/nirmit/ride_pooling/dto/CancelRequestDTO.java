package com.nirmit.ride_pooling.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class CancelRequestDTO {
    @NonNull
    private Long requestId ;
    private String reason = "User Cancelled" ;
}
