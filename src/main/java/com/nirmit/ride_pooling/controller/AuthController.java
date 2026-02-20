package com.nirmit.ride_pooling.controller;

import com.nirmit.ride_pooling.dto.*;
import com.nirmit.ride_pooling.entity.User;
import com.nirmit.ride_pooling.service.UserService;
import com.nirmit.ride_pooling.utils.JWTUtil;
import com.nirmit.ride_pooling.utils.exceptions.InvalidStateException;
import com.nirmit.ride_pooling.utils.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;
    private final UserService userService ;

    @PostMapping("/driver/register")
    public ResponseEntity<RegisterResponseDriverDTO> registerDriver(@RequestBody RegisterDTO dto) {

        RegisterResponseDriverDTO response = userService.registerDriver(dto) ;
        log.info("Driver register successfully with id : " + response.getDriverId());
        return new ResponseEntity<>(response , HttpStatus.CREATED);
    }

    @PostMapping("/passenger/register")
    public ResponseEntity<RegisterResponsePassengerDTO> registerPassenger(@RequestBody RegisterDTO dto) {


        RegisterResponsePassengerDTO response = userService.registerPassenger(dto) ;

        log.info("Passenger register successfully with id : " + response.getPassengerId());
        return new ResponseEntity<>(response , HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO dto) {
        LoginResponseDTO response = userService.login(dto) ;
        return new ResponseEntity<>(response , HttpStatus.ACCEPTED);
    }
}

