package com.nirmit.ride_pooling.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RideRequestCreatedEvent {
    private Long rideRequestId ;
}
