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
 * Tests for the login page functionality
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginPageTest extends FrontendUITest {

    @BeforeEach
    @Override
    public void setupTest() {
        super.setupTest();
        driver.get(baseUrl + "/login");
        UITestHelper.waitForPageLoad(driver);
    }

    @Test
    public void testLoginPageLoads() {
        // Verify login page elements
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        Assertions.assertTrue(usernameField.isDisplayed(), "Username field should be displayed");
        Assertions.assertTrue(passwordField.isDisplayed(), "Password field should be displayed");
        Assertions.assertTrue(submitButton.isDisplayed(), "Login button should be displayed");

        // Verify page title
        Assertions.assertTrue(driver.getTitle().contains("Login"),
                "Page title should contain 'Login'");
    }

    @Test
    public void testLoginFailsWithInvalidCredentials() {
        // Fill in login form with invalid credentials
        driver.findElement(By.id("username")).sendKeys("invalid_user");
        driver.findElement(By.id("password")).sendKeys("invalid_password");

        // Submit form
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for error message or redirect
        UITestHelper.waitForPageLoad(driver);

        // Check for error message
        boolean hasError = driver.getCurrentUrl().contains("error") ||
                UITestHelper.elementExists(driver, By.cssSelector(".alert-danger"));

        Assertions.assertTrue(hasError, "Error message should be displayed for invalid credentials");
    }

    @Test
    public void testRememberMeFunctionality() {
        // Check if remember-me checkbox exists
        if (!UITestHelper.elementExists(driver, By.id("remember-me"))) {
            // If remember-me functionality isn't implemented, skip test
            return;
        }

        // Click remember-me checkbox
        WebElement rememberMeCheckbox = driver.findElement(By.id("remember-me"));
        rememberMeCheckbox.click();

        // Verify checkbox is checked
        Assertions.assertTrue(rememberMeCheckbox.isSelected(),
                "Remember me checkbox should be selected after clicking it");

        // Additional verification would require creating a real user and validating
        // that a persistent cookie is created after successful login
    }

    @Test
    public void testFormValidation() {
        // Submit empty form
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Check for validation messages
        boolean hasValidationMessage = UITestHelper.elementExists(driver, By.cssSelector(".is-invalid")) ||
                UITestHelper.elementExists(driver, By.cssSelector(".invalid-feedback")) ||
                driver.findElement(By.id("username")).getAttribute("validationMessage").length() > 0;

        Assertions.assertTrue(hasValidationMessage,
                "Validation message should appear when submitting empty form");
    }
}