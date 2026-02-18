package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.Cancellation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancellationRepository extends JpaRepository<Cancellation , Long> {
}
