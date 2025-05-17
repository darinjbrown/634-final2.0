# SkyExplorer - Flight Search Application

SkyExplorer is a modern Java Spring Boot application that provides a comprehensive flight search and booking platform. It integrates with the Amadeus API for real-time flight data and features a responsive Material UI-inspired interface built with Bootstrap 5. The application supports user management, flight searches, history tracking, and booking capabilities.

## Project Structure

```
backend/                          # Spring Boot application
├── src/main/java/com/__final_backend/backend/
│   ├── config/                   # Application configurations
│   ├── controller/               # REST controllers and view controllers
│   │   ├── admin/                # Admin-specific controllers
│   │   ├── db/                   # Database-related controllers
│   ├── dto/                      # Data Transfer Objects
│   ├── entity/                   # Database entity classes
│   ├── repository/               # Spring Data JPA repositories
│   ├── security/                 # Security configurations and JWT handlers
│   ├── service/                  # Business logic and Amadeus API integration
│   │   ├── db/                   # Database service classes
│   └── util/                     # Helper utilities for Amadeus API
├── src/main/resources/
│   ├── db/migration/             # Flyway database migration scripts
│   ├── static/                   # Static resources (CSS, JavaScript)
│   │   ├── css/                  # Stylesheet files
│   │   ├── js/                   # JavaScript files
│   │   └── favicon.ico           # Application favicon
│   └── templates/                # HTML templates
└── src/test/                     # Test classes
```

## Features

- **Flight Search**: Search for flights by origin, destination, date, and number of travelers
- **Trip Type Selection**: Choose between one-way or round-trip flights
- **Responsive Design**: Material UI-inspired Bootstrap design for all screen sizes
- **Real-time Validation**: Form validation with clear error messages
- **Flight Results Display**: Clearly formatted flight details and pricing
- **User Account Management**: Create and manage user accounts with profile information
- **Booking System**: Complete flight booking functionality with confirmation
- **Saved Flights**: Ability to save and retrieve favorite flight options
- **Security Features**: JWT authentication, password encryption, CSRF protection
- **Role-Based Access**: Different permissions for regular users and administrators
- **Remember Me**: Persistent login functionality for improved user experience
- **Customizable UI**: Status indicators and user-friendly messages throughout the application
- **Search History**: Track and display user's flight search history --FUTURE
- **Administrative Interface**: Admin panel for user management and system monitoring --FUTURE

## User Interface Overview

### Main Pages
- **Home/Flight Search**: The main entry point for flight searches
- **My Bookings**: View all your flight bookings with status information
- **Saved Flights**: Access your saved flight options for future reference
- **Login/Register**: User authentication pages

### User Experience Features
- **Responsive Navigation**: Consistent navigation bar across all pages
- **Status Indicators**: Color-coded statuses for bookings (Confirmed, Pending, Cancelled)
- **Loading Indicators**: Loading spinners for asynchronous operations
- **Error Handling**: User-friendly error messages and recovery options
- **Confirmation Messages**: Clear feedback when actions are completed successfully

## Prerequisites

Ensure the following are installed on your system:
- **Java 17 or higher**
- **Maven** (or use the Maven Wrapper included in the project)
- **ChromeDriver** (for running UI tests)
- **Internet Connection** (for Amadeus API access)
- **MySQL** (optional, for production database)

## Installation and Setup

### Clone the Repository
1. Clone this repository to your local machine:
   ```bash
   git clone https://github.com/darinjbrown/634-final2.0.git
   ```
   Or download and extract the attached ZIP file.

### Configure Application Properties
1. Navigate to `backend/src/main/resources/application.properties`
2. Review the configuration settings:
   - The default H2 in-memory database is suitable for development
   - Verify Amadeus API credentials are set correctly
   - For production, consider configuring MySQL (see Database Configuration section)

## Running the Application

1. Open a terminal and navigate to the `backend` directory:
   ```bash
   cd backend
   ```

2. Build and run the application using Maven:
   - Using Maven Wrapper (Windows):
     ```bash
     mvnw spring-boot:run
     ```
   - Using Maven Wrapper (Linux/Mac):
     ```bash
     ./mvnw spring-boot:run
     ```
   - If Maven is installed globally:
     ```bash
     mvn spring-boot:run
     ```

3. Wait for the application to start. You should see output indicating the server is running on port 8080.

4. Open your browser and navigate to `http://localhost:8080` to access the application.

## Accessing SkyExplorer

### Public Access
- Visit `http://localhost:8080` to access the main flight search page
- Anyone can search for flights without logging in
- Registration and login are required for saving searches and booking flights

### User Registration
1. Click "Register" in the top navigation bar
2. Fill out the registration form with your details
3. Accept the terms and conditions
4. Click "Register" to create your account
5. You'll be redirected to the login page upon successful registration

### User Login
1. Click "Login" in the top navigation bar
2. Enter your username/email and password
3. Check the "Remember me" option if desired
4. Click "Login" to access your account
5. Once logged in, you can access additional features like saving searches and booking flights

### Admin Access
1. To access administrative features, log in with the following credentials:
   - Username: `AdminTester` (AdminTester2 in XML)
   - Password: `Test634`(Test634A in XML)
2. After logging in as admin, you'll see an "Admin" link in the navigation bar
3. Click this link to access the administrative dashboard where you can:
   - View and manage users
   - Monitor system activity
   - Access system diagnostics

## Using the Flight Search

