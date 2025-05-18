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
 * Tests for the saved flights page functionality
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SavedFlightsPageTest extends FrontendUITest {

  @BeforeEach
  @Override
  public void setupTest() {
    super.setupTest();
    driver.get(baseUrl + "/saved-flights");
    UITestHelper.waitForPageLoad(driver);
  }

  /**
   * Test that the saved flights page loads with expected UI elements.
   */
  @Test
  public void testSavedFlightsPageLoads() {
    // Verify page title
    Assertions.assertTrue(driver.getTitle().contains("Saved Flights"),
        "Page title should contain 'Saved Flights'");

    // Verify navigation bar contains the "Saved Flights" link that's active
    WebElement activeNavLink = driver.findElement(By.cssSelector("a.nav-link.active"));
    Assertions.assertTrue(activeNavLink.getText().contains("Saved Flights") ||
        activeNavLink.getText().contains("Favorite"),
        "Active navigation link should be 'Saved Flights'");
  }

  /**
   * Test that the UI shows appropriate messaging when no saved flights exist.
   */
  @Test
  public void testEmptySavedFlightsView() {
    // Skip if redirected to login
    if (driver.getCurrentUrl().contains("/login")) {
      return;
    }

    // Look for empty state message if no saved flights exist
    boolean hasSavedFlights = elementExists(By.className("flight-item")) ||
        elementExists(By.className("flight-card"));

    if (!hasSavedFlights) {
      boolean hasEmptyMessage = elementExists(By.className("empty-state")) ||
          elementExists(By.className("no-flights")) ||
          driver.getPageSource().contains("No saved flights") ||
          driver.getPageSource().contains("haven't saved any flights");

      Assertions.assertTrue(hasEmptyMessage,
          "When no saved flights exist, an empty state message should be displayed");
    }
  }

  /**
   * Test that the booking functionality is available for saved flights.
   */
  @Test
  public void testBookingActionAvailable() {
    // Skip if redirected to login
    if (driver.getCurrentUrl().contains("/login")) {
      return;
    }

    // Check if there are any saved flights
    boolean hasSavedFlights = elementExists(By.className("flight-item")) ||
        elementExists(By.className("flight-card"));

    if (hasSavedFlights) {
      // Look for booking buttons or links
      boolean hasBookingAction = elementExists(By.className("book-button")) ||
          elementExists(By.cssSelector(".btn-book")) ||
          elementExists(By.cssSelector("a[href*='bookings/new']"));

      Assertions.assertTrue(hasBookingAction,
          "Saved flights should have a booking action available");
    }
    // If no saved flights exist, this test is not applicable
  }
}
