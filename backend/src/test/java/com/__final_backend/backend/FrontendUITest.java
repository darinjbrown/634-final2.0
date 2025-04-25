package com.__final_backend.backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FrontendUITest {

  private WebDriver driver;

  @BeforeEach
  void setUp() {
    // Set the path to the ChromeDriver executable
    System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
    driver = new ChromeDriver();
  }

  @AfterEach
  void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  void testIndexPageTitle() {
    driver.get("http://localhost:8080");
    String title = driver.getTitle();
    assertEquals("Expected Title", title);
  }

  @Test
  void testButtonClick() {
    driver.get("http://localhost:8080");
    WebElement button = driver.findElement(By.id("button-id"));
    button.click();
    WebElement result = driver.findElement(By.id("result-id"));
    assertEquals("Expected Result", result.getText());
  }
}