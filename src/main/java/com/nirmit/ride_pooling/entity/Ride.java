package com.nirmit.ride_pooling.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "rides",
        indexes = {
             @Index(name = "idx_ride_status", columnList = "status")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cab_id" , nullable = false)
    private Cab cab ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status ;

    private Integer totalSeatsUsed ;
    private Integer totalLuggageUsed ;
    private Integer estimatedTotalTime ;

    @Version
    private Long version;

    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY)
    private List<RideRequest> rideRequests;

    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY)
    private List<RidePassenger> passengers ;
}
