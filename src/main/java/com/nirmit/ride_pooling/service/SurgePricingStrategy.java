package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.CabStatus;
import com.nirmit.ride_pooling.repository.CabRepository;
import com.nirmit.ride_pooling.repository.RideRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurgePricingStrategy {

    private final RideRequestRepository rideRequestRepository ;
    private final CabRepository cabRepository ;

    public double getSurgeMultiplier() {

        long waiting = rideRequestRepository.count();
        long available = cabRepository.findByStatus(CabStatus.AVAILABLE).size();

        if (available == 0) return 1.5;

        double ratio = (double) waiting / available;

        if (ratio < 1) return 1.0;
        if (ratio < 2) return 1.2;

        return 1.5;
    }
}
