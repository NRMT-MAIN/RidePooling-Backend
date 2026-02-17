package com.nirmit.ride_pooling.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ride_passengers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RidePassenger {
    @EmbeddedId
    private RidePassengerId id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rideId")
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("passengerId")
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    private Integer pickupOrder;
    private Integer dropOrder;
}
