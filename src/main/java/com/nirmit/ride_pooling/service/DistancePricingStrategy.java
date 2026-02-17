package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.Ride;
import com.nirmit.ride_pooling.entity.RideRequest;
import com.nirmit.ride_pooling.utils.DistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistancePricingStrategy implements PricingStrategy {

    private final DistanceUtils distanceUtils ;

    private static final double PRICE_PER_KM = 15.0 ;

    @Override
    public double calculate(Ride ride, RideRequest request) {
        double distance = distanceUtils.calculateDistance(
                request.getPickupLat() ,
                request.getPickupLng(),
                request.getDropLat(),
                request.getDropLng()
        ) ;

        return distance * PRICE_PER_KM ;
    }
}
