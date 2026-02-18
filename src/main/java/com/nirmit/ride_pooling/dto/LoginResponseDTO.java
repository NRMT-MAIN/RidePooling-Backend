package com.nirmit.ride_pooling.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    private String username ;
    private String message ;
    private String token ;
}
