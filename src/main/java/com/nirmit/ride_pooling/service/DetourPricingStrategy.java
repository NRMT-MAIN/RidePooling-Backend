package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.Ride;
import com.nirmit.ride_pooling.entity.RideRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DetourPricingStrategy implements PricingStrategy {
    private final  RouteOptimizationService routeOptimizationService ;

    private static final double PRICE_PER_EXTRA_MIN = 2.0 ;

    @Override
    public double calculate(Ride ride, RideRequest request) {
        int directTime = routeOptimizationService
                .estimateTravelTimeMinutes(
                        request.getPickupLat(),
                        request.getPickupLng(),
                        request.getDropLat(),
                        request.getDropLng()
                ) ;
        int rideTime = ride.getEstimatedTotalTime() ;

        int extraTime = Math.max(0 , rideTime - directTime) ;

        return extraTime * PRICE_PER_EXTRA_MIN ;
    }
}
