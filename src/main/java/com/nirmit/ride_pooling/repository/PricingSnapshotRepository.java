package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.PricingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PricingSnapshotRepository extends JpaRepository<PricingSnapshot , Long> {
    Optional<PricingSnapshot> findTopByPassengerIdAndRideIdOrderByCreatedAtDesc(Long passengerId, Long rideId);

    @Query("""
        SELECT p.finalPrice
        FROM PricingSnapshot p
        WHERE p.ride.id = :rideId
          AND p.passenger.id = :passengerId
        ORDER BY p.createdAt DESC
        LIMIT 1
    """)
    Double findFinalPriceByRideIdAndPassengerId(Long rideId , Long passengerId) ;
}
