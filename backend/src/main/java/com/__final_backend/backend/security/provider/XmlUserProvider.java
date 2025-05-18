package com.__final_backend.backend.security.provider;

import com.__final_backend.backend.entity.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of UserProvider that stores and retrieves user data from an
 * XML file.
 * <p>
 * This provider manages user data in an XML file as an alternative to database
 * storage.
 * It provides functionality for initializing the XML file if it doesn't exist,
 * as well as
 * thread-safe operations for reading and writing user data. The implementation
 * ensures
 * proper ID management for users stored in the XML file.
 * <p>
 * This provider is typically used when XML-based authentication is configured
 * for the
 * application, allowing user data to be stored in a simple file format rather
 * than
 * requiring a database.
 */
@Component
public class XmlUserProvider implements UserProvider {
  /**
   * Path to the XML file that stores user data, configured via application
   * properties.
   */
  @Value("${app.auth.xml-file}")
  private String xmlFilePath;

  /** The XML file object for user data storage. */
  private File xmlFile;

  /** Lock object for thread-safe file operations. */
  private final Object fileLock = new Object();

  /** Counter for generating unique user IDs. */
  private AtomicLong nextId = new AtomicLong(1);

  /**
   * Initializes the XML file for user storage.
   * <p>
   * This method is called automatically after dependency injection. It ensures
   * that the XML file exists and contains a valid root element. If the file
   * doesn't exist, it creates a new one with an empty users root element. If
   * the file already exists, it scans for the highest user ID to initialize the
   * ID counter.
   */
  @PostConstruct
  public void init() {
    xmlFile = new File(xmlFilePath);
    File directory = xmlFile.getParentFile();

    // Create directory if it doesn't exist
    if (!directory.exists()) {
      directory.mkdirs();
    }

    // Create XML file with root element if it doesn't exist
    if (!xmlFile.exists()) {
      try {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Create root element
        Element rootElement = doc.createElement("users");
        doc.appendChild(rootElement);

        // Write to XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);

        System.out.println("XML User file created at: " + xmlFile.getAbsolutePath());
      } catch (ParserConfigurationException | TransformerException e) {
        throw new RuntimeException("Failed to create XML user file", e);
      }
    } else {
      // If file exists, find the highest ID to set nextId correctly
      updateNextId();
    }
  }

  /**
   * Updates the next ID counter based on existing user data.
   * <p>
   * Scans all users in the XML file to find the highest ID,
   * then sets the next ID counter to one higher to ensure uniqueness.
   */
  private void updateNextId() {
    List<User> users = findAll();
    if (!users.isEmpty()) {
      long maxId = users.stream()
          .mapToLong(User::getId)
          .max()
          .orElse(0);
      nextId.set(maxId + 1);
    }
  }