1. On the homepage, fill out the search form:
   - Enter 3-letter IATA airport codes for origin and destination (e.g., JFK, LAX)
   - Select your travel date(s)
   - Choose the number of travelers
   - Select trip type (one-way or round-trip)
2. Click "Search Flights" to submit your query
3. Review the flight results displayed below the search form
4. You can sort the results by price using the "Sort by Price" button
5. If logged in, you can save flights for later reference

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

## Authentication Provider Configuration

The application supports multiple authentication provider options, allowing you to switch between database (default) and XML file-based user storage.

### Provider Options

1. **Database Authentication (Default)** 
   - Uses the configured database (H2 or MySQL) to store user credentials
   - All user data persists in the database tables

2. **XML Authentication**
   - Stores user data in an XML file
   - File location: `src/main/resources/xml/users.xml`
   - User data persists between application restarts
   - Useful for simple deployments or testing scenarios

### Switching Between Providers

To switch authentication providers, modify the following settings in `application.properties`:

```properties
# Authentication provider configuration
# Options: database or xml
app.auth.provider=database

# XML user file path (when using XML provider)
app.auth.xml-file=src/main/resources/xml/users.xml
```

Simply change `app.auth.provider=database` to `app.auth.provider=xml` to switch to XML-based authentication.

### Implementation Details

The authentication system uses an abstraction layer that allows different storage mechanisms without changing the business logic. This architecture:

- Maintains a consistent security approach regardless of storage mechanism
- Preserves all security features (password encryption, JWT token validation)
- Allows seamless switching between providers without code changes

## Database Schema

The application uses Flyway for database migrations. The schema includes:

- **users**: User account information
- **user_roles**: Role assignments for user authorization
- **flight_searches**: User search history
- **saved_flights**: Flights saved by users for later reference
- **booking_records**: Flight booking information
- **audit_trail**: System audit logs for security monitoring

## Technical Implementation

### Backend
- **Spring Boot Framework**: Powers the application backend
- **Amadeus API Integration**: Connects to Amadeus for flight search capabilities
- **Spring Data JPA**: ORM for database operations
- **Flyway**: Database migrations for version control
- **RESTful API**: Provides endpoints for flight search and database operations
- **Spring Security**: User authentication and authorization
- **JWT Authentication**: Secure token-based authentication
- **Error Handling**: Comprehensive exception handling with meaningful error messages

### Frontend
- **Thymeleaf Templates**: Server-side rendering of HTML pages
- **Bootstrap 5**: Modern responsive layout
- **Material Design**: UI elements styled with Material Design principles
- **JavaScript**: Dynamic form interaction and AJAX requests
- **CSRF Protection**: Security for form submissions

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
1. Ensure that ChromeDriver is installed. The tests use WebDriver manager that automatically detects and uses your Chrome installation - no manual path configuration is required.
2. Run the UI tests:
   ```bash
   ./mvnw test
   ```
   or
   ```bash
   mvn test
   ```

   ```

## API Reference

The application integrates with the Amadeus Flight Offers Search API and provides database API endpoints. Key endpoints:

### Flight Search
- `GET /api/flights/search` - Search for flights with query parameters
- `POST /api/flights/search` - Search for flights with request body

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate a user and get JWT token
- `POST /api/auth/logout` - Logout and invalidate token
- `GET /api/auth/me` - Get current authenticated user details

### Database Operations
- `GET /api/users` - Get all users
- `POST /api/users` - Create a new user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/flight-searches/user/{userId}` - Get flight searches by user
- `POST /api/flight-searches` - Save a flight search

### Admin Operations
- `POST /api/admin/users/{userId}/promote` - Promote a user to admin
- `POST /api/admin/users/{userId}/demote` - Remove admin role from a user
- `POST /api/admin/users/{userId}/roles/{role}` - Add a role to a user
- `DELETE /api/admin/users/{userId}/roles/{role}` - Remove a role from a user

## Development Guide

### Modifying Frontend Components
To modify frontend components (HTML templates and JavaScript):

1. HTML templates are located in `backend/src/main/resources/templates/`
2. JavaScript files are in `backend/src/main/resources/static/js/`
3. CSS styles are in `backend/src/main/resources/static/css/`

### Key JavaScript Files
- **auth.js**: Handles authentication and user management
- **bookings.js**: Manages the bookings page functionality
- **scripts.js**: Core functionality for the flight search page
- **saved-flights.js**: Handles saved flights operations

### Adding New Features
When adding new features:

1. Make sure to maintain the existing authentication flow
2. Update relevant JavaScript files for frontend logic
3. Modify controller classes for new endpoints
4. Add service methods as needed
5. Test thoroughly across different browsers
6. Update this documentation to reflect new features

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
- The in-memory H2 database is reset with each application restart; all user data and searches will be lost
- CORS is configured to allow requests from `http://localhost:3000` and `http://localhost:3001`
- The database automatically initializes with sample data for testing
- A test admin account (AdminTester/Test634) is created automatically on startup
- When running XML authentication, previous users created will persist, but their database data will be lost on restart

## Troubleshooting
- If `mvnw` is not recognized, ensure you are in the correct directory and the Maven Wrapper files exist
- If the application fails to start, ensure all dependencies are installed and the required ports (e.g., `8080`) are available
- For UI tests, ensure that ChromeDriver is compatible with your installed version of Chrome
- If database errors occur, check the connection settings in `application.properties`
- For database migration issues, check the Flyway logs and ensure migration scripts are correctly formatted
- If you're unable to log in as admin, ensure the Flyway migrations have run successfully