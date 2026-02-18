package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.Cab;
import com.nirmit.ride_pooling.entity.CabStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CabRepository extends JpaRepository<Cab , Long> {
    List<Cab> findByStatus(CabStatus status) ;

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cab c WHERE c.id = :id")
    Optional<Cab> findByIdForUpdate(@Param("id") Long id);
}
