package com.nirmit.ride_pooling.utils;

import ch.hsr.geohash.GeoHash;

public class GeohashUtils {
    public static String encode(double lat , double lng , int precision) {
        return GeoHash.geoHashStringWithCharacterPrecision(lat , lng , precision) ;
    }
}
