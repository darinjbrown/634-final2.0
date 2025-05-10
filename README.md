# Flight Search Application

This project is a Java Spring Boot application that serves both the backend and frontend. It provides a flight search feature using the Amadeus API and serves a web-based user interface with a modern Material UI-inspired design.

## Project Structure

```
backend/                          # Spring Boot application
├── src/main/java/com/__final_backend/backend/
│   ├── config/                   # Application configurations
│   ├── controller/               # REST controllers and view controllers
│   │   ├── db/                   # Database-related controllers
│   ├── dto/                      # Data Transfer Objects
│   ├── entity/                   # Database entity classes
│   ├── repository/               # Spring Data JPA repositories
│   ├── service/                  # Business logic and Amadeus API integration
│   │   ├── db/                   # Database service classes
│   └── util/                     # Helper utilities for Amadeus API
├── src/main/resources/
│   ├── db/migration/             # Flyway database migration scripts
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
- **Database Integration**: Store user data, flight search history, saved flights, and bookings
- **User Management**: Create and manage user accounts
- **Search History**: Track and display user's flight search history

## Prerequisites

Ensure the following are installed on your system:
- **Java 17 or higher**
- **Maven** (or use the Maven Wrapper included in the project)
- **ChromeDriver** (for running UI tests)
- **Internet Connection** (for Amadeus API access)
- **MySQL** (optional, for production database)

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

## Database Configuration

The application uses an embedded H2 database by default for development and testing. For production, you can configure MySQL.

### H2 Database (Development/Testing)
- No additional setup required
- Access the H2 console at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:flightdb`
- Username: `sa`
- Password: `password`

### MySQL Database (Production)
1. Install MySQL on your system
2. Create a database named `flightdb`:
   ```sql
   CREATE DATABASE flightdb;
   ```
3. Update `application.properties` to use MySQL:
   - Uncomment the MySQL configuration section
   - Comment out the H2 configuration section
   - Update the MySQL username and password to match your installation

## Database Schema

The application uses Flyway for database migrations. The schema includes:

- **users**: User account information
- **flight_searches**: User search history
- **saved_flights**: Flights saved by users for later reference
- **booking_records**: Flight booking information
- **audit_trail**: System audit logs

## Technical Implementation

### Backend
- **Spring Boot Framework**: Powers the application backend
- **Amadeus API Integration**: Connects to Amadeus for flight search capabilities
- **Spring Data JPA**: ORM for database operations
- **Flyway**: Database migrations for version control
- **RESTful API**: Provides endpoints for flight search and database operations
- **Error Handling**: Comprehensive exception handling with meaningful error messages
- **Caching**: Caches airline information for improved performance

### Frontend
- **Thymeleaf Templates**: Server-side rendering of HTML pages
- **Bootstrap 5**: Modern responsive layout
- **Material Design**: UI elements styled with Material Design principles
- **JavaScript**: Dynamic form interaction and AJAX requests

### Database
- **H2/MySQL**: Relational database storage
- **Spring Data JPA**: Repository pattern for data access
- **Entity Classes**: Java model classes mapped to database tables
- **Flyway Migrations**: Version-controlled database schema changes

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

The application integrates with the Amadeus Flight Offers Search API and provides database API endpoints. Key endpoints:

### Flight Search
- `GET /api/flights/search` - Search for flights with query parameters
- `POST /api/flights/search` - Search for flights with request body

### Database Operations
- `GET /api/users` - Get all users
- `POST /api/users` - Create a new user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/flight-searches/user/{userId}` - Get flight searches by user
- `POST /api/flight-searches` - Save a flight search

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

# Database Configuration (H2 for development)
spring.datasource.url=jdbc:h2:mem:flightdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate settings
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Flyway configuration for database migrations
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration

# MySQL Database Configuration (for production)
#spring.datasource.url=jdbc:mysql://localhost:3306/flightdb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
#spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
#spring.datasource.username=root
#spring.datasource.password=yourpassword
#spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# Logging configuration
logging.level.root=WARN
logging.level.com.__final_backend=WARN
```

## Additional Notes
- The application uses Amadeus test environment for flight data
- CORS is configured to allow requests from `http://localhost:3000` and `http://localhost:3001`
- The database automatically initializes with sample data for testing

## Troubleshooting
- If `mvnw` is not recognized, ensure you are in the correct directory and the Maven Wrapper files exist
- If the application fails to start, ensure all dependencies are installed and the required ports (e.g., `8080`) are available
- For UI tests, ensure that ChromeDriver is compatible with your installed version of Chrome
- If database errors occur, check the connection settings in `application.properties`
- For database migration issues, check the Flyway logs and ensure migration scripts are correctly formatted