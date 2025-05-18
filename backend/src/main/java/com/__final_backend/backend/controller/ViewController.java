package com.__final_backend.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling view-related requests in the SkyExplorer application.
 * <p>
 * This controller maps URL paths to the corresponding Thymeleaf templates,
 * facilitating server-side rendering of HTML pages.
 * </p>
 * <p>
 * The controller follows Spring MVC conventions for view resolution, where
 * return strings are resolved to templates in the src/main/resources/templates
 * directory.
 * </p>
 */
@Controller
public class ViewController {
  /**
   * Maps the root URL ("/") to the index page.
   * <p>
   * This endpoint serves as the landing page for the SkyExplorer application.
   * </p>
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
   * <p>
   * This endpoint provides the authentication interface for users to sign in
   * to the SkyExplorer application.
   * </p>
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
   * <p>
   * This endpoint provides the user registration interface for creating new
   * accounts in the SkyExplorer application.
   * </p>
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
   * <p>
   * This endpoint provides access to the user's saved flight search results and
   * favorite flights for future reference and booking.
   * </p>
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
   * <p>
   * This endpoint allows users to view and manage their flight bookings,
   * including booking details, status, and options for modification.
   * </p>
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
   * <p>
   * This route is used when creating a new booking from a saved flight.
   * It directs users to the booking interface with the appropriate context
   * for initiating a new reservation process.
   * </p>
   *
   * @return String template name "bookings" which resolves to the bookings page
   *         with the appropriate context for creating a new booking
   */
  @GetMapping("/bookings/new")
  public String newBooking() {
    return "bookings";
  }
}