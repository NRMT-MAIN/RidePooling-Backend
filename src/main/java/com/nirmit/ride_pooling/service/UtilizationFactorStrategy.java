package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.Ride;
import org.springframework.stereotype.Component;

@Component
public class UtilizationFactorStrategy {
    public double getFactor(Ride ride) {
        int used = ride.getTotalSeatsUsed() ;
        int capacity = ride.getCab().getTotalSeats() ;

        double occupancy = (double) used / capacity ;

        if(occupancy >= 0.75) return 0.9 ;
        if(occupancy >= 0.5) return 0.95 ;

        return 1.0 ;
    }
}
