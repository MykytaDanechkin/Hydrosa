# Hydrosa Ecosystem

The Hydrosa ecosystem consists of two main components:
- **Hydrosa**: The main service for tracking and processing water object data.
- **HydrosaSim**: A simulation service that generates water object movements and signals.

## Project Structure

- `Hydrosa/`: Main service (Spring Boot).
- `HydrosaSim/`: Simulation service (Spring Boot).
- `docker-compose.yaml`: Orchestration for local development databases.

## Quick Start (Local)

1. **Start Databases**
   ```bash
   docker compose up -d
   ```
   This will start two PostgreSQL instances:
   - Port `5432`: Database for Hydrosa.
   - Port `5433`: Database for HydrosaSim.

2. **Run Hydrosa (Main Service)**
   ```bash
   cd Hydrosa
   ./gradlew bootRun
   ```
   See [Hydrosa/README.md](Hydrosa/README.md) for details.

3. **Run HydrosaSim (Simulator)**
   ```bash
   cd HydrosaSim
   ./gradlew bootRun
   ```
   See [HydrosaSim/README.md](HydrosaSim/README.md) for details.

## Components

### Hydrosa
Responsible for:
- Tracking water objects and their positions.
- Signal processing and storage.
- Historical data management.
- API for frontend applications.

### HydrosaSim
Responsible for:
- Simulating movements of water objects within a defined bounding box.
- Periodically sending signals to the main Hydrosa service.
- Providing a control API for simulation entities.
