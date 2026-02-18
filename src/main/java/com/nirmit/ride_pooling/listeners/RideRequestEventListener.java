package com.nirmit.ride_pooling.listeners;

import com.nirmit.ride_pooling.entity.RideRequest;
import com.nirmit.ride_pooling.event.RideRequestCreatedEvent;
import com.nirmit.ride_pooling.repository.RideRequestRepository;
import com.nirmit.ride_pooling.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RideRequestEventListener {
    private final MatchingService matchingService ;
    private final RideRequestRepository rideRequestRepository ;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("matchingExecutor")
    public void handleRideRequestCreated(RideRequestCreatedEvent requestCreatedEvent) {
        RideRequest request = rideRequestRepository.findById(requestCreatedEvent.getRideRequestId())
                .orElseThrow() ;

        matchingService.match(request);
    }
}
