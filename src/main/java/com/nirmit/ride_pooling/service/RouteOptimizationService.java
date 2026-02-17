package com.nirmit.ride_pooling.service;

import com.nirmit.ride_pooling.entity.RideRequest;
import com.nirmit.ride_pooling.utils.DistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteOptimizationService {
    private final DistanceUtils distanceUtil;

    private static final double AVG_SPEED_KMPH = 40.0;

    public int estimateTravelTimeMinutes(double lat1, double lng1,
                                         double lat2, double lng2) {

        double distanceKm = distanceUtil.calculateDistance(lat1, lng1, lat2, lng2);
        double hours = distanceKm / AVG_SPEED_KMPH;
        return (int) (hours * 60);
    }

    public boolean isDetourAcceptable(RideRequest existing,
                                      RideRequest incoming) {

        int directTimeExisting = estimateTravelTimeMinutes(
                existing.getPickupLat(), existing.getPickupLng(),
                existing.getDropLat(), existing.getDropLng()
        );

        int directTimeIncoming = estimateTravelTimeMinutes(
                incoming.getPickupLat(), incoming.getPickupLng(),
                incoming.getDropLat(), incoming.getDropLng()
        );

        int combinedTime = directTimeExisting + directTimeIncoming;

        return combinedTime <=
                (directTimeExisting + existing.getMaxDetourMinutes())
                &&
                combinedTime <=
                        (directTimeIncoming + incoming.getMaxDetourMinutes());
    }
}
