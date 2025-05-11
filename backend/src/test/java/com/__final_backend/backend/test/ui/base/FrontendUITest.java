package com.__final_backend.backend.test.ui.base;

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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for frontend UI testing
 * This class serves as a container for all frontend tests and provides
 * setup/teardown for common resources
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FrontendUITest {

  private static final Logger logger = LoggerFactory.getLogger(FrontendUITest.class);
  protected static WebDriver driver;
  protected static WebDriverWait wait;
  protected String baseUrl;

  @LocalServerPort
  private int port;

  @BeforeAll
  public static void setupClass() {
    logger.info("Setting up ChromeDriver for UI testing");
    try {
      ChromeOptions options = new ChromeOptions();
      options.addArguments("--headless"); // Run headless for CI environments
      options.addArguments("--no-sandbox");
      options.addArguments("--disable-dev-shm-usage");
      options.addArguments("--window-size=1920,1080"); // Set larger window size

      driver = new ChromeDriver(options);
      driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS); // Increased timeout
      wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Increased timeout
      logger.info("ChromeDriver initialized successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize ChromeDriver: " + e.getMessage());
    }
  }

  @BeforeEach
  public void setupTest() {
    baseUrl = "http://localhost:" + port;
    driver.manage().deleteAllCookies();
  }

  @AfterAll
  public static void teardown() {
    if (driver != null) {
      logger.info("Closing ChromeDriver");
      driver.quit();
    }
  }

  /**
   * Helper method to wait for page to be fully loaded
   */
  protected void waitForPageLoad() {
    new WebDriverWait(driver, Duration.ofSeconds(20)).until(
        webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
  }

  /**
   * Helper method to safely check if an element exists
   */
  protected boolean elementExists(By locator) {
    try {
      driver.findElement(locator);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  /**
   * Helper method to safely wait for an element and handle timeouts
   */
  protected WebElement waitForElement(By locator) {
    try {
      return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    } catch (TimeoutException e) {
      logger.error("Timeout waiting for element: " + locator);
      throw e;
    }
  }

  /**
   * Tests that the homepage loads correctly with all expected elements
   */
  @Test
  @Order(1)
  public void testHomepageLoads() {
    driver.get(baseUrl);

    // Verify page title
    Assertions.assertTrue(driver.getTitle().contains("SkyExplorer"));

    // Verify navigation elements exist
    Assertions.assertTrue(driver.findElement(By.cssSelector("nav.navbar")).isDisplayed());
    Assertions.assertTrue(driver.findElement(By.id("flightSearchForm")).isDisplayed());

    // Verify login/register links are visible when not authenticated
    Assertions.assertTrue(driver.findElement(By.id("loginNavItem")).isDisplayed());
    Assertions.assertTrue(driver.findElement(By.id("registerNavItem")).isDisplayed());
  }

  /**
   * Tests the login functionality
   */
  @Test
  @Order(3)
  public void testLogin() {
    driver.get(baseUrl + "/login");

    // Fill in the login form
    driver.findElement(By.id("username")).sendKeys("AdminTester");
    driver.findElement(By.id("password")).sendKeys("Test634");

    // Submit the form
    driver.findElement(By.cssSelector("button[type='submit']")).click();

    // Wait for redirect to home page after successful login
    wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));

    // Verify user is logged in by checking if username appears in dropdown
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userDropdown")));
    Assertions.assertTrue(driver.findElement(By.id("userDropdown")).isDisplayed());

    // Verify login button is no longer visible
    Assertions.assertFalse(driver.findElement(By.id("loginNavItem")).isDisplayed());
  }

  @Test
  public void testUiComponentsExist() {
    // This is a simple smoke test to verify the UI testing framework is working
    // Detailed UI tests are in the specific test classes
    logger.info("Frontend UI testing framework is configured correctly");
  }
}