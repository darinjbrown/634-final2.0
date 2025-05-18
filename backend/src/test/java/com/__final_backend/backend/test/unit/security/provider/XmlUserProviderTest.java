package com.__final_backend.backend.test.unit.security.provider;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.security.provider.XmlUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for XmlUserProvider class.
 * Tests XML-based user storage operations through the UserProvider interface.
 */
public class XmlUserProviderTest {

  private XmlUserProvider xmlUserProvider;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    // Create a new provider with a test XML file
    xmlUserProvider = new XmlUserProvider();

    // Create a temporary XML file for testing
    String xmlFilePath = tempDir.resolve("test-users.xml").toString();
    ReflectionTestUtils.setField(xmlUserProvider, "xmlFilePath", xmlFilePath);

    // Initialize the provider
    xmlUserProvider.init();
  }

  /**
   * Test saving a user and finding it by username.
   * Verifies that the provider correctly saves a user to XML and retrieves it.
   */
  @Test
  void testSaveAndFindByUsername() {
    // Arrange - Create a test user
    User testUser = new User();
    testUser.setUsername("xmlTestUser");
    testUser.setEmail("xml-test@example.com");
    testUser.setPasswordHash("xmlHashedPassword");
    testUser.setFirstName("XML");
    testUser.setLastName("User");
    testUser.addRole("USER");

    // Act - Save user to XML
    User savedUser = xmlUserProvider.save(testUser);

    // Assert - User was saved with an ID
    assertNotNull(savedUser.getId(), "Saved user should have an ID");

    // Act - Find the user by username
    Optional<User> foundUser = xmlUserProvider.findByUsername("xmlTestUser");

    // Assert - User can be found by username
    assertTrue(foundUser.isPresent(), "User should be found by username");
    assertEquals("xmlTestUser", foundUser.get().getUsername(), "Username should match");
    assertEquals("xml-test@example.com", foundUser.get().getEmail(), "Email should match");
  }

  /**
   * Test checking if a username exists after saving a user.
   */
  @Test
  void testExistsByUsername() {
    // Arrange - Create and save a test user
    User testUser = new User();
    testUser.setUsername("existingXmlUser");
    testUser.setEmail("existing-xml@example.com");
    testUser.setPasswordHash("xmlHashedPassword");
    xmlUserProvider.save(testUser);

    // Act & Assert
    assertTrue(xmlUserProvider.existsByUsername("existingXmlUser"),
        "Should return true for existing username");
    assertFalse(xmlUserProvider.existsByUsername("nonExistingUser"),
        "Should return false for non-existing username");
  }

  /**
   * Test finding all users after saving multiple users.
   */
  @Test
  void testFindAll() {
    // Arrange - Create and save multiple test users
    User user1 = new User();
    user1.setUsername("xmlUser1");
    user1.setEmail("xml1@example.com");
    user1.setPasswordHash("hash1");

    User user2 = new User();
    user2.setUsername("xmlUser2");
    user2.setEmail("xml2@example.com");
    user2.setPasswordHash("hash2");

    xmlUserProvider.save(user1);
    xmlUserProvider.save(user2);

    // Act
    List<User> allUsers = xmlUserProvider.findAll();

    // Assert
    assertTrue(allUsers.size() >= 2, "Should find at least the two users we added");
    assertTrue(allUsers.stream().anyMatch(u -> "xmlUser1".equals(u.getUsername())),
        "Should contain first user");
    assertTrue(allUsers.stream().anyMatch(u -> "xmlUser2".equals(u.getUsername())),
        "Should contain second user");
  }
}
