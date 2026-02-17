package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.RidePassenger;
import com.nirmit.ride_pooling.entity.RidePassengerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RidePassengerRepository extends JpaRepository<RidePassenger , RidePassengerId> {
    List<RidePassenger> findByRideId(Long rideId) ;
}
