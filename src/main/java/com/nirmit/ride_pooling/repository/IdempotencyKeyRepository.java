package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey , Long> {
    Optional<IdempotencyKey> findByIdempotencyKey(String key);
}
