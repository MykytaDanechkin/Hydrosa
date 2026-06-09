# Hydrosa

Hydrosa is the main service of the Hydrosa ecosystem, responsible for tracking water objects, processing signals, and managing object history.

## Prerequisites

- **Java 21** or higher
- **PostgreSQL** (running locally or in a container)
- **Docker** (optional, for containerized deployment)

## Getting Started

### Local Setup (Development)

1. **Database Configuration**
   By default, the application expects a PostgreSQL database running at `localhost:5432` with the following credentials:
   - **Username**: `postgres`
   - **Password**: `postgres`
   - **Database**: `postgres`

   You can use the `docker-compose.yaml` in the root directory to start the database:
   ```bash
   docker compose up hydrosa_db -d
   ```

2. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd Hydrosa
   ```

3. **Build and Run with Gradle**
   ```bash
   ./gradlew bootRun
   ```

4. **Build and Run JAR**
   ```bash
   ./gradlew build
   java -jar build/libs/Hydrosa-0.0.1-SNAPSHOT.jar
   ```

### Running with Docker

1. **Build the Image**
   ```bash
   docker build -t hydrosa .
   ```

2. **Run the Container**
   ```bash
   docker run -p 8080:8080 \
     -e DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/postgres \
     -e DATASOURCE_USERNAME=postgres \
     -e DATASOURCE_PASSWORD=postgres \
     hydrosa
   ```
   *Note: Use `host.docker.internal` to connect to a database running on your host machine from within the container.*

## Configuration

The application can be configured using environment variables or an `.env` file (via `spring.config.import`).

| Environment Variable | Description | Default Value |
|----------------------|-------------|---------------|
| `SERVER_PORT` | Port the service runs on | `8080` |
| `DATASOURCE_URL` | JDBC URL for PostgreSQL | `jdbc:postgresql://localhost:5432/postgres` |
| `DATASOURCE_USERNAME`| Database username | `postgres` |
| `DATASOURCE_PASSWORD`| Database password | `postgres` |
| `ALLOWED_ORIGINS` | CORS allowed origins | `http://localhost:3000` |

## API Documentation

For detailed information about the API endpoints, see [HYDROSA_API.md](HYDROSA_API.md).

## Deployment (Remote)

To deploy Hydrosa remotely:

1. **Container Registry**: Push the Docker image to a registry.
   ```bash
   docker tag hydrosa your-registry/hydrosa:latest
   docker push your-registry/hydrosa:latest
   ```

2. **Environment**: Set the required environment variables on your remote server.

3. **Database**: Ensure the remote PostgreSQL instance is reachable and the `DATASOURCE_URL` is correctly configured.
