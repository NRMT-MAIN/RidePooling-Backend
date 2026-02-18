package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.*;
import com.nirmit.ride_pooling.repository.CabRepository;
import com.nirmit.ride_pooling.repository.RideRepository;
import com.nirmit.ride_pooling.repository.RideRequestRepository;
import com.nirmit.ride_pooling.validators.ConstraintValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {
    private final RideRepository rideRepository ;
    private final RideRequestRepository rideRequestRepository ;
    private final CabRepository cabRepository ;
    private final ConstraintValidator constraintValidator ;

    private static final int MIN_POOL_SIZE = 2;


    @Transactional
    public void match(RideRequest newRequest) {
        String prefix = newRequest.getPickupGeohash().substring(0 , 5) ;

        List<RideRequest> candidates = rideRequestRepository.findAndLockCandidates(prefix , 10) ;

        for(RideRequest candidate : candidates) {
            if (candidate.getRide() != null) {
                Ride ride = rideRepository
                        .findByIdForUpdate(candidate.getRide().getId())
                        .orElseThrow() ;

                if (ride.getStatus() != RideStatus.FORMING) {
                    continue;
                }

                if(constraintValidator.canMerge(ride , candidate , newRequest)) {
                    attachToRide(ride , newRequest) ;
                    return;
                }
            }
        }

        createNewRide(newRequest) ;
    }

    private void createNewRide(RideRequest request) {
        Cab availableCab = cabRepository
                .findByStatus(CabStatus.AVAILABLE)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No cab available")) ;

        Ride ride = Ride.builder()
                .cab(availableCab)
                .status(RideStatus.FORMING)
                .totalSeatsUsed(request.getSeatsRequired())
                .totalLuggageUsed(request.getLuggageCount())
                .estimatedTotalTime(0)
                .build();

        rideRepository.save(ride) ;
        request.setRide(ride);
        request.setStatus(RideRequestStatus.WAITING);

        log.info("Ride created with id : " + ride.getId());
        rideRequestRepository.save(request) ;
    }

    private void attachToRide(Ride ride , RideRequest request) {
        ride.setTotalSeatsUsed(ride.getTotalSeatsUsed() + request.getSeatsRequired());

        ride.setTotalLuggageUsed(ride.getTotalSeatsUsed() + request.getLuggageCount());

        if(ride.getStatus() == RideStatus.FORMING &&
                ride.getTotalSeatsUsed() >= MIN_POOL_SIZE
        ) {
            log.info("Ride is confirmed with id : " + ride.getId());
            ride.transitionTo(RideStatus.CONFIRMED);
        }

        request.setRide(ride);
        request.setStatus(RideRequestStatus.MATCHED);
        log.info("Ride Request is matched with id : " + request.getId());
        rideRepository.save(ride) ;
        rideRequestRepository.save(request) ;
    }
}
