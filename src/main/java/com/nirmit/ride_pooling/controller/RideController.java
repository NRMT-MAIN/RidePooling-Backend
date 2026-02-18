package com.nirmit.ride_pooling.controller;

import com.nirmit.ride_pooling.dto.RideResponseDTO;
import com.nirmit.ride_pooling.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService ;

    @PostMapping("/start/{rideId}")
    public ResponseEntity<Void> startRide(@PathVariable Long rideId) {
        rideService.startRide(rideId);
        return new ResponseEntity<>(HttpStatus.OK) ;
    }

    @PostMapping("/complete/{rideId}")
    public ResponseEntity<Void> completeRide(@PathVariable Long rideId) {
        rideService.completeRide(rideId);
        return new ResponseEntity<>(HttpStatus.OK) ;
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponseDTO> getRide(@PathVariable Long rideId) {

        RideResponseDTO response = rideService.getRideDetails(rideId);
        return ResponseEntity.ok(response);
    }
}
