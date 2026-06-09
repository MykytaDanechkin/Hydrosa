package com.mykyda.hydrosasim.app.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeoUtilsTest {

    @Test
    void testDistance() {
        // Distance between (0,0) and (0,1)
        // 1 degree longitude at equator is about 111.19 km (using 6371km radius)
        double dist = GeoUtils.distance(0, 0, 0, 1);
        assertEquals(111194.92, dist, 1.0);
    }

    @Test
    void testDistanceSamePoint() {
        double dist = GeoUtils.distance(45, 45, 45, 45);
        assertEquals(0, dist, 0.001);
    }

    @Test
    void testBearing() {
        // From (0,0) to (0,1) should be 90 degrees (East)
        double b = GeoUtils.bearing(0, 0, 0, 1);
        assertEquals(90.0, b, 0.001);

        // From (0,0) to (1,0) should be 0 degrees (North)
        b = GeoUtils.bearing(0, 0, 1, 0);
        assertEquals(0.0, b, 0.001);

        // From (0,0) to (-1,0) should be 180 degrees (South)
        b = GeoUtils.bearing(0, 0, -1, 0);
        assertEquals(180.0, b, 0.001);

        // From (0,0) to (0,-1) should be 270 degrees (West)
        b = GeoUtils.bearing(0, 0, 0, -1);
        assertEquals(270.0, b, 0.001);
    }
}
