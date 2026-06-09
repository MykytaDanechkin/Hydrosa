package com.mykyda.hydrosa.app.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GeoUtilsTest {

    @Test
    void testDistance() {
        double dist = GeoUtils.distance(50.0, 30.0, 50.1, 30.0);
        assertEquals(11119, dist, 50);
    }

    @Test
    void testBearing() {
        double brg = GeoUtils.bearing(50.0, 30.0, 51.0, 30.0);
        assertEquals(0.0, brg, 0.001);

        brg = GeoUtils.bearing(50.0, 30.0, 50.0, 31.0);
        assertEquals(90.0, brg, 1.0);
    }
}
