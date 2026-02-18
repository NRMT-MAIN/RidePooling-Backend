package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.dto.CabResponseDTO;
import com.nirmit.ride_pooling.dto.CreateCabRequestDTO;
import com.nirmit.ride_pooling.dto.UpdateLocationDTO;
import com.nirmit.ride_pooling.entity.Cab;
import com.nirmit.ride_pooling.entity.CabStatus;
import com.nirmit.ride_pooling.repository.CabRepository;
import com.nirmit.ride_pooling.utils.GeohashUtils;
import com.nirmit.ride_pooling.utils.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CabService {

    private final CabRepository cabRepository;

    @Transactional
    public CabResponseDTO createCab(CreateCabRequestDTO dto) {
        String currentGeoHash = GeohashUtils.encode(dto.getCurrentLat(), dto.getCurrentLng(), 6) ;

        Cab cab = Cab.builder()
                .driverName(dto.getDriverName())
                .totalSeats(dto.getTotalSeats())
                .luggageCapacity(dto.getLuggageCapacity())
                .currentLat(dto.getCurrentLat())
                .currentLng(dto.getCurrentLng())
                .currentGeohash(currentGeoHash)
                .status(CabStatus.AVAILABLE)
                .build();

        cabRepository.save(cab);

        return CabResponseDTO.builder()
                .id(cab.getId())
                .driverName(cab.getDriverName())
                .totalSeats(cab.getTotalSeats())
                .luggageCapacity(cab.getLuggageCapacity())
                .currentLat(cab.getCurrentLat())
                .currentLng(cab.getCurrentLng())
                .status(cab.getStatus().name())
                .build();
    }

    @Transactional
    public void updateLocation(Long cabId, UpdateLocationDTO dto) {

        Cab cab = cabRepository.findByIdForUpdate(cabId)
                .orElseThrow(() -> new ResourceNotFoundException("Cab not found"));

        cab.setCurrentLat(dto.getLat());
        cab.setCurrentLng(dto.getLng());

        String currentGeoHash =GeohashUtils.encode(dto.getLat(), dto.getLng(), 6) ;
        cab.setCurrentGeohash(currentGeoHash);
    }

    @Transactional(readOnly = true)
    public CabResponseDTO getCab(Long cabId) {

        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cab not found"));

        return CabResponseDTO.builder()
                .id(cab.getId())
                .driverName(cab.getDriverName())
                .totalSeats(cab.getTotalSeats())
                .luggageCapacity(cab.getLuggageCapacity())
                .currentLat(cab.getCurrentLat())
                .currentLng(cab.getCurrentLng())
                .status(cab.getStatus().name())
                .build();
    }

}
