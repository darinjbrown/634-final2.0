# Authentication Provider Abstraction Plan

This document outlines the incremental implementation plan for abstracting the authentication system to support both database and XML-based user authentication.

## Goal
Create an abstraction layer for the user authentication system that allows switching between database authentication (current implementation) and XML file-based authentication while maintaining clean, simple code and reusing existing authentication logic.

## Incremental Implementation Plan

### Phase 1: Setup the Provider Infrastructure

#### Step 1: Create the UserProvider Interface
1. Create the `UserProvider` interface with the essential methods needed for authentication
2. Test: No functionality changes yet, just adding the interface

#### Step 2: Create DatabaseUserProvider Implementation
1. Create `DatabaseUserProvider` implementation that delegates to `UserRepository`
2. Test: No functionality changes yet, just adding the implementation class

#### Step 3: Create Configuration Class
1. Create a simple config class that provides a `UserProvider` bean (initially just the database implementation)
2. Test: No functionality changes yet, just adding the configuration

### Phase 2: Modify CustomUserDetailsService

#### Step 4: Update CustomUserDetailsService
1. Modify `CustomUserDetailsService` to use `UserProvider` instead of `UserRepository` directly
2. Test: Login functionality, JWT token generation, and security checks

### Phase 3: Update AuthService 

#### Step 5: Update AuthServiceImpl
1. Modify `AuthServiceImpl` to use `UserProvider` instead of `UserRepository`
2. Test: User registration, authentication, and role management

### Phase 4: Update UserService

#### Step 6: Update UserServiceImpl
1. Modify `UserServiceImpl` to use `UserProvider` instead of `UserRepository`
2. Test: User CRUD operations through admin interfaces

### Phase 5: Add XML Provider Support

#### Step 7: Create XML Configuration Properties
1. Add properties to application.properties for XML support
2. Test: Application still works with database provider

#### Step 8: Implement XmlUserProvider
1. Create the `XmlUserProvider` implementation
2. Test: Application still works with database provider

#### Step 9: Update Configuration to Support Provider Switching
1. Update configuration to switch providers based on properties
2. Test: Switch to XML provider and verify functionality

## Testing After Each Step

After each incremental change, run these tests to ensure nothing breaks:

1. **User Authentication**: Test login with existing users
2. **User Registration**: Create new users
3. **Remember Me**: Test the remember-me cookie functionality
4. **JWT Verification**: Test that JWT tokens are still valid
5. **Authorization**: Test that role-based access control still works
6. **Frontend Integration**: Test that the JavaScript still works with the backend

## Configuration

To switch between database and XML authentication, the following properties will be added to `application.properties`:

```properties
# Authentication provider: database or xml
app.auth.provider=database

# XML user file path (when using XML provider)
app.auth.xml-file=src/main/resources/xml/users.xml
```

## Commands for Testing

Build and run the application after each step:

```powershell
# Navigate to the backend directory
cd c:\Users\Daren\OneDrive\Desktop\snhu\634\634-final2.0\backend

# Build the application
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```

## Advantages of This Approach

1. Minimal changes to the existing code
2. Clear separation of concerns
3. Easy switching between authentication providers
4. No changes to the frontend code required
5. Maintains all existing security features
6. Incremental implementation reduces risks
7. Each step can be tested independently
