package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.Ride;
import com.nirmit.ride_pooling.entity.RideRequest;
import com.nirmit.ride_pooling.entity.RideRequestStatus;
import com.nirmit.ride_pooling.entity.RideStatus;
import com.nirmit.ride_pooling.repository.RideRepository;
import com.nirmit.ride_pooling.repository.RideRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideRequestService {
    private final RideRequestRepository rideRequestRepository ;
    private final RideRepository rideRepository ;
    private final MatchingService matchingService ;

    @Transactional
    public void cancelRequest(Long requestId) {
        RideRequest request = rideRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request Not found"))  ;

        if(request.getStatus() == RideRequestStatus.CANCELLED) return ;

        Ride ride = request.getRide() ;

        if(ride != null && ride.getStatus() == RideStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot cancel after ride started.") ;
        }

        request.setStatus(RideRequestStatus.CANCELLED);
        request.setRide(null);

        rideRequestRepository.save(request) ;

        if(ride != null) {
            handleRideRebalancing(ride , request) ;
        }

    }

    private void handleRideRebalancing(Ride ride , RideRequest cancelledRequest) {
        Ride lockedRide = rideRepository.findByIdForUpdate(ride.getId())
                .orElseThrow() ;

        lockedRide.setTotalSeatsUsed(
                lockedRide.getTotalSeatsUsed() - cancelledRequest.getSeatsRequired()
        );

        lockedRide.setTotalLuggageUsed(
                lockedRide.getTotalLuggageUsed() - cancelledRequest.getLuggageCount()
        );

        List<RideRequest> remainingRequest = rideRequestRepository.findByRideId(ride.getId()) ;

        if(remainingRequest.size() < 2) {
            dissolveRide(lockedRide , remainingRequest) ;
        } else {
            rideRepository.save(lockedRide) ;
        }
    }

    private void dissolveRide(Ride ride , List<RideRequest> remainingRequests) {
        for(RideRequest rr : remainingRequests) {
            rr.setRide(null);
            rr.setStatus(RideRequestStatus.WAITING);

            rideRequestRepository.save(rr) ;
        }

        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride) ;

        remainingRequests.forEach(matchingService::match);
    }
}
