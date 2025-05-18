package com.__final_backend.backend.test.ui.pages;

import com.__final_backend.backend.test.ui.base.FrontendUITest;
import com.__final_backend.backend.test.ui.util.UITestHelper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests for the bookings page functionality
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingsPageTest extends FrontendUITest {

  @BeforeEach
  @Override
  public void setupTest() {
    super.setupTest();
    driver.get(baseUrl + "/bookings");
    UITestHelper.waitForPageLoad(driver);
  }

  /**
   * Test that the bookings page loads with expected UI elements.
   */
  @Test
  public void testBookingsPageLoads() {
    // Verify page title
    Assertions.assertTrue(driver.getTitle().contains("Bookings") ||
        driver.getTitle().contains("My Bookings"),
        "Page title should contain 'Bookings'");

    // Verify navigation bar contains the "My Bookings" link that's active
    WebElement activeNavLink = driver.findElement(By.cssSelector("a.nav-link.active"));
    Assertions.assertTrue(activeNavLink.getText().contains("Bookings") ||
        activeNavLink.getText().contains("My Bookings"),
        "Active navigation link should be 'Bookings' or 'My Bookings'");
  }

  /**
   * Test authentication redirect for bookings page.
   * If the page requires authentication, unauthenticated users should be
   * redirected to login.
   */
  @Test
  public void testAuthenticationRedirect() {
    // If we're redirected to login page after trying to access bookings
    boolean redirectedToLogin = driver.getCurrentUrl().contains("/login");

    if (redirectedToLogin) {
      // Verify we're on the login page with correct elements
      Assertions.assertTrue(elementExists(By.id("username")),
          "Should be redirected to login page with username field");
      Assertions.assertTrue(elementExists(By.id("password")),
          "Login page should contain password field");
    } else {
      // If we're not redirected, we're either on the bookings page or seeing
      // unauthorized message
      boolean hasUnauthorizedMessage = elementExists(By.cssSelector(".alert-danger")) ||
          elementExists(By.cssSelector(".unauthorized-message"));

      boolean hasBookingsContent = elementExists(By.id("bookings-container")) ||
          elementExists(By.className("bookings-container")) ||
          elementExists(By.className("booking-list"));

      // Either we should see an unauthorized message or valid bookings content
      Assertions.assertTrue(hasUnauthorizedMessage || hasBookingsContent,
          "Page should either show unauthorized message or valid bookings content");
    }
  }

  /**
   * Test that the UI shows appropriate messaging when no bookings exist.
   */
  @Test
  public void testEmptyBookingsView() {
    // Skip if redirected to login
    if (driver.getCurrentUrl().contains("/login")) {
      return;
    }

    // Look for empty state message if no bookings exist
    boolean hasBookings = elementExists(By.className("booking-item")) ||
        elementExists(By.className("booking-card"));

    if (!hasBookings) {
      boolean hasEmptyMessage = elementExists(By.className("empty-state")) ||
          elementExists(By.className("no-bookings")) ||
          driver.getPageSource().contains("No bookings") ||
          driver.getPageSource().contains("haven't made any bookings");

      Assertions.assertTrue(hasEmptyMessage,
          "When no bookings exist, an empty state message should be displayed");
    }
  }
}
