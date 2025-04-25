# Flight Search Application

This project is a Java Spring Boot application that serves both the backend and frontend. It provides a flight search feature using the Amadeus API and serves a web-based user interface.

## Project Structure
```
backend/       # Spring Boot application
```

## Prerequisites
Ensure the following are installed on your system:
- **Java 17 or higher**
- **Maven** (or use the Maven Wrapper included in the project)
- **ChromeDriver** (for running UI tests)

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
3. Open your browser and navigate to `http://localhost:8080` to access the application.

## Accessing the Application
The application serves a web-based interface for searching flights:
1. Start the application as described above.
2. Open your browser and navigate to `http://localhost:8080`.
3. Use the form on the homepage to search for flights by entering the required details such as trip type, starting location, ending location, travel date, and number of travelers.

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

### Frontend UI Tests
1. Ensure that ChromeDriver is installed and its path is correctly set in the `FrontendUITest` class:
   ```java
   System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
   ```
2. Run the UI tests:
   ```bash
   ./mvnw test
   ```
   or
   ```bash
   mvn test
   ```

## Additional Notes
- The backend uses an in-memory H2 database for development purposes.
- CORS is configured to allow requests from `http://localhost:3000` and `http://localhost:3001`.
- The application uses a mock Amadeus API key (`test-key`) for testing purposes. Update the `application.properties` file with your actual API key for production use.

## Troubleshooting
- If `mvnw` is not recognized, ensure you are in the correct directory and the Maven Wrapper files exist.
- If the application fails to start, ensure all dependencies are installed and the required ports (e.g., `8080`) are available.
- For UI tests, ensure that ChromeDriver is compatible with your installed version of Chrome.