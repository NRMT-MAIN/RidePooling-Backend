package com.nirmit.ride_pooling.controller;

import com.nirmit.ride_pooling.dto.CancelRequestDTO;
import com.nirmit.ride_pooling.dto.CancelResponseDTO;
import com.nirmit.ride_pooling.dto.CreateRideRequestDTO;
import com.nirmit.ride_pooling.dto.RideRequestResponseDTO;
import com.nirmit.ride_pooling.entity.Passenger;
import com.nirmit.ride_pooling.service.RideRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ride-request")
@RequiredArgsConstructor
public class RideRequestController {
    private final RideRequestService rideRequestService ;

    @PostMapping
    public ResponseEntity<RideRequestResponseDTO> createRequest(
            @RequestHeader(value = "idempotency-key")  String idempotencyKey  ,
            @Valid @RequestBody CreateRideRequestDTO dto
    ) throws Exception {
        RideRequestResponseDTO response = rideRequestService.createRequest(idempotencyKey , dto) ;
        return new ResponseEntity<>(response , HttpStatus.CREATED) ;
    }

    @PostMapping("/cancel")
    public ResponseEntity<CancelResponseDTO> cancelRequest(@RequestBody CancelRequestDTO body) {
        CancelResponseDTO response = rideRequestService.cancelRequest(body);

        return new ResponseEntity<>(response , HttpStatus.OK) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideRequestResponseDTO> getRequest(@PathVariable Long id) throws Exception {

        RideRequestResponseDTO response = rideRequestService.getRequestById(id);

        return new ResponseEntity<>(response , HttpStatus.OK);
    }
}
