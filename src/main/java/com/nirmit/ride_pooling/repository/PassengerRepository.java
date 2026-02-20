package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.Passenger;
import com.nirmit.ride_pooling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger , Long> {
    Optional<Passenger> findByName(String username) ;
}
