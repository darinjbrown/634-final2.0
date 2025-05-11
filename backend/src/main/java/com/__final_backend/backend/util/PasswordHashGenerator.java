package com.__final_backend.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes.
 * Run this class to generate encoded passwords for database insertion.
 */
public class PasswordHashGenerator {
  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String password = "Test634";
    String encodedPassword = encoder.encode(password);

    System.out.println("Password: " + password);
    System.out.println("Encoded Password: " + encodedPassword);

    // Verify the password matches the hash
    boolean matches = encoder.matches(password, encodedPassword);
    System.out.println("Password matches hash: " + matches);
  }
}