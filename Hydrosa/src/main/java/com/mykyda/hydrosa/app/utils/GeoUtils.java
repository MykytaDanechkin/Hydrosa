package com.mykyda.hydrosa.app.utils;

public class GeoUtils {

    private static final double EARTH_RADIUS = 6371e3;

    public static double[] intersect(
            double lat1, double lon1, double az1,
            double lat2, double lon2, double az2
    ) {
        double phi1 = Math.toRadians(lat1);
        double lambda1 = Math.toRadians(lon1);
        double phi2 = Math.toRadians(lat2);
        double lambda2 = Math.toRadians(lon2);
        double theta1 = Math.toRadians(az1);
        double theta2 = Math.toRadians(az2);

        double dPhi = phi2 - phi1;
        double dLambda = lambda2 - lambda1;

        double dist12 = 2 * Math.asin(Math.sqrt(
                Math.sin(dPhi / 2) * Math.sin(dPhi / 2) +
                        Math.cos(phi1) * Math.cos(phi2) *
                                Math.sin(dLambda / 2) * Math.sin(dLambda / 2)
        ));

        if (Math.abs(dist12) < 1e-10) return null;

        double cosBrgA = (Math.sin(phi2) - Math.sin(phi1) * Math.cos(dist12))
                / (Math.sin(dist12) * Math.cos(phi1));
        double cosBrgB = (Math.sin(phi1) - Math.sin(phi2) * Math.cos(dist12))
                / (Math.sin(dist12) * Math.cos(phi2));

        double brgA = Math.acos(Math.clamp(cosBrgA, -1, 1));
        double brgB = Math.acos(Math.clamp(cosBrgB, -1, 1));

        double brg12, brg21;
        if (Math.sin(dLambda) > 0) {
            brg12 = brgA;
            brg21 = 2 * Math.PI - brgB;
        } else {
            brg12 = 2 * Math.PI - brgA;
            brg21 = brgB;
        }

        double alpha1 = theta1 - brg12;
        double alpha2 = brg21 - theta2;

        if (Math.abs(Math.sin(alpha1)) < 1e-10 && Math.abs(Math.sin(alpha2)) < 1e-10)
            return null;
        if (Math.sin(alpha1) * Math.sin(alpha2) < 0)
            return null;

        double alpha3 = Math.acos(
                -Math.cos(alpha1) * Math.cos(alpha2) +
                        Math.sin(alpha1) * Math.sin(alpha2) * Math.cos(dist12)
        );

        double dist13 = Math.atan2(
                Math.sin(dist12) * Math.sin(alpha1) * Math.sin(alpha2),
                Math.cos(alpha2) + Math.cos(alpha1) * Math.cos(alpha3)
        );

        if (dist13 < 0) return null;

        double phi3 = Math.asin(Math.clamp(
                Math.sin(phi1) * Math.cos(dist13) +
                        Math.cos(phi1) * Math.sin(dist13) * Math.cos(theta1)
                , -1, 1));

        double dLon13 = Math.atan2(
                Math.sin(theta1) * Math.sin(dist13) * Math.cos(phi1),
                Math.cos(dist13) - Math.sin(phi1) * Math.sin(phi3)
        );

        double lambda3 = lambda1 + dLon13;

        return new double[]{Math.toDegrees(phi3), Math.toDegrees(lambda3)};
    }

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
