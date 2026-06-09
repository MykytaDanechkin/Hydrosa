package com.mykyda.hydrosasim.app.controller;

import com.mykyda.hydrosasim.app.DTO.WaterObjectDTO;
import com.mykyda.hydrosasim.app.data.entity.Station;
import com.mykyda.hydrosasim.app.data.entity.WaterObject;
import com.mykyda.hydrosasim.app.service.StationService;
import com.mykyda.hydrosasim.app.service.WaterObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/objects")
@RequiredArgsConstructor
public class WaterObjectController {

    private final WaterObjectService waterObjectService;

    private final StationService stationService;

    @GetMapping("/{id}")
    private WaterObject getAllObjectById(@PathVariable UUID id){
        return waterObjectService.getObject(id);
    }

    @PostMapping("/gen")
    public UUID genSignal(){
        return waterObjectService.generateRandomObject();
    }

    @GetMapping
    public List<WaterObjectDTO> getAll() {
        return waterObjectService.getAllObjects().stream()
                .map(obj -> new WaterObjectDTO(
                        obj.getId(),
                        obj.getLatitude().doubleValue(),
                        obj.getLongitude().doubleValue()
                ))
                .toList();
    }

    @GetMapping(value = "/map", produces = "text/html")
    public String getMap() {

        var stations = stationService.getAll();

        StringBuilder stationMarkers = getStringBuilder(stations);

        return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8"/>
        <title>Water Objects</title>

        <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css"/>
        <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
    </head>

    <body>
        <div id="map" style="height:100vh;"></div>

        <script>
            var map = L.map('map').setView([56.0, 12.5], 8);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 18
            }).addTo(map);

            var markers = {};

            %s

            function updateObjects() {
                fetch('/objects')
                    .then(res => res.json())
                    .then(data => {

                        data.forEach(obj => {

                            var lat = obj.latitude;
                            var lon = obj.longitude;

                            if (markers[obj.id]) {
                                markers[obj.id].setLatLng([lat, lon]);
                            } else {
                                markers[obj.id] = L.marker([lat, lon])
                                    .addTo(map)
                                    .bindPopup("Object " + obj.id);
                            }
                        });
                    });
            }

            updateObjects();
            setInterval(updateObjects, 2000);

        </script>
    </body>
    </html>
    """.formatted(stationMarkers.toString());
    }

    private static @NonNull StringBuilder getStringBuilder(List<Station> stations) {
        StringBuilder stationMarkers = new StringBuilder();

        for (var s : stations) {
            stationMarkers.append(String.format("""
            L.circleMarker([%f, %f], {
                radius: 8,
                color: 'red',
                fillColor: 'red',
                fillOpacity: 1
            }).addTo(map)
              .bindPopup("Station %d");
        """,
                    s.getLatitude().doubleValue(),
                    s.getLongitude().doubleValue(),
                    s.getId()
            ));
        }
        return stationMarkers;
    }
}
