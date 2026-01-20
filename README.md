# Microservices-Based Monitoring and Decision System for Airport Safety

A distributed safety-critical system for monitoring and responding to dangerous situations in airport fuel station areas using a microservices architecture.

**Authors:** João Gabriel Buttow Albuquerque and Fatine Azzabi  
**Institution:** INSA Toulouse

---

## Project Overview

This project models a safety-critical airport system using a microservices architecture. The scenario focuses on the fuel station area of an airport, where dangerous situations such as gas leaks or fires may occur. The system is designed to detect abnormal events and trigger an immediate emergency response at the fire station.

The architecture comprises six independent Spring Boot microservices, all created with Maven, that communicate through REST APIs. Each service is autonomous and handles a specific responsibility, demonstrating a realistic distributed system where:

- **Sensors are autonomous** – each maintains its own state
- **Decisions are centralized** – the Decision Engine coordinates all logic
- **Actuators are passive** – they respond to commands from the Decision Engine
- **Persistence is isolated** – the History Service handles all data storage
- **Communication is RESTful** – all services interact via HTTP REST APIs

---

## Architecture

The system consists of six microservices:

### 1. Gas Sensor Service
- **Port:** 8081
- **Purpose:** Maintains the current gas level as a numeric value
- **State:** Stores a single `double` representing the gas concentration
- **Endpoints:**
  - `GET /gas` – retrieve current gas level
  - `POST /gas?value={value}` – update gas level and trigger decision evaluation
  - `POST /gas/simulate?value={value}` – simulate gas level without triggering evaluation

### 2. Fire Sensor Service
- **Port:** 8082
- **Purpose:** Detects whether fire is present
- **State:** Stores a single `boolean` indicating fire detection
- **Endpoints:**
  - `GET /fire` – retrieve fire detection state
  - `POST /fire?value={true|false}` – update fire state and trigger decision evaluation
  - `POST /fire/simulate?value={true|false}` – simulate fire state without triggering evaluation

### 3. Alarm Button Service
- **Port:** 8083
- **Purpose:** Represents a manual emergency button
- **State:** Stores a single `boolean` indicating button press state
- **Endpoints:**
  - `GET /alarm` – retrieve alarm button state
  - `POST /alarm?value={true|false}` – update alarm state and trigger decision evaluation
  - `POST /alarm/simulate?value={true|false}` – simulate alarm state without triggering evaluation

### 4. Decision Engine Service
- **Port:** 8080
- **Purpose:** Central service that retrieves all sensor states and applies decision rules
- **State:** Stateless – queries sensors on demand
- **Endpoints:**
  - `POST /decision/evaluate` – query all sensors, apply rules, trigger actuators, and log events
- **Decision Rules:**
  - If `gasLevel > 50` → activate siren, turn on lights, open gate
  - If `fireDetected == true` → activate siren, turn on lights, open gate
  - If `alarmPressed == true` → activate siren, turn on lights, open gate

### 5. Actuator Service
- **Port:** 8084
- **Purpose:** Represents the fire station infrastructure and controls three actuators
- **State:** Stores current state of siren, alarm lights, and gate
- **Actuators:**
  - **Siren** – emergency alarm sound (ON/OFF)
  - **Alarm Lights** – visual warning signals (ON/OFF)
  - **Gate** – fire station gate (OPEN/CLOSED)
- **Endpoints:**
  - `GET /actuators/state` – retrieve current state of all actuators
  - `POST /actuators/siren?value={true|false}` – control siren
  - `POST /actuators/lights?value={true|false}` – control alarm lights
  - `POST /actuators/doors?state={OPEN|CLOSED}` – control gate

### 6. History Service
- **Port:** 8085
- **Purpose:** Persists all events (sensor readings and triggered actions) in a MySQL database
- **Database:** MySQL with table `Event`
- **Endpoints:**
  - `POST /history/save?sensorType={type}&value={value}&action={action}` – save event to database
  - `GET /history/all` – retrieve all stored events

---

## Database Schema

The History Service uses a MySQL database with the following table:

**Table: `Event`**

| Column            | Type         | Description                                      |
|-------------------|--------------|--------------------------------------------------|
| `id`              | BIGINT       | Primary key, auto-increment                      |
| `timestamp`       | DATETIME     | Event timestamp (automatically set)              |
| `sensor_type`     | VARCHAR(255) | Type of sensor (gas, fire, alarm)                |
| `value`           | VARCHAR(255) | Sensor value (e.g., "60.0", "true", "false")     |
| `action_triggered`| VARCHAR(255) | Description of triggered action or "lecture"     |

---

## How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL database (optional for testing with H2)

### Starting the Services

**IMPORTANT:** Services must be started in the following order to ensure proper dependency resolution:

1. **Gas Sensor Service**
   ```bash
   cd gas-sensor-service
   mvn spring-boot:run
   ```

