# 🚛 Transporter Assignment API

An optimized transporter-to-lane assignment engine built with **Spring Boot**. Given a set of trade lanes and transporters with varying freight quotes, the API finds the cost-minimized assignment while respecting a configurable transporter limit.

> Built as a backend take-home assignment for **FreightFox**.

---

## 📋 Table of Contents

- [Problem Statement](#-problem-statement)
- [Architecture & Design](#-architecture--design)
- [Algorithm](#-algorithm)
- [API Reference](#-api-reference)
- [Assumptions & Ambiguities](#-assumptions--ambiguities)
- [Local Setup](#-local-setup)
- [Running Tests](#-running-tests)
- [Deployment](#-deployment)
- [Tech Stack](#-tech-stack)

---

## 🎯 Problem Statement

A freight company operates multiple **trade lanes** (shipping routes) and receives quotes from multiple **transporters** (trucking companies). The goal is to:

1. **Ingest** lanes and transporter quotes via an API.
2. **Compute** the optimal assignment of transporters to lanes that **minimizes total shipping cost**, using at most `k` transporters.

---

## 🏗 Architecture & Design

The project follows a **layered architecture** with clean separation of concerns:

```
┌────────────────────────────────────────────────────────────┐
│                     Client (Swagger UI / curl)             │
└──────────────────────────┬─────────────────────────────────┘
                           │ HTTP POST (JSON)
┌──────────────────────────▼─────────────────────────────────┐
│  Controller Layer         TransporterController            │
│  ─ HTTP concerns only (request parsing, response codes)    │
│  ─ Jakarta Bean Validation (@Valid, @NotNull, @Min)        │
└──────────────────────────┬─────────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────────┐
│  Service Layer            TransporterService               │
│  ─ DTO ↔ Model conversion                                 │
│  ─ Orchestrates store + solver                             │
└────────────┬─────────────────────────────────┬─────────────┘
             │                                 │
┌────────────▼──────────┐     ┌────────────────▼─────────────┐
│  InMemoryDataStore    │     │  AssignmentSolver             │
│  ─ Thread-safe store  │     │  ─ Combinatorial backtracking │
│  ─ Lanes & Transporters│    │  ─ Greedy per-lane assignment │
└───────────────────────┘     └──────────────────────────────┘
```

### Design Patterns

| Pattern | Where | Why |
|---------|-------|-----|
| **Layered Architecture** | Controller → Service → Store/Solver | Each layer has a single responsibility |
| **DTO Pattern** | `dto/request/`, `dto/response/` | Decouples API shape from domain model |
| **Singleton (Spring-managed)** | `InMemoryDataStore`, `AssignmentSolver` | Spring `@Component` creates one instance |
| **Global Exception Handler** | `GlobalExceptionHandler` | Centralized error formatting, no try-catch in controllers |
| **Constructor Injection** | All Spring beans | Immutable dependencies, testable |

---

## 🧮 Algorithm

The solver uses **combinatorial enumeration + greedy assignment**:

1. **Generate** all subsets of transporters from size 1 to `min(k, T)` using **backtracking** (LC 77 — Combinations).
2. **For each subset**, greedily assign every lane to the **cheapest transporter** in that subset.
3. If any lane has **no quote** from any transporter in the subset → mark infeasible, skip.
4. **Track** the minimum-cost feasible assignment. **Tie-break**: prefer more transporters.
5. If no subset covers all lanes → throw `NoFeasibleSolutionException` → HTTP 400.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(C(T, k) × L) where T = transporters, k = max limit, L = lanes |
| **Space** | O(T × L) for the quote lookup map |
| **At scale (T=7, k=3)** | C(7,3) × 5 = 175 operations — instant |

> **Why brute-force over Hungarian Algorithm or LP?** At this assignment's scale (T ≤ 10, L ≤ 10), the exhaustive approach is simple, correct, and explainable. No premature optimization.

---

## 📡 API Reference

### Base URL

```
Local:    http://localhost:8080
```

### API 1 — Data Ingestion

```
POST /api/v1/transporters/input
```

Submit trade lanes and transporter quotes.

**Request Body:**
```json
{
  "lanes": [
    { "id": 1, "origin": "Mumbai", "destination": "Delhi" },
    { "id": 2, "origin": "Delhi", "destination": "Bangalore" }
  ],
  "transporters": [
    {
      "id": 1,
      "name": "T1",
      "laneQuotes": [
        { "laneId": 1, "quote": 20835 },
        { "laneId": 2, "quote": 10512 }
      ]
    },
    {
      "id": 2,
      "name": "T2",
      "laneQuotes": [
        { "laneId": 1, "quote": 48844 },
        { "laneId": 2, "quote": 31326 }
      ]
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Input data saved successfully."
}
```

**curl:**
```bash
curl -X POST http://localhost:8080/api/v1/transporters/input \
  -H "Content-Type: application/json" \
  -d '{
    "lanes": [
      {"id":1,"origin":"Mumbai","destination":"Delhi"},
      {"id":2,"origin":"Delhi","destination":"Bangalore"},
      {"id":3,"origin":"Chennai","destination":"Kolkata"},
      {"id":4,"origin":"Pune","destination":"Hyderabad"},
      {"id":5,"origin":"Ahmedabad","destination":"Jaipur"}
    ],
    "transporters": [
      {"id":1,"name":"T1","laneQuotes":[{"laneId":1,"quote":20835},{"laneId":2,"quote":10512},{"laneId":3,"quote":22105},{"laneId":4,"quote":42481},{"laneId":5,"quote":19862}]},
      {"id":2,"name":"T2","laneQuotes":[{"laneId":1,"quote":48844},{"laneId":2,"quote":31326},{"laneId":3,"quote":18640},{"laneId":4,"quote":45828},{"laneId":5,"quote":18297}]},
      {"id":3,"name":"T3","laneQuotes":[{"laneId":1,"quote":39020},{"laneId":2,"quote":20648},{"laneId":3,"quote":31438},{"laneId":4,"quote":36447},{"laneId":5,"quote":12789}]},
      {"id":4,"name":"T4","laneQuotes":[{"laneId":1,"quote":14400},{"laneId":2,"quote":44514},{"laneId":3,"quote":14316},{"laneId":4,"quote":10678},{"laneId":5,"quote":13032}]},
      {"id":5,"name":"T5","laneQuotes":[{"laneId":1,"quote":11601},{"laneId":2,"quote":19760},{"laneId":3,"quote":40870},{"laneId":4,"quote":20635},{"laneId":5,"quote":26421}]},
      {"id":6,"name":"T6","laneQuotes":[{"laneId":1,"quote":35095},{"laneId":2,"quote":12494},{"laneId":3,"quote":17808},{"laneId":4,"quote":36210},{"laneId":5,"quote":39444}]},
      {"id":7,"name":"T7","laneQuotes":[{"laneId":1,"quote":26070},{"laneId":2,"quote":41098},{"laneId":3,"quote":20932},{"laneId":4,"quote":16897},{"laneId":5,"quote":27938}]}
    ]
  }'
```

### API 2 — Optimized Assignment

```
POST /api/v1/transporters/assignment
```

Compute the optimal transporter-to-lane assignment.

**Request Body:**
```json
{
  "maxTransporters": 3
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "totalCost": 60139,
  "assignments": [
    { "laneId": 1, "transporterId": 5 },
    { "laneId": 2, "transporterId": 1 },
    { "laneId": 3, "transporterId": 4 },
    { "laneId": 4, "transporterId": 4 },
    { "laneId": 5, "transporterId": 4 }
  ],
  "selectedTransporters": [1, 4, 5]
}
```

**curl:**
```bash
curl -X POST http://localhost:8080/api/v1/transporters/assignment \
  -H "Content-Type: application/json" \
  -d '{"maxTransporters": 3}'
```

### Error Responses

| Scenario | Status | Response |
|----------|--------|----------|
| Missing required fields | 400 | `{ "status": "error", "message": "lanes: Lanes list cannot be empty", "timestamp": "..." }` |
| API 2 before API 1 | 400 | `{ "status": "error", "message": "No input data available. Submit data via /api/v1/transporters/input first.", "timestamp": "..." }` |
| No feasible solution | 400 | `{ "status": "error", "message": "No feasible assignment exists with at most 2 transporters covering all 5 lanes.", "timestamp": "..." }` |
| `maxTransporters` < 1 | 400 | `{ "status": "error", "message": "maxTransporters: maxTransporters must be at least 1", "timestamp": "..." }` |

---

## 🔑 Assumptions & Ambiguities

| # | Ambiguity | Decision |
|---|-----------|----------|
| 1 | **Optimization objective** | Minimize total cost first. If tied, prefer more transporters (up to `k`). |
| 2 | **Missing quotes** | If a transporter has no quote for a lane, they cannot service it. |
| 3 | **Infeasibility** | If no combination of ≤ k transporters covers all lanes → HTTP 400. |
| 4 | **State management** | API 2 before API 1 → 400. Calling API 1 again overwrites data. |
| 5 | **Spec discrepancy** | PDF sample shows `totalCost: 75000`, but the mathematically correct minimum with ≤ 3 transporters is `60139` (T1+T4+T5). This implementation computes the correct answer. |

---

## 🚀 Local Setup

### Prerequisites

- **Java 17+** ([Download](https://adoptium.net/))
- **Maven 3.8+** (or use the included `mvnw` wrapper)

### Run

```bash
# Clone the repository
git clone https://github.com/MohittShukla/Transporter.git
cd Transporter

# Start the application (uses embedded Tomcat on port 8080)
./mvnw spring-boot:run

# Or with locally installed Maven
mvn spring-boot:run
```

The app starts at `http://localhost:8080`.

Interactive API documentation: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🧪 Running Tests

```bash
# Run all tests (7 unit + 7 integration)
./mvnw clean test

# Or
mvn clean test
```

### Test Coverage

| Test Class | Tests | What It Covers |
|------------|-------|----------------|
| `AssignmentSolverTest` | 7 | Algorithm correctness: TC1, TC2, single lane, maxTransporters > available, infeasible (uncovered lane), infeasible (not enough transporters), cheaper combo selection |
| `TransporterControllerTest` | 7 | Full HTTP integration: valid input, empty lanes (400), null body (400), assignment after input (200), missing maxTransporters (400), zero maxTransporters (400), sparse quotes TC2 |

---

## 🌐 Deployment

### Docker

```bash
# Build
docker build -t transporter-api .

# Run
docker run -p 8080:8080 transporter-api
```

### Render

1. Connect the GitHub repo on [Render](https://render.com).
2. Select **Docker** as the environment.
3. Render auto-detects the `Dockerfile`.
4. The app reads `PORT` from the environment automatically.

---

## 🛠 Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Build Tool | Maven (with `mvnw` wrapper) |
| Persistence | In-memory (`InMemoryDataStore` singleton) |
| Validation | Jakarta Bean Validation |
| API Docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5 + MockMvc |
| Containerization | Multi-stage Docker (Eclipse Temurin 17) |

---

## 📁 Project Structure

```
src/main/java/com/freightfox/transporter/
├── TransporterApplication.java          # Spring Boot entry point
├── config/
│   └── OpenApiConfig.java               # Swagger metadata
├── controller/
│   └── TransporterController.java       # REST endpoints (POST /input, /assignment)
├── dto/
│   ├── request/                         # Input DTOs with validation annotations
│   │   ├── InputRequest.java
│   │   ├── LaneDto.java
│   │   ├── LaneQuoteDto.java
│   │   ├── TransporterDto.java
│   │   └── AssignmentRequest.java
│   └── response/                        # Output DTOs
│       ├── AssignmentResponse.java
│       ├── AssignmentDetail.java
│       ├── InputResponse.java
│       └── ErrorResponse.java
├── exception/
│   ├── GlobalExceptionHandler.java      # Centralized error handling
│   ├── NoDataException.java
│   └── NoFeasibleSolutionException.java
├── model/                               # Domain models
│   ├── Lane.java
│   ├── LaneQuote.java
│   └── Transporter.java
├── service/
│   ├── TransporterService.java          # Business logic orchestration
│   └── AssignmentSolver.java            # Combinatorial optimization solver
└── store/
    └── InMemoryDataStore.java           # Thread-safe in-memory storage
```
