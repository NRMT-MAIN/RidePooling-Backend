package com.nirmit.ride_pooling.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride_requests" ,
    indexes = {
        @Index(name = "idx_ride_id", columnList = "ride_id") ,
        @Index(name = "idx_status_geohash", columnList = "status,pickupGeohash")
    }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RideRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id" , nullable = false)
    private Passenger passenger;

    @Column(nullable = false)
    private Double pickupLat ;
    @Column(nullable = false)
    private Double pickupLng ;
    @Column(nullable = false)
    private String pickupGeohash ;

    @Column(nullable = false)
    private Double dropLat ;
    @Column(nullable = false)
    private Double dropLng ;
    @Column(nullable = false)
    private String dropGeohash ;

    private Integer seatsRequired ;
    private Integer luggageCount ;
    @Column(nullable = false)
    private Integer maxDetourMinutes ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideRequestStatus status ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id")
    private Ride ride;

    private LocalDateTime requestTimestamp ;

    @Version
    private Long version;
}
