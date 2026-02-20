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

    public boolean isDetourAcceptable(RideRequest e, RideRequest i) {

        int directE = estimateTravelTimeMinutes(
                e.getPickupLat(), e.getPickupLng(),
                e.getDropLat(), e.getDropLng()
        );

        int directI = estimateTravelTimeMinutes(
                i.getPickupLat(), i.getPickupLng(),
                i.getDropLat(), i.getDropLng()
        );

        int option1 = routeTime(
                e.getPickupLat(), e.getPickupLng(),
                i.getPickupLat(), i.getPickupLng(),
                e.getDropLat(), e.getDropLng(),
                i.getDropLat(), i.getDropLng()
        );

        int option2 = routeTime(
                i.getPickupLat(), i.getPickupLng(),
                e.getPickupLat(), e.getPickupLng(),
                i.getDropLat(), i.getDropLng(),
                e.getDropLat(), e.getDropLng()
        );

        int sharedTime = Math.min(option1, option2);

        int detourE = sharedTime - directE;
        int detourI = sharedTime - directI;

        return detourE <= e.getMaxDetourMinutes()
                &&
                detourI <= i.getMaxDetourMinutes();
    }

    private int routeTime(double lat1, double lng1,
                          double lat2, double lng2,
                          double lat3, double lng3,
                          double lat4, double lng4) {

        return estimateTravelTimeMinutes(lat1, lng1, lat2, lng2)
                + estimateTravelTimeMinutes(lat2, lng2, lat3, lng3)
                + estimateTravelTimeMinutes(lat3, lng3, lat4, lng4);
    }
}
