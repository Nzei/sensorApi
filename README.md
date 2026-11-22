# Sensor API
A REST API built with Java 21 and Spring Boot for registering temperature readings from sensors and calculating average temperatures over a specified time range.

## Technologies
- Java 21
- H2 Database
- Maven

## Features
- Register temperature readings for individual sensors.
- Calculate the average temperature across all sensors.
- Filter average temperatures by sensor ID.
- Filter readings using a date and time range.
- Validate incoming requests and return appropriate HTTP errors.
- Persist readings using an H2 database.
- Unit and integration tests for the API, service and repository layers.

## Getting Started
### Prerequisites
- Java 21
- Git

### Run the application
```bash
./mvnw spring-boot:run
```

The application starts on:
`http://localhost:8080`

### Run the tests
```bash
./mvnw clean verify
```

## API Endpoints
### 1. Register a reading
**POST** `/api/v1/readings`

Request body:

```json
{
  "sensorId": "readingSensor1",
  "temperature": 10.0,
  "timestamp": "2026-11-22T15:14:00Z"
}
```

Successful response: `201 Created`

Example response:

```json
{
  "id": 1,
  "sensorId": "readingSensor1",
  "temperature": 10.0,
  "timestamp": "2026-11-22T15:14:00Z"
}
```

### 2. Calculate average temperature

**GET** `/api/v1/readings/average`

Required query parameters:

- `fromDate`: Start of the time range in ISO-8601 format.
- `toDate`: End of the time range in ISO-8601 format.

Optional query parameter:

- `sensorId`: Restricts the calculation to a particular sensor.

Example for all sensors:

```http
GET /api/v1/readings/average?fromDate=2026-11-22T00:00:00Z&toDate=2026-11-23T00:00:00Z
```

Example for specific sensor:

```http
GET /api/v1/readings/average?sensorId=readingSensor1&fromDate=2026-11-22T00:00:00Z&toDate=2026-11-23T00:00:00Z
```

Successful response: `200 OK`

```json
{
  "averageReading": 55.0
}
```

The average depends on the stored readings.

The time interval includes `fromDate` and excludes `toDate`:

`fromDate <= timestamp < toDate`

If no readings match the criteria, the API returns `204 No Content`.

## Validation and Error Handling

The API returns `400 Bad Request` for invalid requests, including:

- Missing or blank sensor IDs when registering readings.
- Missing temperatures or timestamps.
- Invalid timestamp formats.
- Invalid date ranges where `fromDate` is greater than or equal to `toDate`.
- Blank sensor ID filters.
- Non-finite temperature values.

Errors are returned using Spring's `ProblemDetail` response format.

Example:

```json
{
  "title": "Bad Request",
  "status": 400,
  "detail": "sensorId cannot be blank"
}
```

## Database

The application uses H2 with Spring Data JPA.

The application database is file-backed, allowing readings to persist between application restarts.

Tests use a separate in-memory H2 database to avoid modifying application data.