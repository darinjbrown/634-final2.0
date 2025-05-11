package com.__final_backend.backend.controller;

/**
 * Controller for handling view-related requests.
 * Maps the root URL to the index.html template.
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

  @GetMapping("/")
  public String index() {
    return "index"; // Maps to src/main/resources/templates/index.html
  }

  @GetMapping("/login")
  public String login() {
    return "login"; // Maps to src/main/resources/templates/login.html
  }

  @GetMapping("/register")
  public String register() {
    return "register"; // Maps to src/main/resources/templates/register.html
  }
}