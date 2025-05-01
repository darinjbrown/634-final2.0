# Flight Search Application

This project is a Java Spring Boot application that serves both the backend and frontend. It provides a flight search feature using the Amadeus API and serves a web-based user interface with a modern Material UI-inspired design.

## Project Structure

```
backend/                          # Spring Boot application
├── src/main/java/com/__final_backend/backend/
│   ├── config/                   # Application configurations
│   ├── controller/               # REST controllers and view controllers
│   ├── dto/                      # Data Transfer Objects
│   ├── service/                  # Business logic and Amadeus API integration
│   └── util/                     # Helper utilities for Amadeus API
├── src/main/resources/
│   ├── static/                   # Static resources (CSS, JavaScript)
│   └── templates/                # HTML templates
└── src/test/                     # Test classes
```

## Features

- **Flight Search**: Search for flights by origin, destination, date, and number of travelers
- **Trip Type Selection**: Choose between one-way or round-trip flights
- **Responsive Design**: Material UI-inspired Bootstrap design for all screen sizes
- **Real-time Validation**: Form validation with clear error messages
- **Flight Results Display**: Clearly formatted flight details and pricing

## Prerequisites

Ensure the following are installed on your system:
- **Java 17 or higher**
- **Maven** (or use the Maven Wrapper included in the project)
- **ChromeDriver** (for running UI tests)
- **Internet Connection** (for Amadeus API access)

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
The application serves a modern web-based interface for searching flights:
1. Start the application as described above.
2. Open your browser and navigate to `http://localhost:8080`.
3. Use the form on the homepage to search for flights:
   - Enter the 3-letter IATA airport codes for origin and destination (e.g., JFK, LAX)
   - Select your travel dates
   - Choose the number of travelers
   - Select trip type (one-way or round-trip)
4. Click "Search Flights" to see available flights matching your criteria.

## Technical Implementation

### Backend
- **Spring Boot Framework**: Powers the application backend
- **Amadeus API Integration**: Connects to Amadeus for flight search capabilities
- **RESTful API**: Provides endpoints for flight search operations
- **Error Handling**: Comprehensive exception handling with meaningful error messages
- **Caching**: Caches airline information for improved performance

### Frontend
- **Thymeleaf Templates**: Server-side rendering of HTML pages
- **Bootstrap 5**: Modern responsive layout
- **Material Design**: UI elements styled with Material Design principles
- **JavaScript**: Dynamic form interaction and AJAX requests

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

### Amadeus API Connection Test
1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Run the `AmadeusAPIConnectionTest`:
   ```bash
   ./mvnw test -Dtest=AmadeusAPIConnectionTest
   ```
   or
   ```bash
   mvn test -Dtest=AmadeusAPIConnectionTest
   ```

## API Reference

The application integrates with the Amadeus Flight Offers Search API. Key endpoints:

- `GET /api/flights/search` - Search for flights with query parameters
- `POST /api/flights/search` - Search for flights with request body

## Configuration

The application can be configured through `application.properties`:

```properties
# Amadeus API credentials
amadeus.api.key=your-api-key
amadeus.api.secret=your-api-secret

# SSL settings for API communication
javax.net.ssl.trustStore=NONE
javax.net.ssl.trustStoreType=jks
javax.net.ssl.trustAll=true

# Logging configuration
logging.level.root=WARN
logging.level.com.__final_backend=WARN
```

## Additional Notes
- The application uses Amadeus test environment for flight data
- CORS is configured to allow requests from `http://localhost:3000` and `http://localhost:3001`

## Troubleshooting
- If `mvnw` is not recognized, ensure you are in the correct directory and the Maven Wrapper files exist
- If the application fails to start, ensure all dependencies are installed and the required ports (e.g., `8080`) are available
- For UI tests, ensure that ChromeDriver is compatible with your installed version of Chrome