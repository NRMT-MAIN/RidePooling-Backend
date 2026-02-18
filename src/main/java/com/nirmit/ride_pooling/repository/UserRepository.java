package com.nirmit.ride_pooling.repository;

import com.nirmit.ride_pooling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username) ;
}