  /**
   * Loads and parses the XML document.
   *
   * @return the parsed XML document
   * @throws RuntimeException if the file cannot be read or parsed
   */
  private Document getDocument() {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      return dBuilder.parse(xmlFile);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException("Error reading XML user file", e);
    }
  }

  /**
   * Saves the XML document to the file system.
   * <p>
   * This method is synchronized using a file lock to prevent concurrent
   * modifications.
   *
   * @param doc the XML document to save
   * @throws RuntimeException if the file cannot be saved
   */
  private void saveDocument(Document doc) {
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(xmlFile);
      synchronized (fileLock) {
        transformer.transform(source, result);
      }
    } catch (TransformerException e) {
      throw new RuntimeException("Error saving XML user file", e);
    }
  }

  /**
   * Converts an XML user element to a User object.
   *
   * @param element the XML element representing a user
   * @return the converted User object
   */
  private User elementToUser(Element element) {
    User user = new User();
    user.setId(Long.parseLong(element.getAttribute("id")));
    user.setUsername(element.getAttribute("username"));
    user.setEmail(element.getAttribute("email"));
    user.setPasswordHash(element.getAttribute("password")); // XML stores password hash in "password" attribute

    // Parse roles from comma-separated string
    String rolesStr = element.getAttribute("roles");
    Set<String> roleSet = new HashSet<>();
    if (rolesStr != null && !rolesStr.isEmpty()) {
      String[] roleArray = rolesStr.split(",");
      for (String role : roleArray) {
        roleSet.add(role.trim());
      }
    }
    user.setRoles(roleSet);
    return user;
  }

  /**
   * Converts a User object to an XML user element.
   *
   * @param doc  the XML document to create the element in
   * @param user the User object to convert
   * @return the newly created XML element representing the user
   */
  private Element userToElement(Document doc, User user) {
    Element element = doc.createElement("user");
    element.setAttribute("id", String.valueOf(user.getId()));
    element.setAttribute("username", user.getUsername());
    element.setAttribute("email", user.getEmail());
    element.setAttribute("password", user.getPasswordHash()); // Store password hash in "password" attribute

    // Join roles as comma-separated string
    String rolesStr = String.join(",", user.getRoles());
    element.setAttribute("roles", rolesStr);
    return element;
  }

  /**
   * Finds a user by their username.
   * <p>
   * Searches through the XML document to find a user with the specified username.
   * This method is case-sensitive and returns the first matching user found.
   * 
   * @param username the username to search for
   * @return an Optional containing the User if found, or empty if no match exists
   */
  @Override
  public Optional<User> findByUsername(String username) {
    Document doc = getDocument();
    NodeList userList = doc.getElementsByTagName("user");

    for (int i = 0; i < userList.getLength(); i++) {
      Element userElement = (Element) userList.item(i);
      if (username.equals(userElement.getAttribute("username"))) {
        return Optional.of(elementToUser(userElement));
      }
    }

    return Optional.empty();
  }

  /**
   * Finds a user by their email address.
   * <p>
   * Searches through the XML document to find a user with the specified email
   * address.
   * This method is case-sensitive and returns the first matching user found.
   * 
   * @param email the email address to search for (e.g., "user@example.com")
   * @return an Optional containing the User if found, or empty if no match exists
   */
  @Override
  public Optional<User> findByEmail(String email) {
    Document doc = getDocument();
    NodeList userList = doc.getElementsByTagName("user");

    for (int i = 0; i < userList.getLength(); i++) {
      Element userElement = (Element) userList.item(i);
      if (email.equals(userElement.getAttribute("email"))) {
        return Optional.of(elementToUser(userElement));
      }
    }

    return Optional.empty();
  }

  /**
   * Checks if a user with the specified username exists.
   * <p>
   * This is a convenience method that leverages {@link #findByUsername(String)}
   * to determine if a user exists with the given username.
   * 
   * @param username the username to check for existence
   * @return true if a user with the specified username exists, false otherwise
   */
  @Override
  public boolean existsByUsername(String username) {
    return findByUsername(username).isPresent();
  }

  /**
   * Checks if a user with the specified email address exists.
   * <p>
   * This is a convenience method that leverages {@link #findByEmail(String)}
   * to determine if a user exists with the given email address.
   * 
   * @param email the email address to check for existence
   * @return true if a user with the specified email exists, false otherwise
   */
  @Override
  public boolean existsByEmail(String email) {
    return findByEmail(email).isPresent();
  }

  /**
   * Finds a user by their unique ID.
   * <p>
   * Searches through the XML document to find a user with the specified ID.
   * Since IDs are unique in the system, this will return at most one user.
   * 
   * @param id the unique identifier of the user to find
   * @return an Optional containing the User if found, or empty if no user exists
   *         with the given ID
   */
  @Override
  public Optional<User> findById(Long id) {
    Document doc = getDocument();
    NodeList userList = doc.getElementsByTagName("user");

    for (int i = 0; i < userList.getLength(); i++) {
      Element userElement = (Element) userList.item(i);
      if (id.equals(Long.parseLong(userElement.getAttribute("id")))) {
        return Optional.of(elementToUser(userElement));
      }
    }

    return Optional.empty();
  }

  /**
   * Retrieves all users stored in the XML file.
   * <p>
   * This method reads the entire XML document and converts all user elements
   * into User objects. The returned list may be empty if no users exist,
   * but it will never be null.
   * 
   * @return a List containing all User objects, which may be empty but never null
   */
  @Override
  public List<User> findAll() {
    Document doc = getDocument();
    NodeList userList = doc.getElementsByTagName("user");
    List<User> users = new ArrayList<>();

    for (int i = 0; i < userList.getLength(); i++) {
      Element userElement = (Element) userList.item(i);
      users.add(elementToUser(userElement));
    }

    return users;
  }

  /**
   * Saves or updates a user in the XML file.
   * <p>
   * This method performs different operations depending on whether the user
   * already exists:
   * <ul>
   * <li>For new users (id is null or 0), it assigns a new unique ID and adds the
   * user to the XML file</li>
   * <li>For existing users, it updates the user's attributes in the XML file</li>
   * <li>If a user has an ID but doesn't exist in the XML file, it will be added
   * as a new entry</li>
   * </ul>
   * <p>
   * This operation is thread-safe, using a lock to prevent concurrent
   * modifications
   * to the XML file.
   * 
   * @param user the User object to save or update
   * @return the saved User object, with an ID assigned if it was a new user
   */
  @Override
  public User save(User user) {
    synchronized (fileLock) {
      Document doc = getDocument();
      Element rootElement = doc.getDocumentElement();

      // For new users, assign an ID
      if (user.getId() == null || user.getId() == 0) {
        user.setId(nextId.getAndIncrement());
        Element newUserElement = userToElement(doc, user);
        rootElement.appendChild(newUserElement);
      } else {
        // Update existing user
        NodeList userList = doc.getElementsByTagName("user");
        boolean found = false;

        for (int i = 0; i < userList.getLength(); i++) {
          Element userElement = (Element) userList.item(i);
          if (user.getId().equals(Long.parseLong(userElement.getAttribute("id")))) { // Update attributes
            userElement.setAttribute("username", user.getUsername());
            userElement.setAttribute("email", user.getEmail());
            userElement.setAttribute("password", user.getPasswordHash()); // Use passwordHash
            // Join roles as comma-separated string
            String rolesStr = String.join(",", user.getRoles());
            userElement.setAttribute("roles", rolesStr);
            found = true;
            break;
          }
        }

        if (!found) {
          // User with ID doesn't exist, create new entry
          Element newUserElement = userToElement(doc, user);
          rootElement.appendChild(newUserElement);
        }
      }

      saveDocument(doc);
      return user;
    }
  }

  /**
   * Deletes a user by their unique ID.
   * <p>
   * This method removes a user from the XML file based on their ID. If no user
   * with the specified ID exists, this method completes silently without making
   * any changes.
   * <p>
   * This operation is thread-safe, using a lock to prevent concurrent
   * modifications
   * to the XML file.
   * 
   * @param id the unique identifier of the user to delete
   */
  @Override
  public void deleteById(Long id) {
    synchronized (fileLock) {
      Document doc = getDocument();
      NodeList userList = doc.getElementsByTagName("user");

      for (int i = 0; i < userList.getLength(); i++) {
        Element userElement = (Element) userList.item(i);
        if (id.equals(Long.parseLong(userElement.getAttribute("id")))) {
          userElement.getParentNode().removeChild(userElement);
          saveDocument(doc);
          return;
        }
      }
    }
  }
}
