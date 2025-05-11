package com.__final_backend.backend.test.ui.pages;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.springframework.boot.test.context.SpringBootTest;

import com.__final_backend.backend.test.ui.base.FrontendUITest;

/**
 * Tests the registration functionality of the SkyExplorer application
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterPageTest extends FrontendUITest {

  /**
   * Tests that the registration page loads correctly
   */
  @Test
  public void testRegisterPageLoads() {
    driver.get(baseUrl + "/register");
    waitForPageLoad();

    // Verify page title
    Assertions.assertTrue(driver.getTitle().contains("Register"));

    // Verify form elements exist using the safer element exists check
    Assertions.assertTrue(elementExists(By.id("registerForm")));
    Assertions.assertTrue(elementExists(By.id("username")));
    Assertions.assertTrue(elementExists(By.id("email")));
    Assertions.assertTrue(elementExists(By.id("password")));
    Assertions.assertTrue(elementExists(By.id("confirmPassword")));
    Assertions.assertTrue(elementExists(By.id("firstName")));
    Assertions.assertTrue(elementExists(By.id("lastName")));
  }
}