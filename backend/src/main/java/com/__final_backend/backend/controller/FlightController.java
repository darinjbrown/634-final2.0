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
 * REST controller that handles flight-related API endpoints in the SkyExplorer
 * application.
 * 
 * This controller provides endpoints for searching available flights based on
 * user criteria
 * such as origin, destination, travel dates, and number of travelers. It
 * integrates with
 * the FlightService which communicates with the Amadeus API to retrieve
 * real-time flight data.
 * 
 * All endpoints return structured flight information as DTOs (Data Transfer
 * Objects).
 */
@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);

    /**
     * Service that handles flight search operations and communicates with the
     * Amadeus API.
     */
    @Autowired
    private FlightService flightService;

    /**
     * Searches for flights based on provided search criteria.
     * 
     * This endpoint accepts a POST request with flight search parameters and
     * returns
     * a list of matching flights with details like airline, flight number,
     * departure/arrival
     * times, and pricing information.
     *
     * @param request The flight search criteria including origin, destination,
     *                dates, etc.
     * @return ResponseEntity containing a list of flights matching the search
     *         criteria
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

    /**
     * Handles validation exceptions thrown when the request body fails validation.
     * 
     * @param ex The validation exception containing details about validation
     *           failures
     * @return ResponseEntity with HTTP 400 Bad Request status and error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Invalid request payload");
    }
}