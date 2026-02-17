package com.nirmit.ride_pooling.validators;

import com.nirmit.ride_pooling.entity.Ride;
import com.nirmit.ride_pooling.entity.RideRequest;
import com.nirmit.ride_pooling.service.RouteOptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConstraintValidator {
    private final RouteOptimizationService routeOptimizationService ;

    public boolean canMerge(Ride ride , RideRequest existed , RideRequest incoming) {
        int updatedSeat = ride.getTotalSeatsUsed()  + incoming.getSeatsRequired();
        int updatedLuggage = ride.getTotalLuggageUsed() + incoming.getLuggageCount();

        if(ride.getCab().getTotalSeats() < updatedSeat) return false ;

        if(ride.getCab().getLuggageCapacity() < updatedLuggage) return false ;

        return routeOptimizationService.isDetourAcceptable(existed , incoming) ;
    }
}
