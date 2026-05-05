package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.DTO.demo.SignalViewDTO;
import com.mykyda.hydrosa.app.service.SignalService;
import com.mykyda.hydrosa.app.service.StationService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HydroController {

    private final SignalService signalService;

    private final StationService stationService;

    @GetMapping("/{stationId}/map")
    public String getMap(@PathVariable Long stationId) {

        var signals = signalService.getAllByStationId(stationId);
        var station = stationService.getById(stationId);

        double lat = station.getLatitude().doubleValue();
        double lon = station.getLongitude().doubleValue();

        StringBuilder jsLines = getStringBuilder(signals, lat, lon);

        String stationMarker = String.format("""
                    L.marker([%f, %f]).addTo(map)
                        .bindPopup("Station %d")
                        .openPopup();
                """, lat, lon, stationId);

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8" />
                    <title>Signals Map</title>
                
                    <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css" />
                    <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
                
                    <script src="https://cdn.jsdelivr.net/npm/sockjs-client/dist/sockjs.min.js"></script>
                    <script src="https://cdn.jsdelivr.net/npm/stompjs/lib/stomp.min.js"></script>
                </head>
                <body>
                
                    <div id="map" style="height: 100vh;"></div>
                
                    <script>
                
                        var lat = %f;
                        var lon = %f;
                        var stationId = %d;
                
                        var map = L.map('map').setView([lat, lon], 10);
                
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            maxZoom: 18
                        }).addTo(map);
                
                        %s
                        %s
                
                        var socket = new SockJS('/ws');
                        var stompClient = Stomp.over(socket);
                
                        stompClient.connect({}, function () {
                
                            fetch('/stream/start/' + stationId, { method: 'POST' });
                
                            stompClient.subscribe('/topic/station/' + stationId, function (message) {
                
                                var signal = JSON.parse(message.body);
                
                                var length = 0.1 + signal.strength * 0.2;
                                var rad = signal.azimuth * Math.PI / 180;
                
                                var endLat = lat + length * Math.cos(rad);
                                var endLon = lon + length * Math.sin(rad);
                
                                L.polyline([[lat, lon], [endLat, endLon]], {
                                    color: 'blue',
                                    weight: 2,
                                    opacity: signal.strength
                                }).addTo(map);
                            });
                        });
                
                        window.addEventListener("beforeunload", function () {
                            fetch('/stream/stop/' + stationId, { method: 'POST' });
                        });
                
                    </script>
                
                </body>
                </html>
                """.formatted(
                lat, lon, stationId,
                jsLines.toString(),
                stationMarker
        );
    }

    private static @NonNull StringBuilder getStringBuilder(List<SignalViewDTO> signals, double lat, double lon) {
        StringBuilder jsLines = new StringBuilder();

        for (var s : signals) {

            double length = 0.1 + s.strength() * 0.2;
            double rad = Math.toRadians(s.azimuth());

            double endLat = lat + length * Math.cos(rad);
            double endLon = lon + length * Math.sin(rad);

            jsLines.append(String.format("""
                        L.polyline([[%f, %f], [%f, %f]], {
                            color: 'red',
                            weight: 2,
                            opacity: %f
                        }).addTo(map);
                    """, lat, lon, endLat, endLon, s.strength()));
        }
        return jsLines;
    }


    @GetMapping("/map")
    public String getGlobalMap() {

        var stations = stationService.getAll();

        StringBuilder stationMarkers = new StringBuilder();
        for (var s : stations) {
            double lat = s.getLatitude().doubleValue();
            double lon = s.getLongitude().doubleValue();
            stationMarkers.append("""
            L.circleMarker([%f, %f], { color: 'red', radius: 6 })
              .addTo(map).bindPopup("Station %d");
        """.formatted(lat, lon, s.getId()));
        }

        String html = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Global Map</title>
    <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
</head>
<body>
<div id="map" style="height: 100vh;"></div>
<script>
    var map = L.map('map').setView([55.15, 12.6], 11);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18 }).addTo(map);

    STATION_MARKERS

    var objectLayers = [];

    function updateObjects() {
        fetch('/trackedObject')
            .then(res => res.json())
            .then(data => {
                objectLayers.forEach(l => map.removeLayer(l));
                objectLayers = [];

                data.forEach(obj => {
                    if (obj.status === 'LOST') return;

                    var color = obj.status === 'CONFIRMED' ? 'blue' : 'orange';

                    var circle = L.circleMarker([obj.latitude, obj.longitude], {
                        color: color,
                        radius: 6 + (obj.confidence * 4),
                        fillOpacity: 0.6
                    }).addTo(map);

                    var speed = obj.estimatedSpeed    ? obj.estimatedSpeed.toFixed(2) + ' m/s' : 'unknown';
                    var dir   = obj.estimatedDirection ? obj.estimatedDirection.toFixed(1) + ' deg' : 'unknown';
                    var conf  = (obj.confidence * 100).toFixed(0) + '%';

                    circle.bindPopup(
                        '<b>' + obj.status + '</b><br>' +
                        'Speed: ' + speed + '<br>' +
                        'Direction: ' + dir + '<br>' +
                        'Confidence: ' + conf + '<br>' +
                        'Detections: ' + obj.detectionCount
                    );
                    objectLayers.push(circle);

                    if (obj.estimatedSpeed && obj.estimatedDirection) {
                        var rad  = obj.estimatedDirection * Math.PI / 180;
                        var len  = obj.estimatedSpeed * 30;
                        var dLat = (len * Math.cos(rad)) / 111320;
                        var dLon = (len * Math.sin(rad)) / (111320 * Math.cos(obj.latitude * Math.PI / 180));

                        var arrow = L.polyline(
                            [[obj.latitude, obj.longitude],
                             [obj.latitude + dLat, obj.longitude + dLon]],
                            { color: color, weight: 2, opacity: 0.8 }
                        ).addTo(map);
                        objectLayers.push(arrow);
                    }
                });
            });
    }

    updateObjects();
    setInterval(updateObjects, 1000);
</script>
</body>
</html>
""";

        return html.replace("STATION_MARKERS", stationMarkers.toString());
    }
}