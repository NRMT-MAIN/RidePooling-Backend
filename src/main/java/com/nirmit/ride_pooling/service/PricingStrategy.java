package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.Ride;
import com.nirmit.ride_pooling.entity.RideRequest;

public interface PricingStrategy {
    double calculate(Ride ride , RideRequest request) ;
}
