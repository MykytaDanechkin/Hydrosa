# Hydrosa API Documentation

This document describes the endpoints of the Hydrosa main application, including request/response examples and descriptions.

## Base URL
Default: `http://localhost:8080`

---

## Signals (`/api/signals`)

### Get All Signals
- **URL**: `/api/signals`
- **Method**: `GET`
- **Description**: Returns a list of all received signals.
- **Response Example**:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "azimuth": 125.5,
    "station": {
      "id": 1,
      "latitude": 55.200000,
      "longitude": 12.500000,
      "magneticCorrection": 0.0
    },
    "receivedAt": "2024-05-06T12:00:00",
    "strength": 0.85,
    "processed": false
  }
]
```

### Save Signal
- **URL**: `/api/signals`
- **Method**: `POST`
- **Description**: Receives and saves a new signal. `timestamp` is set server-side.
- **Request Example**:
```json
{
  "stationId": 1,
  "azimuth": 125.5,
  "strength": 0.85
}
```
- **Response**: `204 No Content`

### Delete Signal
- **URL**: `/api/signals/{id}`
- **Method**: `DELETE`
- **Description**: Deletes a signal by its UUID.
- **Response**: `204 No Content`

---

## Stations (`/station`)

### Get All Stations
- **URL**: `/station`
- **Method**: `GET`
- **Description**: Returns a list of all monitoring stations.
- **Response Example**:
```json
[
  {
    "id": 1,
    "latitude": 55.200000,
    "longitude": 12.500000,
    "magneticCorrection": 0.0
  }
]
```

### Get Station by ID
- **URL**: `/station/{id}`
- **Method**: `GET`
- **Description**: Returns details of a specific station.
- **Response Example**:
```json
{
  "id": 1,
  "latitude": 55.200000,
  "longitude": 12.500000,
  "magneticCorrection": 0.0
}
```

---

## Tracked Objects (`/trackedObject`)

### Get All Tracked Objects
- **URL**: `/trackedObject`
- **Method**: `GET`
- **Description**: Returns a list of all objects currently being tracked (ships, etc.).
- **Response Example**:
```json
[
  {
    "id": "3cc9c2d0-8f9c-4c0d-b28e-f19b22295874",
    "latitude": 55.123456,
    "longitude": 12.654321,
    "estimatedSpeed": 5.2,
    "estimatedDirection": 90.0,
    "lastSeen": "2024-05-06T12:05:00",
    "firstSeen": "2024-05-06T11:50:00",
    "detectionCount": 15,
    "confidence": 0.92,
    "status": "CONFIRMED"
  }
]
```
- **Statuses**: `ACTIVE`, `LOST`, `CONFIRMED`

---

## Streaming (`/stream`)

### Start Streaming
- **URL**: `/stream/start/{stationId}`
- **Method**: `POST`
- **Description**: Commands the application to start streaming real-time signal data for a specific station via WebSocket.

### Stop Streaming
- **URL**: `/stream/stop/{stationId}`
- **Method**: `POST`
- **Description**: Commands the application to stop streaming data for a specific station.

---

## Visualizations (HTML)

### Station Map
- **URL**: `/{stationId}/map`
- **Method**: `GET`
- **Description**: Returns an interactive Leaflet map showing real-time signals for a specific station.

### Global Map
- **URL**: `/map`
- **Method**: `GET`
- **Description**: Returns an interactive Leaflet map showing all stations and currently tracked objects with their vectors.

---

## WebSocket Real-time Updates
- **Connection URL**: `/ws` (SockJS)
- **Topic**: `/topic/station/{stationId}`
- **Message Format**:
```json
{
  "azimuth": 125.5,
  "strength": 0.85
}
```
