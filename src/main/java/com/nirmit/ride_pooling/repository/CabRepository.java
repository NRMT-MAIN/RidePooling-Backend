package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.Cab;
import com.nirmit.ride_pooling.entity.CabStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CabRepository extends JpaRepository<Cab , Long> {
    List<Cab> findByStatus(CabStatus status) ;
}
