package com.nirmit.ride_pooling.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelResponseDTO {
    private Long requestId;
    private String status;
    private String message;
}
