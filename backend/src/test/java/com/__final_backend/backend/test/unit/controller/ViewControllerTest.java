package com.__final_backend.backend.test.unit.controller;

import com.__final_backend.backend.controller.ViewController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Unit tests for the ViewController class.
 * Tests the mapping of URL paths to their corresponding view templates.
 */
public class ViewControllerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    ViewController viewController = new ViewController();
    mockMvc = MockMvcBuilders.standaloneSetup(viewController).build();
  }

  /**
   * Test that the root URL maps to the index view.
   */
  @Test
  void testIndexMapping() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"));
  }

  /**
   * Test that the /bookings/new URL maps to the bookings view.
   */
  @Test
  void testNewBookingMapping() throws Exception {
    mockMvc.perform(get("/bookings/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("bookings"));
  }
}
