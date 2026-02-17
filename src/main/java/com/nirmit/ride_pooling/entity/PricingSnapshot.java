package com.nirmit.ride_pooling.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id")
    private Ride ride ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    private Double baseFare;
    private Double surgeMultiplier;
    private Double detourPenalty;
    private Double finalPrice;

    private LocalDateTime createdAt;
}
