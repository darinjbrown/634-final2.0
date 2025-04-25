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
}