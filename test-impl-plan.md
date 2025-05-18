# Test Implementation Plan for SkyExplorer

## Original Prompt
```
review the entire code base. Then review the test directory structure. make a plan to make an ultra simple unit or ui test for each key class. Coverage is not important, just that there is a simple test that passes for each key class. do not implement anything. only make a plan
```

## Overview of Current Test Structure
- **Unit Tests**: Located in `src/test/java/com/__final_backend/backend/test/unit/`
  - Currently has basic tests for FlightService, BackendApplication, and Amadeus API
- **UI Tests**: Located in `src/test/java/com/__final_backend/backend/test/ui/`
  - Has tests for pages (FlightSearchTest, LoginPageTest, RegisterPageTest)
  - Base UI test framework in FrontendUITest class
  - UI helpers in UITestHelper class

## Key Missing Test Coverage

### Backend Service Tests

1. **AuthService Test**
   - **Class to Test**: `AuthServiceImpl`
   - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/service/AuthServiceImplTest.java`
   - **Simple Test**: Test user registration with valid inputs
   - **Approach**: Mock UserProvider to simulate user storage

2. **BookingService Test**
   - **Class to Test**: `BookingService`
   - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/service/db/BookingServiceTest.java`
   - **Simple Test**: Create a booking with valid flight data
   - **Approach**: Mock required dependencies, verify booking created with expected values

3. **SavedFlightService Test**
   - **Class to Test**: `SavedFlightServiceImpl`
   - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/service/db/SavedFlightServiceImplTest.java`
   - **Simple Test**: Save a flight for a user and retrieve it
   - **Approach**: Mock repository, verify save and retrieve operations

4. **UserService Test**
   - **Class to Test**: `UserServiceImpl`
   - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/service/db/UserServiceImplTest.java`
   - **Simple Test**: Create a user and retrieve by username
   - **Approach**: Mock UserRepository, verify basic operations

### Controller Tests

5. **AuthController Test**
   - **Class to Test**: `AuthController`
   - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/controller/AuthControllerTest.java`
   - **Simple Test**: Test login endpoint with valid credentials
   - **Approach**: Use MockMvc to simulate HTTP requests, mock AuthService

6. **FlightController Test**
   - **Class to Test**: `FlightController`
   - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/controller/FlightControllerTest.java`
   - **Simple Test**: Search flights with valid parameters
   - **Approach**: Mock FlightService, verify request handling and response

7. **ViewController Test**
   - **Class to Test**: `ViewController`
   - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/controller/ViewControllerTest.java`
   - **Simple Test**: Test route mappings return correct templates
   - **Approach**: Simple controller test verifying view names

8. **SavedFlightController Test**
   - **Class to Test**: `SavedFlightController`
   - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/controller/db/SavedFlightControllerTest.java`
   - **Simple Test**: Save a flight and retrieve user's saved flights
   - **Approach**: Mock service layer, test endpoint behavior

### Security Tests

9. **JwtTokenUtil Test**
   - **Class to Test**: `JwtTokenUtil`
   - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/security/JwtTokenUtilTest.java`
   - **Simple Test**: Generate token and validate it
   - **Approach**: Create token for test user, verify extraction of username and expiration

10. **UserProvider Tests**
    - **Class to Test**: `XmlUserProvider` and `DatabaseUserProvider`
    - **Test Files**: Create `src/test/java/com/__final_backend/backend/test/unit/security/provider/XmlUserProviderTest.java` and `src/test/java/com/__final_backend/backend/test/unit/security/provider/DatabaseUserProviderTest.java`
    - **Simple Test**: Find user by username
    - **Approach**: Setup test data, verify retrieval functionality

### Entity/DTO Tests

11. **FlightDTO Test**
    - **Class to Test**: `FlightDTO`
    - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/dto/FlightDTOTest.java`
    - **Simple Test**: Create DTO and verify property setters/getters
    - **Approach**: Simple POJO testing

12. **User Entity Test**
    - **Class to Test**: `User`
    - **Test File**: Create `src/test/java/com/__final_backend/backend/test/unit/entity/UserEntityTest.java`
    - **Simple Test**: Create entity and test role management methods
    - **Approach**: Test adding/removing roles and basic properties

### UI Tests (Additional)

13. **Bookings Page Test**
    - **Class to Test**: Bookings page functionality
    - **Test File**: Create `src/test/java/com/__final_backend/backend/test/ui/pages/BookingsPageTest.java`
    - **Simple Test**: Test bookings page loads with expected elements
    - **Approach**: Similar to existing UI tests, verify DOM elements

14. **Saved Flights Page Test**
    - **Class to Test**: Saved Flights page functionality
    - **Test File**: Create `src/test/java/com/__final_backend/backend/test/ui/pages/SavedFlightsPageTest.java`
    - **Simple Test**: Verify page loads correctly
    - **Approach**: Check for key UI elements

## Implementation Plan

1. **First Priority**: Basic service layer tests (1-4)
   - These validate core business logic
   - Should be simpler to implement with mocking

2. **Second Priority**: Controller tests (5-8)
   - These validate REST endpoints and view routing
   - Cover the API exposed to clients

3. **Third Priority**: Security-related tests (9-10)
   - These ensure authentication and authorization work properly

4. **Fourth Priority**: Entity/DTO and UI tests (11-14)
   - Round out coverage with simple tests for data objects
   - Add any missing UI page tests

## Test Implementation Guidelines

- Use JUnit 5 for all tests
- Use Mockito for mocking dependencies
- Make tests focused on single functionality
- Prefer readability over comprehensive testing
- Include proper test documentation to explain test purpose
- Follow existing test patterns in the codebase
- For UI tests, build on the existing FrontendUITest base class

## Test Report Generation

To generate comprehensive test reports, run:

```powershell
cd backend
./mvnw clean test jacoco:report site
```

This will generate:
- Individual test reports in `target/surefire-reports/`
- Code coverage reports in `target/site/jacoco/index.html`
- Complete project reports in `target/site/index.html`
