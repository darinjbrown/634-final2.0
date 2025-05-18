package com.__final_backend.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the SkyExplorer application.
 * <p>
 * This class provides centralized exception handling across all controllers in
 * the application.
 * It intercepts exceptions thrown during request processing and translates them
 * into
 * appropriate HTTP responses with structured error information.
 * </p>
 * <p>
 * Exception handling helps provide consistent error responses to clients and
 * avoids
 * exposing sensitive implementation details when errors occur.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {
  /**
   * Handles validation exceptions from request body parameter validation.
   * <p>
   * This method processes exceptions thrown when request parameters fail
   * validation
   * constraints (such as @NotNull, @Size, etc.). It extracts all field-level
   * validation
   * errors and returns them in a structured format.
   * </p>
   *
   * @param ex the validation exception containing binding results with field
   *           errors
   * @return ResponseEntity with map of field names to error messages and HTTP 400
   *         status
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles all other unhandled exceptions.
   * <p>
   * This is a fallback exception handler that processes any exceptions not caught
   * by more specific handlers. It provides a generic error response without
   * exposing
   * implementation details.
   * </p>
   *
   * @param ex the exception that was thrown during request processing
   * @return ResponseEntity with error message and HTTP 500 status
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGeneralExceptions(Exception ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}