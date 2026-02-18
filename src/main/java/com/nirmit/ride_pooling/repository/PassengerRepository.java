package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger , Long> {
}
