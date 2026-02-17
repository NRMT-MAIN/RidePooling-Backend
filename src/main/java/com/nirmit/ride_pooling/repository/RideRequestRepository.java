package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest , Long> {
    @Query( value = """
        SELECT *
        FROM RIDE_REQUESTS
        WHERE STATUS='WAITING'
        AND PICKUP_GEOHASH LIKE CONCAT(:prefix , '%')
        ORDER BY REQUEST_TIMESTAMP
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
       """
    , nativeQuery = true)
    List<RideRequest> findAndLockCandidates(
            @Param("prefix") String prefix ,
            @Param("limit") int limit
    ) ;

    List<RideRequest> findByRideId(Long rideId);
}
