package com.__final_backend.backend.security.provider;

import com.__final_backend.backend.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.PostConstruct;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class XmlUserProvider implements UserProvider {

  @Value("${app.auth.xml-file}")
  private String xmlFilePath;

  private File xmlFile;
  private final Object fileLock = new Object();
  private AtomicLong nextId = new AtomicLong(1);

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

  private Document getDocument() {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      return dBuilder.parse(xmlFile);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException("Error reading XML user file", e);
    }
  }

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

  private User elementToUser(Element element) {
    User user = new User();
    user.setId(Long.parseLong(element.getAttribute("id")));
    user.setUsername(element.getAttribute("username"));
    user.setEmail(element.getAttribute("email"));
    user.setPasswordHash(element.getAttribute("password")); // Use passwordHash instead
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

  private Element userToElement(Document doc, User user) {
    Element element = doc.createElement("user");
    element.setAttribute("id", String.valueOf(user.getId()));
    element.setAttribute("username", user.getUsername());
    element.setAttribute("email", user.getEmail());
    element.setAttribute("password", user.getPasswordHash()); // Use passwordHash
    // Join roles as comma-separated string
    String rolesStr = String.join(",", user.getRoles());
    element.setAttribute("roles", rolesStr);
    return element;
  }

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

  @Override
  public boolean existsByUsername(String username) {
    return findByUsername(username).isPresent();
  }

  @Override
  public boolean existsByEmail(String email) {
    return findByEmail(email).isPresent();
  }

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
