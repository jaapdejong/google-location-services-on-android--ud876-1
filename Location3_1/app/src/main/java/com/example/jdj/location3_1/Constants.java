package com.example.jdj.location3_1;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class Constants {

    private Constants() {
    }

    public static final HashMap<String, LatLng> JDJ_LANDMARKS = new HashMap<>();

    public static final float GEOFENCE_RADIUS_IN_METERS = 1000;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 8 * 60 * 60 * 1000; // 8 hours

    static {
        // Nedap
        JDJ_LANDMARKS.put("Nedap", new LatLng(52.0413186, 6.6146727));

        // Thuis
        JDJ_LANDMARKS.put("Home", new LatLng(52.0942468, 6.4227689));

        // Hambroek
        JDJ_LANDMARKS.put("Hambroek", new LatLng(52.1049305, 6.5295949));

        // Overbiel
        JDJ_LANDMARKS.put("Overbiel", new LatLng(52.1124892, 6.5829024));

    }

}
