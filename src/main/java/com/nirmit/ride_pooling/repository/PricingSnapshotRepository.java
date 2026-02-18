package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.PricingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PricingSnapshotRepository extends JpaRepository<PricingSnapshot , Long> {
    Optional<PricingSnapshot> findTopByPassengerIdAndRideIdOrderByCreatedAtDesc(Long passengerId, Long rideId);
}
