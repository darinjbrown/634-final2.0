package com.__final_backend.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling view-related requests in the SkyExplorer application.
 * This controller maps URL paths to the corresponding Thymeleaf templates,
 * facilitating server-side rendering of HTML pages.
 * 
 * The controller follows Spring MVC conventions for view resolution, where
 * return strings are resolved to templates in the src/main/resources/templates
 * directory.
 */
@Controller
public class ViewController {

  /**
   * Maps the root URL ("/") to the index page.
   * 
   * @return String template name "index" which resolves to
   *         src/main/resources/templates/index.html
   */
  @GetMapping("/")
  public String index() {
    return "index";
  }

  /**
   * Maps the "/login" URL to the login page.
   * 
   * @return String template name "login" which resolves to
   *         src/main/resources/templates/login.html
   */
  @GetMapping("/login")
  public String login() {
    return "login";
  }

  /**
   * Maps the "/register" URL to the registration page.
   * 
   * @return String template name "register" which resolves to
   *         src/main/resources/templates/register.html
   */
  @GetMapping("/register")
  public String register() {
    return "register";
  }

  /**
   * Maps the "/saved-flights" URL to the saved flights page.
   * 
   * @return String template name "saved-flights" which resolves to
   *         src/main/resources/templates/saved-flights.html
   */
  @GetMapping("/saved-flights")
  public String savedFlights() {
    return "saved-flights";
  }

  /**
   * Maps the "/bookings" URL to the bookings page.
   * 
   * @return String template name "bookings" which resolves to
   *         src/main/resources/templates/bookings.html
   */
  @GetMapping("/bookings")
  public String bookings() {
    return "bookings";
  }

  /**
   * Maps the "/bookings/new" URL to the booking creation page.
   * This route is used when creating a new booking from a saved flight.
   * 
   * @return String template name "bookings" with a query parameter for the flight
   *         ID
   */
  @GetMapping("/bookings/new")
  public String newBooking() {
    return "bookings";
  }
}