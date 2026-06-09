package com.mykyda.hydrosa.app.utils;

public class GeoUtils {

    private static final double EARTH_RADIUS = 6371e3;

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);

        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(dLambda / 2) * Math.sin(dLambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public static double[] destinationPoint(double lat, double lon, double bearingDeg, double distanceMeters) {
        double R = 6_371_000;
        double delta = distanceMeters / R;
        double theta = Math.toRadians(bearingDeg);
        double phi1 = Math.toRadians(lat);
        double lam1 = Math.toRadians(lon);

        double phi2 = Math.asin(
                Math.sin(phi1) * Math.cos(delta) +
                        Math.cos(phi1) * Math.sin(delta) * Math.cos(theta)
        );
        double lam2 = lam1 + Math.atan2(
                Math.sin(theta) * Math.sin(delta) * Math.cos(phi1),
                Math.cos(delta) - Math.sin(phi1) * Math.sin(phi2)
        );

        return new double[]{Math.toDegrees(phi2), Math.toDegrees(lam2)};
    }

    public static double bearing(double lat1, double lon1, double lat2, double lon2) {

        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double lambda = Math.toRadians(lon2 - lon1);

        double y = Math.sin(lambda) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2) -
                Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda);

        double theta = Math.atan2(y, x);

        return (Math.toDegrees(theta) + 360) % 360;
    }
}
