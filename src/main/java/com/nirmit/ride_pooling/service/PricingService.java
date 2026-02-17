package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.PricingSnapshot;
import com.nirmit.ride_pooling.entity.Ride;
import com.nirmit.ride_pooling.entity.RideRequest;
import com.nirmit.ride_pooling.repository.PricingSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PricingService {
    private final DistancePricingStrategy distancePricingStrategy ;
    private final DetourPricingStrategy detourPricingStrategy ;
    private final SurgePricingStrategy surgePricingStrategy ;
    private final UtilizationFactorStrategy utilizationFactorStrategy ;
    private final PricingSnapshotRepository pricingSnapshotRepository ;

    public double calculatePrice(Ride ride, RideRequest request) {

        double baseFare = distancePricingStrategy.calculate(ride, request);
        double detourPenalty = detourPricingStrategy.calculate(ride, request);

        double surgeMultiplier = surgePricingStrategy.getSurgeMultiplier();
        double utilizationFactor = utilizationFactorStrategy.getFactor(ride);

        double finalPrice =
                (baseFare + detourPenalty)
                        * surgeMultiplier
                        * utilizationFactor;

        double rounded = Math.round(finalPrice * 100.0) / 100.0;

        saveSnapshot(ride, request,
                baseFare, surgeMultiplier,
                detourPenalty, rounded);

        return rounded;
    }

    private void saveSnapshot(Ride ride,
                              RideRequest request,
                              double base,
                              double surge,
                              double detour,
                              double finalPrice) {

        PricingSnapshot snapshot = PricingSnapshot.builder()
                .ride(ride)
                .passenger(request.getPassenger())
                .baseFare(base)
                .surgeMultiplier(surge)
                .detourPenalty(detour)
                .finalPrice(finalPrice)
                .createdAt(LocalDateTime.now())
                .build();

        pricingSnapshotRepository.save(snapshot);
    }

}
