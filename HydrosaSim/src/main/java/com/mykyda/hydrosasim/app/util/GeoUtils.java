package com.mykyda.hydrosasim.app.util;

public class GeoUtils {

    private static final double EARTH_RADIUS = 6371e3;

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);

        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dPhi/2) * Math.sin(dPhi/2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(dLambda/2) * Math.sin(dLambda/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS * c;
    }

    public static double bearing(double lat1, double lon1, double lat2, double lon2) {

        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double lambda = Math.toRadians(lon2 - lon1);

        double y = Math.sin(lambda) * Math.cos(phi2);
        double x = Math.cos(phi1)*Math.sin(phi2) -
                Math.sin(phi1)*Math.cos(phi2)*Math.cos(lambda);

        double theta = Math.atan2(y, x);

        return (Math.toDegrees(theta) + 360) % 360;
    }


}