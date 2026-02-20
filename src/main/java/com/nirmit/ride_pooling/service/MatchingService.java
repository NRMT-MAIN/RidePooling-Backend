package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.*;
import com.nirmit.ride_pooling.repository.CabRepository;
import com.nirmit.ride_pooling.repository.RidePassengerRepository;
import com.nirmit.ride_pooling.repository.RideRepository;
import com.nirmit.ride_pooling.repository.RideRequestRepository;
import com.nirmit.ride_pooling.validators.ConstraintValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {
    private final PricingService pricingService ;
    private final RidePassengerRepository ridePassengerRepository ;
    private final RouteOptimizationService routeOptimizationService ;

    private final RideRepository rideRepository;
    private final RideRequestRepository rideRequestRepository;
    private final CabRepository cabRepository;
    private final ConstraintValidator constraintValidator;

    private static final int MIN_POOL_SIZE = 2;

    @Transactional
    public void match(Long requestId) {

        RideRequest newRequest = rideRequestRepository
                .findByIdForUpdate(requestId)
                .orElseThrow();

        if (newRequest.getRide() != null) {
            log.warn("Request already matched: {}", requestId);
            return;
        }

        String prefix = newRequest.getPickupGeohash().substring(0, 5);

        List<RideRequest> candidates = rideRequestRepository.findAndLockCandidates(prefix, 10);

        log.info("Candidates found: {}", candidates.size());


        for (RideRequest candidate : candidates) {

            if (candidate.getRide() == null) continue;

            Ride ride = rideRepository
                    .findByIdForUpdate(candidate.getRide().getId())
                    .orElse(null);

            if (ride == null || ride.getStatus() != RideStatus.FORMING) continue;

            List<RideRequest> passengers =
                    rideRequestRepository.findByRideId(ride.getId());

            if (canMergeWithRide(ride, passengers, newRequest)) {
                attachToRide(ride, newRequest);
                rideRepository.save(ride);

                log.info("Attached request {} to ride {}", newRequest.getId(), ride.getId());
                return;
            }
        }


        Cab cab = cabRepository
                .findAndLockAvailableCab(CabStatus.AVAILABLE.name())
                .orElse(null);

        if (cab == null) {
            markWaiting(newRequest);
            return;
        }

        int maxSeats = cab.getTotalSeats();
        int maxLuggage = cab.getLuggageCapacity();

        List<RideRequest> waiting = candidates.stream()
                .filter(r -> r.getRide() == null)
                .toList();

        List<RideRequest> group = new ArrayList<>();

        int currentSeats = 0;
        int currentLuggage = 0;

        //  Add new request first
        group.add(newRequest);
        currentSeats += newRequest.getSeatsRequired();
        currentLuggage += newRequest.getLuggageCount();

        for (RideRequest req : waiting) {

            if (req.getId().equals(newRequest.getId())) continue;

            if (canMergeInGroup(group, req,
                    currentSeats, currentLuggage,
                    maxSeats, maxLuggage)) {

                group.add(req);
                currentSeats += req.getSeatsRequired();
                currentLuggage += req.getLuggageCount();
            }
        }


        if (group.size() < MIN_POOL_SIZE) {
            markWaiting(newRequest);
            return;
        }

        createRide(cab, group);
    }


    private boolean canMergeInGroup(List<RideRequest> group,
                                    RideRequest incoming,
                                    int currentSeats,
                                    int currentLuggage,
                                    int maxSeats,
                                    int maxLuggage) {

        int newSeats = currentSeats + incoming.getSeatsRequired();
        int newLuggage = currentLuggage + incoming.getLuggageCount();

        if (newSeats > maxSeats) return false;
        if (newLuggage > maxLuggage) return false;

        if (group.isEmpty()) return true;

        for (RideRequest existing : group) {
            if (!constraintValidator.areRequestsCompatible(existing, incoming)) {
                return false;
            }
        }

        return true;
    }

    private boolean canMergeWithRide(Ride ride,
                                     List<RideRequest> passengers,
                                     RideRequest incoming) {

        if (!constraintValidator.canFitInRide(ride, incoming)) {
            return false;
        }

        for (RideRequest existing : passengers) {
            if (!constraintValidator.areRequestsCompatible(existing, incoming)) {
                return false;
            }
        }

        return true;
    }

    private void createRide(Cab cab, List<RideRequest> group) {

        cab.setStatus(CabStatus.BUSY);
        cabRepository.saveAndFlush(cab);

        Ride ride = Ride.builder()
                .cab(cab)
                .status(RideStatus.FORMING)
                .totalSeatsUsed(0)
                .totalLuggageUsed(0)
                .build();

        rideRepository.save(ride);

        for (RideRequest req : group) {
            attachToRide(ride, req);
        }

        List<RideRequest> passengers = rideRequestRepository.findByRideId(ride.getId());

        int maxEta = rideRequestRepository.findByRideId(ride.getId())
                .stream()
                .mapToInt(r -> routeOptimizationService.estimateTravelTimeMinutes(
                        r.getPickupLat(),
                        r.getPickupLng(),
                        r.getDropLat(),
                        r.getDropLng()
                ))
                .max()
                .orElse(0);

        ride.setEstimatedTotalTime(maxEta);
        ride.transitionTo(RideStatus.CONFIRMED);
        rideRepository.save(ride);


        for (RideRequest req : passengers) {
            pricingService.calculatePrice(ride, req);
        }

        log.info("Ride {} created with {} passengers, ETA {} mins",
                ride.getId(), group.size(), maxEta);
    }

    private void attachToRide(Ride ride, RideRequest request) {
        if (request.getRide() != null) {
            log.warn("Request already matched: {}", request.getId());
            return;
        }

        ride.setTotalSeatsUsed(
                ride.getTotalSeatsUsed() + request.getSeatsRequired()
        );

        ride.setTotalLuggageUsed(
                ride.getTotalLuggageUsed() + request.getLuggageCount()
        );

        request.setRide(ride);
        request.setStatus(RideRequestStatus.MATCHED);

        RidePassenger ridePassenger = RidePassenger.builder()
                .id(new RidePassengerId())
                .passenger(request.getPassenger())
                .ride(ride)
                .build();

        ridePassengerRepository.save(ridePassenger) ;

        rideRequestRepository.save(request);


        log.info("Request {} attached to ride {}", request.getId(), ride.getId());
    }


    private void markWaiting(RideRequest request) {
        request.setStatus(RideRequestStatus.WAITING);
        rideRequestRepository.save(request);

        log.info("Request {} marked as WAITING", request.getId());
    }

}
