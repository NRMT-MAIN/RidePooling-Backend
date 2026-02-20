package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.dto.CancelRequestDTO;
import com.nirmit.ride_pooling.dto.CancelResponseDTO;
import com.nirmit.ride_pooling.dto.CreateRideRequestDTO;
import com.nirmit.ride_pooling.dto.RideRequestResponseDTO;
import com.nirmit.ride_pooling.entity.*;
import com.nirmit.ride_pooling.event.RideRequestCreatedEvent;
import com.nirmit.ride_pooling.repository.*;
import com.nirmit.ride_pooling.utils.GeohashUtils;
import com.nirmit.ride_pooling.utils.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideRequestService {
    private final RideRequestRepository rideRequestRepository ;
    private final RideRepository rideRepository ;
    private final PassengerRepository passengerRepository ;
    private final IdempotencyKeyRepository idempotencyKeyRepository ;
    private final PricingSnapshotService pricingSnapshotService ;
    private final ApplicationEventPublisher eventPublisher ;
    private CancellationRepository cancellationRepository ;

    @Transactional(readOnly = true)
    public RideRequestResponseDTO getRequestById(Long id) throws Exception {

        RideRequest request = rideRequestRepository
                .findById(id)
                .orElseThrow(() ->  {
                    return new ResourceNotFoundException("Request not found") ;
                }) ;

        Double price = pricingSnapshotService.fetchLatestPrice(request);

        log.info("Ride Request fetched with id : " + request.getId());
        return RideRequestResponseDTO.builder()
                .requestId(request.getId())
                .status(request.getStatus().name())
                .rideId(
                        request.getRide() != null
                                ? request.getRide().getId()
                                : null
                )
                .estimatedPrice(price)
                .message("Request fetched successfully")
                .build();
    }


    @Transactional
    public RideRequestResponseDTO createRequest(String idempotencyKey , CreateRideRequestDTO dto) throws Exception {

        Optional<IdempotencyKey> existing = idempotencyKeyRepository.findByIdempotencyKey(idempotencyKey) ;

        if(existing.isPresent()) {
            log.warn("Idempotency key existed!");
            RideRequest oldRequest = rideRequestRepository
                    .findById(existing.get().getRideRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Request Not Found")) ;

            return RideRequestResponseDTO.builder()
                    .requestId(oldRequest.getId())
                    .status(oldRequest.getStatus().name())
                    .rideId( oldRequest.getRide() != null
                            ? oldRequest.getRide().getId()
                            : null)
                    .estimatedPrice(null)
                    .message("Duplicate request detected")
                    .build() ;
        }

        Optional<Passenger> passenger = passengerRepository.findById(dto.getPassengerId()) ;

        if(passenger.isEmpty()) {
            log.warn("Passenger not found in the database!");
            throw new ResourceNotFoundException("Passneger Not Found") ;
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
        IdempotencyKey keyRecord =  IdempotencyKey.builder()
                        .idempotencyKey(idempotencyKey)
                        .rideRequestId(request.getId())
                        .createdAt(LocalDateTime.now())
                        .build();

        idempotencyKeyRepository.save(keyRecord) ;
        log.info("Idempotency key created");

        eventPublisher.publishEvent(new RideRequestCreatedEvent(request.getId()));
        log.info("Ride Request is published with id : " + request.getId());
        log.info("Ride Request is created with id : " + request.getId());
        return RideRequestResponseDTO.builder()
                .requestId(request.getId())
                .status(request.getStatus().name())
                .rideId(null)
                .estimatedPrice(null)
                .message("Request Submitted Succesfully")
                .build() ;
    }

    @Transactional
    public CancelResponseDTO cancelRequest(CancelRequestDTO dto) {
        RideRequest request = rideRequestRepository
                .findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Request Not found"))  ;

        if(request.getStatus() == RideRequestStatus.CANCELLED) {
            return CancelResponseDTO.builder()
                    .requestId(dto.getRequestId())
                    .status(RideRequestStatus.CANCELLED.name())
                    .message("Already cancelled the ride")
                    .build();
        }

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
        Cancellation cancellation = Cancellation.builder()
                .rideRequestId(request.getId())
                .reason(dto.getReason())
                .createdAt(LocalDateTime.now())
                .build();

        cancellationRepository.save(cancellation) ;

        log.info("Ride request got cancelled wit id : " + request.getId());
        return CancelResponseDTO.builder()
                .requestId(dto.getRequestId())
                .status(RideRequestStatus.CANCELLED.name())
                .message("Ride Cancelled!")
                .build();
    }

    private void handleRideRebalancing(Ride ride , RideRequest cancelledRequest) {
        Ride lockedRide = rideRepository.findByIdForUpdate(ride.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Request Not found")) ;

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
            log.info("Ride Request is published with id : " + request.getId());
            eventPublisher.publishEvent(new RideRequestCreatedEvent(request.getId()));
        });
    }
}