2. **Fire Sensor Service**
   ```bash
   cd fire-sensor-service
   mvn spring-boot:run
   ```

3. **Alarm Button Service**
   ```bash
   cd alarm-button-service
   mvn spring-boot:run
   ```

4. **Actuator Service**
   ```bash
   cd actuator-service
   mvn spring-boot:run
   ```

5. **History Service**
   ```bash
   cd history-service
   mvn spring-boot:run
   ```

6. **Decision Engine Service**
   ```bash
   cd decision-engine-service
   mvn spring-boot:run
   ```

Each service will start on its designated port and be ready to accept HTTP requests.

---

## Testing with Postman

### Scenario 1: High Gas Level Detection

1. **Update gas level to trigger emergency:**
   ```
   POST http://localhost:8081/gas?value=60
   ```
   This will:
   - Update the gas sensor value to 60
   - Automatically trigger `/decision/evaluate`
   - Activate siren, lights, and open gate
   - Log events to the database

2. **Check actuator state:**
   ```
   GET http://localhost:8084/actuators/state
   ```
   Expected response: `siren=true, lights=true, doors=OPEN`

3. **View event history:**
   ```
   GET http://localhost:8085/history/all
   ```

### Scenario 2: Fire Detection

1. **Simulate fire detection:**
   ```
   POST http://localhost:8082/fire?value=true
   ```
   This triggers the same emergency response as high gas levels.

### Scenario 3: Manual Alarm Button

1. **Press the alarm button:**
   ```
   POST http://localhost:8083/alarm?value=true
   ```
   This also triggers the full emergency response.

### Scenario 4: Normal Conditions

1. **Set gas level below threshold:**
   ```
   POST http://localhost:8081/gas?value=30
   ```

2. **Set fire sensor to false:**
   ```
   POST http://localhost:8082/fire?value=false
   ```

3. **Set alarm to false:**
   ```
   POST http://localhost:8083/alarm?value=false
   ```

4. **Manually trigger evaluation:**
   ```
   POST http://localhost:8080/decision/evaluate
   ```
   Expected response: `Aucune action nécessaire.` (No action necessary)

---

## Expected System Behavior

### Emergency Detection Flow

1. **Sensor Update:** A sensor value is updated via POST request
2. **Automatic Evaluation:** The sensor service calls the Decision Engine's `/decision/evaluate` endpoint
3. **Rule Processing:** The Decision Engine:
   - Queries all three sensors via REST calls
   - Logs current sensor readings to the History Service
   - Evaluates decision rules against current state
4. **Emergency Response:** If any rule is triggered:
   - Commands are sent to the Actuator Service
   - Siren is activated
   - Alarm lights are turned on
   - Gate is opened
   - Emergency action is logged to the History Service
5. **Persistence:** All events (readings and actions) are stored in the MySQL database with timestamps

### Database Traceability

After running the test scenarios, the `Event` table will contain a full trace showing:
- All sensor readings with timestamps
- Actions triggered by each emergency condition
- Complete audit trail of system behavior

This ensures full traceability and allows for post-incident analysis of system responses.

---

## System Architecture Diagram

```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  Gas Sensor     │  │  Fire Sensor    │  │  Alarm Button   │
│  Service        │  │  Service        │  │  Service        │
│  (Port 8081)    │  │  (Port 8082)    │  │  (Port 8083)    │
└────────┬────────┘  └────────┬────────┘  └────────┬────────┘
         │                    │                     │
         │                    │                     │
         └────────────────────┼─────────────────────┘
                              │
                              ▼
                  ┌───────────────────────┐
                  │  Decision Engine      │
                  │  Service              │
                  │  (Port 8080)          │
                  └───────────┬───────────┘
                              │
                 ┌────────────┴────────────┐
                 │                         │
                 ▼                         ▼
      ┌──────────────────┐    ┌──────────────────┐
      │  Actuator        │    │  History         │
      │  Service         │    │  Service         │
      │  (Port 8084)     │    │  (Port 8085)     │
      └──────────────────┘    └────────┬─────────┘
                                       │
                                       ▼
                              ┌─────────────────┐
                              │  MySQL Database │
                              │  (Event Table)  │
                              └─────────────────┘
```

---

## Key Features

✓ **Microservices Architecture** – each service is independent and can be deployed separately  
✓ **RESTful Communication** – all inter-service communication uses HTTP REST APIs  
✓ **Centralized Decision Logic** – business rules are isolated in the Decision Engine  
✓ **Event Persistence** – full audit trail of all sensor readings and actions  
✓ **Safety-Critical Design** – immediate response to dangerous conditions  
✓ **Manual Override** – emergency button provides manual control  
✓ **Distributed State Management** – each sensor maintains its own state independently

---

## Technologies Used

- **Spring Boot** – microservices framework
- **Maven** – build and dependency management
- **MySQL** – persistent data storage
- **JPA/Hibernate** – object-relational mapping
- **REST** – inter-service communication
- **Postman** – API testing
