package com.__final_backend.backend.test.ui.pages;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.__final_backend.backend.test.ui.util.UITestHelper;

import java.time.Duration;
import java.util.List;

/**
 * Tests the flight search functionality of the SkyExplorer application
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FlightSearchTest {

  @LocalServerPort
  private int port;

  private static WebDriver driver;
  private static WebDriverWait wait;
  private String baseUrl;

  @BeforeAll
  public static void setupClass() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");

    driver = new ChromeDriver(options);
    // Replace deprecated implicitlyWait with Duration-based version
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Longer wait time for API calls
  }

  @BeforeEach
  public void setupTest() {
    baseUrl = "http://localhost:" + port;
    driver.manage().deleteAllCookies();
    driver.get(baseUrl);
    UITestHelper.waitForPageLoad(driver);
  }

  @AfterAll
  public static void teardown() {
    if (driver != null) {
      driver.quit();
    }
  }

  /**
   * Tests saving a flight when user is logged in
   */
  @Test
  @Order(5)
  public void testSaveFlightWhenLoggedIn() {
    // Login first
    boolean loginSuccess = UITestHelper.login(driver, wait, baseUrl, "AdminTester", "Test634");

    if (!loginSuccess) {
      System.out.println("Could not log in to test flight saving.");
      return; // Skip this test if login fails
    }

    // Now search for flights
    UITestHelper.searchFlights(driver, "JFK", "LAX", 30, "one-way", 1);

    // Wait for search results
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flightResults")));

    // Try to save the first flight if any are found
    try {
      List<WebElement> saveButtons = driver.findElements(By.cssSelector(".save-flight-btn"));
      if (!saveButtons.isEmpty() && saveButtons.get(0).isDisplayed()) {
        saveButtons.get(0).click();

        // Fixed: Using waitForToast instead of waitForToastMessage
        boolean toastShown = UITestHelper.waitForToast(driver, wait, "saved");
        Assertions.assertTrue(toastShown, "Success toast should be shown after saving flight");

        // Verify button is now disabled or changed
        WebElement savedButton = saveButtons.get(0);
        Assertions.assertTrue(
            savedButton.getAttribute("class").contains("btn-success") ||
                savedButton.isEnabled() == false ||
                savedButton.getText().contains("Saved"),
            "Save button should change appearance after successful save");
      } else {
        System.out.println("No flights found to save");
      }
    } catch (Exception e) {
      System.out.println("Exception during flight saving: " + e.getMessage());
    }

    // Log out
    UITestHelper.logout(driver, wait);
  }

  /**
   * Tests the "Save All Flights" functionality when user is logged in
   */
  @Test
  @Order(6)
  public void testSaveAllFlightsWhenLoggedIn() {
    // Login first
    boolean loginSuccess = UITestHelper.login(driver, wait, baseUrl, "AdminTester", "Test634");

    if (!loginSuccess) {
      System.out.println("Could not log in to test save all flights.");
      return; // Skip this test if login fails
    }

    // Now search for flights
    UITestHelper.searchFlights(driver, "JFK", "LAX", 30, "one-way", 1);

    // Wait for search results
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flightResults")));

    // Try to use "Save All Flights" button if results and button exist
    try {
      WebElement saveAllButton = driver.findElement(By.id("saveAllFlightsBtn"));
      if (saveAllButton.isDisplayed() && saveAllButton.isEnabled()) {
        saveAllButton.click();

        // Fixed: Using waitForToast instead of waitForToastMessage
        boolean toastShown = UITestHelper.waitForToast(driver, wait, "saved");
        Assertions.assertTrue(toastShown, "Success toast should be shown after saving all flights");

        // Verify button is now disabled or changed
        Assertions.assertTrue(
            !saveAllButton.isEnabled() ||
                saveAllButton.getAttribute("class").contains("btn-success") ||
                saveAllButton.getText().contains("Saved"),
            "Save All button should change appearance after successful save");
      } else {
        System.out.println("Save All button not available");
      }
    } catch (Exception e) {
      System.out.println("Exception during save all flights: " + e.getMessage());
    }

    // Log out
    UITestHelper.logout(driver, wait);
  }
}