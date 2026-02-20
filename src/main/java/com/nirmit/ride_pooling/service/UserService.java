package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.dto.*;
import com.nirmit.ride_pooling.entity.Passenger;
import com.nirmit.ride_pooling.entity.Role;
import com.nirmit.ride_pooling.entity.User;
import com.nirmit.ride_pooling.repository.PassengerRepository;
import com.nirmit.ride_pooling.repository.UserRepository;
import com.nirmit.ride_pooling.utils.JWTUtil;
import com.nirmit.ride_pooling.utils.exceptions.InvalidStateException;
import com.nirmit.ride_pooling.utils.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository ;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil ;

    @Transactional
    public RegisterResponsePassengerDTO registerPassenger(RegisterDTO dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.PASSENGER)
                .build();

        Passenger passenger = Passenger.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .build();

        userRepository.save(user);
        passengerRepository.save(passenger) ;

        return RegisterResponsePassengerDTO.builder()
                .passengerId(passenger.getId())
                .name(dto.getName())
                .message("Passenger Registered Succesfully!")
                .build();
    }

    public RegisterResponseDriverDTO registerDriver(RegisterDTO dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.DRIVER)
                .build();

        userRepository.save(user);

        return RegisterResponseDriverDTO.builder()
                .driverId(user.getId())
                .name(dto.getName())
                .message("Driver Registered Succesfully!")
                .build();
    }

    public LoginResponseDTO login(LoginDTO dto) {
        User user = userRepository
                .findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Username not found!"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.warn("Wrong Password for username : " + dto.getUsername());
            throw new InvalidStateException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name()) ;

        log.info("Token generated for user : " + user.getUsername());
        return LoginResponseDTO.builder()
                .username(dto.getUsername())
                .message("Hello " + dto.getUsername() + "!")
                .token(token)
                .build();
    }
}
