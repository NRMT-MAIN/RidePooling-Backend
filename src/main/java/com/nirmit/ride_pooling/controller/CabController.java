package com.nirmit.ride_pooling.controller;

import com.nirmit.ride_pooling.dto.CabResponseDTO;
import com.nirmit.ride_pooling.dto.CreateCabRequestDTO;
import com.nirmit.ride_pooling.dto.UpdateLocationDTO;
import com.nirmit.ride_pooling.service.CabService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cabs")
@RequiredArgsConstructor
public class CabController {

    private final CabService cabService;

    @PostMapping
    public ResponseEntity<CabResponseDTO> createCab(@RequestBody CreateCabRequestDTO dto) {

        CabResponseDTO response = cabService.createCab(dto) ;

        return new ResponseEntity<>(response , HttpStatus.CREATED);
    }

    @PostMapping("/location/{cabId}")
    public ResponseEntity<Void> updateLocation(@PathVariable Long cabId, @RequestBody UpdateLocationDTO dto) {
        cabService.updateLocation(cabId, dto);
        return ResponseEntity.ok().build();
    }
}
