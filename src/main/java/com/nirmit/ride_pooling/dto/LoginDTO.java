package com.nirmit.ride_pooling.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDTO {
    @NotNull(message = "Username Required")
    private String username ;
    @NotNull(message = "Password Required")
    private String password ;
}
