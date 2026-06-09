# HydrosaSim

HydrosaSim is a simulation service for the Hydrosa ecosystem, designed to simulate water objects and signal transmissions.

## Prerequisites

- **Java 21** or higher
- **PostgreSQL** (running locally or in a container)
- **Docker** (optional, for containerized deployment)

## Getting Started

### Local Setup (Development)

1. **Database Configuration**
   By default, the application expects a PostgreSQL database running at `localhost:5433` with the following credentials:
   - **Username**: `postgres`
   - **Password**: `postgres`
   - **Database**: `postgres`

   You can override these settings using environment variables (see [Configuration](#configuration)).

2. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd HydrosaSim
   ```

3. **Build and Run with Gradle**
   ```bash
   ./gradlew bootRun
   ```

4. **Build and Run JAR**
   ```bash
   ./gradlew build
   java -jar build/libs/HydrosaSim-0.0.1-SNAPSHOT.jar
   ```

### Running with Docker

1. **Build the Image**
   ```bash
   docker build -t hydrosasim .
   ```

2. **Run the Container**
   ```bash
   docker run -p 8081:8081 \
     -e DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/postgres \
     -e DATASOURCE_USERNAME=postgres \
     -e DATASOURCE_PASSWORD=postgres \
     hydrosasim
   ```
   *Note: Use `host.docker.internal` to connect to a database running on your host machine from within the container.*

## Configuration

The application can be configured using environment variables or an `.env` file (via `spring.config.import`).

| Environment Variable | Description | Default Value |
|----------------------|-------------|---------------|
| `SERVER_PORT` | Port the simulator runs on | `8081` |
| `DATASOURCE_URL` | JDBC URL for PostgreSQL | `jdbc:postgresql://localhost:5433/postgres` |
| `DATASOURCE_USERNAME`| Database username | `postgres` |
| `DATASOURCE_PASSWORD`| Database password | `postgres` |
| `HYDROSA_URL` | URL of the main Hydrosa service | `http://localhost:8080` |
| `MOVE_FREQUENCY_MILLISECONDS` | How often objects move | `1000` |
| `SIGNAL_FREQUENCY_MILLISECONDS` | How often signals are sent | `2000` |

## Deployment (Remote)

To deploy HydrosaSim remotely:

1. **Container Registry**: Push the Docker image to a registry (e.g., Docker Hub, GHCR).
   ```bash
   docker tag hydrosasim your-registry/hydrosasim:latest
   docker push your-registry/hydrosasim:latest
   ```

2. **Environment**: Set the required environment variables on your remote server or orchestration platform (Kubernetes, AWS ECS, etc.).

3. **Database**: Ensure the remote PostgreSQL instance is reachable and the `DATASOURCE_URL` is correctly configured.

## Simulation Parameters

You can adjust the simulation area (bounding box) using the following variables:
- `DOT_NORTHWEST_LAT`, `DOT_NORTHWEST_LON`
- `DOT_NORTHEAST_LAT`, `DOT_NORTHEAST_LON`
- `DOT_SOUTHWEST_LAT`, `DOT_SOUTHWEST_LON`
- `DOT_SOUTHEAST_LAT`, `DOT_SOUTHEAST_LON`
