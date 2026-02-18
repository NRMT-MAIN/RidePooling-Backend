package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.PricingSnapshot;
import com.nirmit.ride_pooling.entity.RideRequest;
import com.nirmit.ride_pooling.repository.PricingSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PricingSnapshotService {
    private final PricingSnapshotRepository pricingSnapshotRepository ;

    public Double fetchLatestPrice(RideRequest request) {

        if (request.getRide() == null) {
            return null;
        }

        return pricingSnapshotRepository
                .findTopByPassengerIdAndRideIdOrderByCreatedAtDesc(
                        request.getPassenger().getId(),
                        request.getRide().getId())
                .map(PricingSnapshot::getFinalPrice)
                .orElse(null);
    }
}
