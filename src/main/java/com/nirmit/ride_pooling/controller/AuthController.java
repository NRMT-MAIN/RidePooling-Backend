package com.nirmit.ride_pooling.controller;

import com.nirmit.ride_pooling.dto.LoginDTO;
import com.nirmit.ride_pooling.dto.LoginResponseDTO;
import com.nirmit.ride_pooling.dto.RegisterDTO;
import com.nirmit.ride_pooling.entity.Role;
import com.nirmit.ride_pooling.entity.User;
import com.nirmit.ride_pooling.repository.UserRepository;
import com.nirmit.ride_pooling.utils.JWTUtil;
import com.nirmit.ride_pooling.utils.exceptions.InvalidStateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @PostMapping("/cab/register")
    public String registerDriver(@RequestBody RegisterDTO dto) {

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.DRIVER)
                .build();

        userRepository.save(user);
        log.info("Driver register successfully with id : " + user.getId());
        return "Driver registered successfully";
    }

    @PostMapping("/passenger/register")
    public String registerPassenger(@RequestBody RegisterDTO dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.PASSENGER)
                .build();

        userRepository.save(user);

        log.info("Passenger register successfully with id : " + user.getId());
        return "User registered successfully";
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO dto) {
        User user = userRepository
                .findByUsername(dto.getUsername())
                .orElseThrow();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.warn("Wrong Password for username : " + dto.getUsername());
            throw new InvalidStateException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user) ;

        LoginResponseDTO response = LoginResponseDTO.builder()
                .username(dto.getUsername())
                .message("Hello " + dto.getUsername() + "!")
                .token(token)
                .build();
        return new ResponseEntity<>(response , HttpStatus.ACCEPTED);
    }
}

