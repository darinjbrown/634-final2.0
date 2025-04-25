# Updated Project Overview

This project is a single Java Spring Boot application that serves both the backend and frontend. The frontend HTML, CSS, and JavaScript files are integrated into the Spring Boot application and served through proper view mapping.

## Project Structure
```
backend/       # Spring Boot application
```

## Prerequisites
Ensure the following are installed on your system:
- **Java 17 or higher**
- **Maven** (if not using Maven Wrapper)

## Starting the Application

### Backend (Spring Boot Application with Integrated Frontend)
1. Open a terminal and navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. If you are using the Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   If Maven is installed globally:
   ```bash
   mvn spring-boot:run
   ```
3. Open your browser and navigate to `http://localhost:8080` to view the application.

## Viewing the HTML
The HTML content is served by the Spring Boot application. To view it:
1. Start the application as described above.
2. Open your browser and navigate to `http://localhost:8080`.

## Running Tests

### Backend Tests
1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Run the tests:
   ```bash
   ./mvnw test
   ```
   or
   ```bash
   mvn test
   ```

### Additional Notes
- The backend uses an in-memory H2 database for development purposes.
- CORS is configured to allow requests from `http://localhost:8080`.

### Troubleshooting
- If `mvnw` is not recognized, ensure you are in the correct directory and the Maven Wrapper files exist.
- If the application fails to start, ensure all dependencies are installed and the required ports are available.