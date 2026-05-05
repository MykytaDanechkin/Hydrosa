package com.mykyda.hydrosa;

import com.mykyda.hydrosa.app.data.entity.Signal;
import com.mykyda.hydrosa.app.data.entity.Station;
import com.mykyda.hydrosa.app.data.entity.TrackedObject;
import com.mykyda.hydrosa.app.data.repository.SignalRepository;
import com.mykyda.hydrosa.app.data.repository.StationRepository;
import com.mykyda.hydrosa.app.data.repository.TrackedObjectRepository;
import com.mykyda.hydrosa.app.service.LocatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LocatorIntegrationTest {

    @Autowired
    private LocatorService locatorService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private TrackedObjectRepository trackedObjectRepository;

    @Test
    @Transactional
    void testTrackingWithTwoStations() {
        signalRepository.deleteAll();
        trackedObjectRepository.deleteAll();
        stationRepository.deleteAll();

        // 1. Setup two stations
        Station s1 = Station.builder()
                .latitude(BigDecimal.valueOf(50.0))
                .longitude(BigDecimal.valueOf(30.0))
                .build();
        s1 = stationRepository.saveAndFlush(s1);

        Station s2 = Station.builder()
                .latitude(BigDecimal.valueOf(50.0))
                .longitude(BigDecimal.valueOf(30.1))
                .build();
        s2 = stationRepository.saveAndFlush(s2);

        // 2. Create signals that should intersect
        signalRepository.saveAndFlush(Signal.builder()
                .station(s1)
                .azimuth(45.0)
                .strength(1.0)
                .processed(false)
                .build());

        signalRepository.saveAndFlush(Signal.builder()
                .station(s2)
                .azimuth(315.0)
                .strength(1.0)
                .processed(false)
                .build());

        // 3. Run locator
        locatorService.locateSignal();

        // 4. Verify tracked object created
        List<TrackedObject> trackedObjects = trackedObjectRepository.findAll();
        assertFalse(trackedObjects.isEmpty(), "Should have created at least one tracked object");
    }
}
