package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.dto.PassengerSummaryDTO;
import com.nirmit.ride_pooling.dto.RideResponseDTO;
import com.nirmit.ride_pooling.entity.Ride;
import com.nirmit.ride_pooling.entity.RideStatus;
import com.nirmit.ride_pooling.repository.RideRepository;
import com.nirmit.ride_pooling.utils.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;

    @Transactional
    public void startRide(Long rideId) {
        Ride ride = rideRepository.findByIdForUpdate(rideId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Ride must be CONFIRMED before starting"
            );
        }

        ride.transitionTo(RideStatus.IN_PROGRESS);

        rideRepository.save(ride);
    }

    @Transactional
    public void completeRide(Long rideId) {
        Ride ride = rideRepository.findByIdForUpdate(rideId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Ride must be IN_PROGRESS to complete"
            );
        }

        ride.transitionTo(RideStatus.COMPLETED);

        rideRepository.save(ride);
    }

    @Transactional(readOnly = true)
    public RideResponseDTO getRideDetails(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not Found")) ;


        return RideResponseDTO.builder()
                .rideId(rideId)
                .cabId(ride.getCab().getId())
                .passengers(ride.getPassengers()
                        .stream()
                        .map(passenger -> PassengerSummaryDTO.builder()
                                .passengerId(passenger.getPassenger().getId())
                                .pickupOrder(passenger.getPickupOrder())
                                .dropOrder(passenger.getDropOrder())
                                .build()
                        ).toList()
                )
                .build();
    }
}
