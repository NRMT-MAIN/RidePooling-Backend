package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.Ride;
import com.nirmit.ride_pooling.entity.RideStatus;
import com.nirmit.ride_pooling.repository.RideRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;

    @Transactional
    public void startRide(Long rideId) {

        Ride ride = rideRepository
                .findByIdForUpdate(rideId)
                .orElseThrow(() ->
                        new RuntimeException("Ride not found"));

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

        Ride ride = rideRepository
                .findByIdForUpdate(rideId)
                .orElseThrow(() ->
                        new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Ride must be IN_PROGRESS to complete"
            );
        }

        ride.transitionTo(RideStatus.COMPLETED);

        rideRepository.save(ride);
    }
}
