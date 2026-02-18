package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.dto.CreateRideRequest;
import com.nirmit.ride_pooling.dto.RideRequestResponseDTO;
import com.nirmit.ride_pooling.entity.*;
import com.nirmit.ride_pooling.event.RideRequestCreatedEvent;
import com.nirmit.ride_pooling.repository.PassengerRepository;
import com.nirmit.ride_pooling.repository.RideRepository;
import com.nirmit.ride_pooling.repository.RideRequestRepository;
import com.nirmit.ride_pooling.utils.GeohashUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RideRequestService {
    private final RideRequestRepository rideRequestRepository ;
    private final RideRepository rideRepository ;
    private final MatchingService matchingService ;
    private final PassengerRepository passengerRepository ;
    private final ApplicationEventPublisher eventPublisher ;

    @Transactional
    public RideRequestResponseDTO createRequest(CreateRideRequest dto) throws Exception {

        Optional<Passenger> passenger = passengerRepository.findById(dto.getPassengerId()) ;

        if(passenger.isEmpty()) {
            throw new Exception("Passneger Not Found") ;
        }

        String pickupGeohash = GeohashUtils.encode(dto.getPickupLat(), dto.getPickupLng(), 6) ;
        String dropGeohash = GeohashUtils.encode(dto.getDropLat() , dto.getDropLng() , 6) ;

        RideRequest request = RideRequest.builder()
                .passenger(passenger.get())
                .pickupLat(dto.getPickupLat())
                .pickupLng(dto.getPickupLng())
                .dropLat(dto.getDropLat())
                .dropLng(dto.getDropLng())
                .pickupGeohash(pickupGeohash)
                .dropGeohash(dropGeohash)
                .seatsRequired(dto.getSeatsRequired())
                .luggageCount(dto.getLuggageCount())
                .maxDetourMinutes(dto.getMaxDetourMinutes())
                .status(RideRequestStatus.WAITING)
                .requestTimestamp(LocalDateTime.now())
                .build();

        rideRequestRepository.save(request) ;

        eventPublisher.publishEvent(new RideRequestCreatedEvent(request.getId()));

        return RideRequestResponseDTO.builder()
                .requestId(request.getId())
                .status(request.getStatus().name())
                .rideId(null)
                .estimatedPrice(null)
                .message("Request Submitted Succesfully")
                .build() ;
    }

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
        }

        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride) ;

        remainingRequests.forEach(request -> {
            eventPublisher.publishEvent(new RideRequestCreatedEvent(request.getId()));
        });
    }
}
