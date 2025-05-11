package com.__final_backend.backend.controller;

import com.__final_backend.backend.dto.FlightDTO;
import com.__final_backend.backend.dto.FlightSearchRequestDTO;
import com.__final_backend.backend.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller that handles flight-related API endpoints.
 * Provides functionality for searching available flights based on various
 * criteria.
 */
@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);

    /**
     * Service that handles flight search operations.
     */
    @Autowired
    private FlightService flightService;

    /**
     * Searches for flights based on provided search criteria.
     */
    @PostMapping("/search")
    public ResponseEntity<List<FlightDTO>> searchFlights(@RequestBody @Valid FlightSearchRequestDTO request) {
        logger.info("Searching flights with request: {}", request);
        List<FlightDTO> results = flightService.searchFlights(
                request.getStartingLocation(),
                request.getEndingLocation(),
                request.getTravelDate(),
                request.getReturnDate(),
                request.getNumberOfTravelers(),
                request.getTripType());
        return ResponseEntity.ok(results);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Invalid request payload");
    }
}