package com.nirmit.ride_pooling.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotNull(message = "Username Required")
    private String username ;
    @NotNull(message = "Password Required")
    private String password ;
    @NotNull(message = "Name Required")
    private String name ;
    @NotNull(message = "MobileNo Required")
    private String phone ;
}
