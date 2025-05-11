package com.__final_backend.backend.test.ui.util;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Helper class providing utility methods for UI testing
 */
public class UITestHelper {

  private static final Logger logger = LoggerFactory.getLogger(UITestHelper.class);

  /**
   * Wait for page to fully load
   * 
   * @param driver WebDriver instance
   */
  public static void waitForPageLoad(WebDriver driver) {
    new WebDriverWait(driver, Duration.ofSeconds(10)).until(
        webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
  }

  /**
   * Wait for an element to be visible
   * 
   * @param driver           WebDriver instance
   * @param by               Element locator
   * @param timeoutInSeconds Timeout in seconds
   * @return WebElement that is now visible
   */
  public static WebElement waitForElement(WebDriver driver, By by, int timeoutInSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  /**
   * Wait for an element to be clickable
   * 
   * @param driver           WebDriver instance
   * @param by               Element locator
   * @param timeoutInSeconds Timeout in seconds
   * @return WebElement that is now clickable
   */
  public static WebElement waitForClickable(WebDriver driver, By by, int timeoutInSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    return wait.until(ExpectedConditions.elementToBeClickable(by));
  }

  /**
   * Login to the application
   * 
   * @param driver   WebDriver instance
   * @param wait     WebDriverWait instance
   * @param baseUrl  Base URL of the application
   * @param username Username to log in with
   * @param password Password to log in with
   * @return true if login was successful, false otherwise
   */
  public static boolean login(WebDriver driver, WebDriverWait wait, String baseUrl, String username, String password) {
    try {
      logger.info("Attempting to log in as {}", username);
      driver.get(baseUrl + "/login");
      waitForPageLoad(driver);

      // Fill in login form
      driver.findElement(By.id("username")).sendKeys(username);
      driver.findElement(By.id("password")).sendKeys(password);

      // Submit form
      driver.findElement(By.cssSelector("button[type='submit']")).click();

      // Wait for redirect to complete
      wait.until((ExpectedCondition<Boolean>) webDriver -> {
        String url = webDriver.getCurrentUrl();
        return url.equals(baseUrl + "/") || url.contains("?error");
      });

      // Check if login was successful
      if (driver.getCurrentUrl().contains("?error")) {
        logger.warn("Login failed for user {}", username);
        return false;
      }

      // Wait for user dropdown to be visible (indicating successful login)
      try {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userDropdown")));
        logger.info("Login successful for user {}", username);
        return true;
      } catch (Exception e) {
        logger.warn("User dropdown not found after login for user {}", username);
        return false;
      }
    } catch (Exception e) {
      logger.error("Exception during login attempt: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Logout from the application
   * 
   * @param driver WebDriver instance
   * @param wait   WebDriverWait instance
   * @return true if logout was successful, false otherwise
   */
  public static boolean logout(WebDriver driver, WebDriverWait wait) {
    try {
      // Click user dropdown
      WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("userDropdown")));
      dropdown.click();

      // Click logout button
      WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("logoutBtn")));
      logoutBtn.click();

      // Wait for login nav item to be visible (indicating successful logout)
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginNavItem")));
      return true;
    } catch (Exception e) {
      logger.error("Exception during logout attempt: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Fill in the flight search form
   * 
   * @param driver        WebDriver instance
   * @param origin        Origin airport code
   * @param destination   Destination airport code
   * @param departureDate Departure date in MM/DD/YYYY format
   * @param returnDate    Return date in MM/DD/YYYY format, can be null for
   *                      one-way trips
   * @param passengers    Number of passengers
   */
  public static void fillFlightSearchForm(WebDriver driver, String origin, String destination,
      String departureDate, String returnDate, int passengers) {
    // Select flight type (one-way or round-trip)
    String flightType = returnDate == null ? "one-way" : "round-trip";
    try {
      driver.findElement(By.id(flightType)).click();
    } catch (Exception e) {
      // Fall back to select by value if ID doesn't work
      driver.findElement(By.cssSelector("select#tripType")).sendKeys(flightType);
    }

    // Fill in origin and destination
    WebElement originInput = driver.findElement(By.id("origin"));
    originInput.clear();
    originInput.sendKeys(origin);

    WebElement destInput = driver.findElement(By.id("destination"));
    destInput.clear();
    destInput.sendKeys(destination);

    // Fill in departure date
    WebElement departureDateInput = driver.findElement(By.id("departureDate"));
    departureDateInput.clear();
    departureDateInput.sendKeys(departureDate);

    // Fill in return date if it's a round trip
    if (returnDate != null) {
      WebElement returnDateInput = driver.findElement(By.id("returnDate"));
      returnDateInput.clear();
      returnDateInput.sendKeys(returnDate);
    }

    // Set number of passengers
    WebElement passengersInput = driver.findElement(By.id("passengers"));
    passengersInput.clear();
    passengersInput.sendKeys(String.valueOf(passengers));
  }

  /**
   * Search for flights with default parameters
   * 
   * @param driver      WebDriver instance
   * @param origin      Origin airport code
   * @param destination Destination airport code
   */
  public static void searchFlights(WebDriver driver, String origin, String destination) {
    // Set a default future date (today + 30 days)
    String futureDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE);
    fillFlightSearchForm(driver, origin, destination, futureDate, null, 1);

    // Submit the form
    driver.findElement(By.cssSelector("#flightSearchForm button[type='submit']")).click();
  }

  /**
   * Search for flights with specific parameters
   * 
   * @param driver      WebDriver instance
   * @param origin      Origin airport code
   * @param destination Destination airport code
   * @param days        Number of days from today for departure date
   * @param tripType    Type of trip ("one-way" or "round-trip")
   * @param passengers  Number of passengers
   */
  public static void searchFlights(WebDriver driver, String origin, String destination, int days, String tripType,
      int passengers) {
    // Set departure date to today + specified days
    String departureDate = LocalDate.now().plusDays(days).format(DateTimeFormatter.ISO_LOCAL_DATE);

    // Set return date to departure + 7 days if round-trip
    String returnDate = null;
    if ("round-trip".equals(tripType)) {
      returnDate = LocalDate.now().plusDays(days + 7).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    // Fill form and search
    fillFlightSearchForm(driver, origin, destination, departureDate, returnDate, passengers);

    // Submit the form
    driver.findElement(By.cssSelector("#flightSearchForm button[type='submit']")).click();
  }

  /**
   * Check if an element exists on the page
   * 
   * @param driver WebDriver instance
   * @param by     Element locator
   * @return true if the element exists, false otherwise
   */
  public static boolean elementExists(WebDriver driver, By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Wait for a toast notification message
   * 
   * @param driver       WebDriver instance
   * @param wait         WebDriverWait instance
   * @param expectedText Text expected in the toast message (case insensitive
   *                     partial match)
   * @return true if toast with matching text was found, false otherwise
   */
  public static boolean waitForToast(WebDriver driver, WebDriverWait wait, String expectedText) {
    try {
      WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast")));
      return toast.getText().toLowerCase().contains(expectedText.toLowerCase());
    } catch (Exception e) {
      logger.warn("Toast notification not found or did not contain expected text: {}", expectedText);
      return false;
    }
  }
}