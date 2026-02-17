package com.nirmit.ride_pooling.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "cabs",
        indexes = {
        @Index(name = "idx_cab_geohash", columnList = "currentGeohash") ,
        @Index(name = "idx_cab_status", columnList = "status")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private String driverName ;

    @Column(nullable = false)
    private Integer totalSeats;
    @Column(nullable = false)
    private Integer luggageCapacity;

    @Column(nullable = false)
    private Double currentLat ;
    @Column(nullable = false)
    private Double currentLng ;
    @Column(nullable = false)
    private String currentGeohash ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CabStatus status ;

    @Version
    private Long version ;

    @OneToMany(mappedBy = "cab" , fetch = FetchType.LAZY)
    private List<Ride> rides ;
}
