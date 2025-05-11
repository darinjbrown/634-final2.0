package com.__final_backend.backend.test.ui.pages;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import java.util.concurrent.TimeUnit;

/**
 * Tests the HTML templates and static content of the SkyExplorer application
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TemplateValidationTest {

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
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  @BeforeEach
  public void setupTest() {
    baseUrl = "http://localhost:" + port;
    driver.manage().deleteAllCookies();
  }

  @AfterAll
  public static void teardown() {
    if (driver != null) {
      driver.quit();
    }
  }

  /**
   * Tests that the index.html template loads correctly and contains all required
   * elements
   */
  @Test
  @Order(1)
  public void testIndexTemplateRenders() {
    driver.get(baseUrl);
    UITestHelper.waitForPageLoad(driver);

    // Check page title
    Assertions.assertEquals("SkyExplorer - Flight Search", driver.getTitle());

    // Check navbar
    WebElement navbar = driver.findElement(By.cssSelector("nav.navbar"));
    Assertions.assertTrue(navbar.isDisplayed());
    Assertions.assertTrue(navbar.getText().contains("SkyExplorer"));

    // Check hero section
    WebElement heroSection = driver.findElement(By.cssSelector(".p-5.text-center"));
    Assertions.assertTrue(heroSection.isDisplayed());
    Assertions.assertTrue(heroSection.getText().contains("Find Your Perfect Flight"));

    // Check search form
    Assertions.assertTrue(driver.findElement(By.id("flightSearchForm")).isDisplayed());

    // Check footer
    WebElement footer = driver.findElement(By.tagName("footer"));
    Assertions.assertTrue(footer.isDisplayed());
    Assertions.assertTrue(footer.getText().contains("SkyExplorer"));
    Assertions.assertTrue(footer.getText().contains("All rights reserved"));
  }

  /**
   * Tests that the login.html template loads correctly and contains all required
   * elements
   */
  @Test
  @Order(2)
  public void testLoginTemplateRenders() {
    driver.get(baseUrl + "/login");
    UITestHelper.waitForPageLoad(driver);

    // Check page title
    Assertions.assertTrue(driver.getTitle().contains("Login"));

    // Check login form
    WebElement loginForm = driver.findElement(By.id("loginForm"));
    Assertions.assertTrue(loginForm.isDisplayed());
    Assertions.assertTrue(loginForm.findElement(By.id("username")).isDisplayed());
    Assertions.assertTrue(loginForm.findElement(By.id("password")).isDisplayed());
    Assertions.assertTrue(loginForm.findElement(By.id("rememberMe")).isDisplayed());

    // Check register link
    WebElement registerLink = driver.findElement(By.cssSelector("a[href='/register']"));
    Assertions.assertTrue(registerLink.isDisplayed());
    Assertions.assertTrue(registerLink.getText().contains("Register"));

    // Check footer
    WebElement footer = driver.findElement(By.tagName("footer"));
    Assertions.assertTrue(footer.isDisplayed());
    Assertions.assertTrue(footer.getText().contains("SkyExplorer"));
  }

  /**
   * Tests that the register.html template loads correctly and contains all
   * required elements
   */
  @Test
  @Order(3)
  public void testRegisterTemplateRenders() {
    driver.get(baseUrl + "/register");
    UITestHelper.waitForPageLoad(driver);

    // Check page title
    Assertions.assertTrue(driver.getTitle().contains("Register"));

    // Check registration form
    WebElement registerForm = driver.findElement(By.id("registerForm"));
    Assertions.assertTrue(registerForm.isDisplayed());
    Assertions.assertTrue(registerForm.findElement(By.id("username")).isDisplayed());
    Assertions.assertTrue(registerForm.findElement(By.id("email")).isDisplayed());
    Assertions.assertTrue(registerForm.findElement(By.id("password")).isDisplayed());
    Assertions.assertTrue(registerForm.findElement(By.id("confirmPassword")).isDisplayed());
    Assertions.assertTrue(registerForm.findElement(By.id("firstName")).isDisplayed());
    Assertions.assertTrue(registerForm.findElement(By.id("lastName")).isDisplayed());
    Assertions.assertTrue(registerForm.findElement(By.id("termsAgreed")).isDisplayed());

    // Check login link
    WebElement loginLink = driver.findElement(By.cssSelector("a[href='/login']"));
    Assertions.assertTrue(loginLink.isDisplayed());
    Assertions.assertTrue(loginLink.getText().contains("Login"));

    // Check footer
    WebElement footer = driver.findElement(By.tagName("footer"));
    Assertions.assertTrue(footer.isDisplayed());
  }

  /**
   * Tests the CSS styling and visual appearance
   */
  @Test
  @Order(4)
  public void testCssStylesApplied() {
    driver.get(baseUrl);
    UITestHelper.waitForPageLoad(driver);

    // Check that navbar has the primary background color
    WebElement navbar = driver.findElement(By.cssSelector("nav.navbar"));
    String navbarBgColor = navbar.getCssValue("background-color");
    Assertions.assertNotNull(navbarBgColor);

    // Check that the search form is in a card with shadow
    WebElement card = driver.findElement(By.cssSelector(".card"));
    String boxShadow = card.getCssValue("box-shadow");
    Assertions.assertFalse(boxShadow.isEmpty());

    // Check card header has the primary color
    WebElement cardHeader = driver.findElement(By.cssSelector(".card-header"));
    String headerBgColor = cardHeader.getCssValue("background-color");
    Assertions.assertNotNull(headerBgColor);
  }

  /**
   * Tests the responsive behavior of the templates
   */
  @Test
  @Order(6)
  public void testResponsiveDesign() {
    driver.get(baseUrl);
    UITestHelper.waitForPageLoad(driver);

    // Test mobile viewport
    driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 667));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".navbar-toggler")));
    Assertions.assertTrue(driver.findElement(By.cssSelector(".navbar-toggler")).isDisplayed(),
        "Navbar toggler should be visible on mobile viewport");

    // Test tablet viewport
    driver.manage().window().setSize(new org.openqa.selenium.Dimension(768, 1024));

    // Test desktop viewport
    driver.manage().window().setSize(new org.openqa.selenium.Dimension(1200, 800));

    // Navbar should be expanded on desktop
    try {
      List<WebElement> navItems = driver.findElements(By.cssSelector(".navbar-nav .nav-item"));
      boolean foundVisibleNavItem = false;
      for (WebElement item : navItems) {
        if (item.isDisplayed() && !item.getAttribute("class").contains("d-none")) {
          // At least one nav item is visible
          foundVisibleNavItem = true;
          break;
        }
      }
      Assertions.assertTrue(foundVisibleNavItem, "At least one navbar item should be visible in desktop viewport");
    } catch (Exception e) {
      Assertions.fail("Error checking responsive design: " + e.getMessage());
    }
  }
}