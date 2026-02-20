package com.nirmit.ride_pooling.validators;

import com.nirmit.ride_pooling.entity.Ride;
import com.nirmit.ride_pooling.entity.RideRequest;
import com.nirmit.ride_pooling.service.RouteOptimizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstraintValidator {
    private final RouteOptimizationService routeOptimizationService ;

    public boolean areRequestsCompatible(RideRequest existing, RideRequest incoming) {
        boolean result =  routeOptimizationService
                .isDetourAcceptable(existing, incoming);
        log.info("Compatibility check: {} <-> {} = {}",
                existing.getId(), incoming.getId(), result);

        return result ;
    }

    public boolean canFitInRide(Ride ride, RideRequest incoming) {

        int updatedSeat =
                ride.getTotalSeatsUsed() + incoming.getSeatsRequired();

        int updatedLuggage =
                ride.getTotalLuggageUsed() + incoming.getLuggageCount();

        if (ride.getCab().getTotalSeats() < updatedSeat) return false;
        if (ride.getCab().getLuggageCapacity() < updatedLuggage) return false;

        return true;
    }

}
