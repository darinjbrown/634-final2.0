package com.__final_backend.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes.
 * <p>
 * This standalone utility class provides functionality to generate secure
 * password hashes
 * using the BCrypt algorithm. It can be executed as a Java application to
 * generate
 * encoded passwords suitable for insertion into the database.
 * <p>
 * BCrypt is a password-hashing function designed to be slow and computationally
 * expensive,
 * which makes it resistant to brute force attacks. The generated hashes include
 * a random salt
 * to protect against rainbow table attacks.
 * <p>
 * Usage: Run this class directly to generate an encoded password for a
 * hardcoded test value.
 * In a production environment, modify this class to accept command line
 * arguments or integrate
 * its functionality into a more comprehensive password management system.
 */
public class PasswordHashGenerator {
  /**
   * Main method to demonstrate BCrypt password hashing.
   * <p>
   * Creates a BCrypt password hash for a sample password, displays both the
   * original
   * and encoded versions, and verifies that the password matches the generated
   * hash.
   *
   * @param args command-line arguments (not used in this implementation)
   */
  public static void main(String[] args) {
    // Create a BCrypt password encoder with default strength (10)
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Sample password to encode - in production, this should come from user input
    // or args
    String password = "Test634";

    // Generate the encoded password hash
    String encodedPassword = encoder.encode(password);

    // Output the results
    System.out.println("Password: " + password);
    System.out.println("Encoded Password: " + encodedPassword);

    // Verify the password matches the hash (demonstration of verification)
    boolean matches = encoder.matches(password, encodedPassword);
    System.out.println("Password matches hash: " + matches);
  }
}