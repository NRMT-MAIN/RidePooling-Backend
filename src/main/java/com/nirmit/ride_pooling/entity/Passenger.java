package com.nirmit.ride_pooling.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "passengers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(nullable = false)
    private String name ;

    @Column(unique = true , nullable = false)
    private String phone;

    private LocalDateTime createdAt ;

    @OneToMany(mappedBy = "passenger" , fetch = FetchType.LAZY)
    private List<RideRequest> rideRequests;
}
