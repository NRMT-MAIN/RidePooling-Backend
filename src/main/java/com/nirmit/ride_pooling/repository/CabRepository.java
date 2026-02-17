package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.Cab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CabRepository extends JpaRepository<Cab , Long> {
}
