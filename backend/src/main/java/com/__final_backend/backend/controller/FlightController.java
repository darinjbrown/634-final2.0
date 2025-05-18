package com.__final_backend.backend.controller;

import com.__final_backend.backend.dto.FlightDTO;
import com.__final_backend.backend.dto.FlightSearchRequestDTO;
import com.__final_backend.backend.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller that handles flight-related API endpoints in the SkyExplorer
 * application.
 * <p>
 * This controller provides endpoints for searching available flights based on
 * user criteria
 * such as origin, destination, travel dates, and number of travelers. It
 * integrates with
 * the FlightService which communicates with the Amadeus API to retrieve
 * real-time flight data.
 * </p>
 * <p>
 * All endpoints return structured flight information as DTOs (Data Transfer
 * Objects).
 * </p>
 */
@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);

    private final FlightService flightService;

    /**
     * Constructs a FlightController with the required dependencies.
     *
     * @param flightService service that handles flight search operations and
     *                      communicates with the Amadeus API
     */
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * Searches for flights based on provided search criteria.
     * <p>
     * This endpoint accepts a POST request with flight search parameters and
     * returns
     * a list of matching flights with details like airline, flight number,
     * departure/arrival
     * times, and pricing information.
     * </p>
     *
     * @param request the flight search criteria including origin, destination,
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
     * <p>
     * This method catches validation errors that occur when the flight search
     * request
     * doesn't meet the defined validation constraints and returns an appropriate
     * error message.
     * </p>
     * 
     * @param ex the validation exception containing details about validation
     *           failures
     * @return ResponseEntity with HTTP 400 Bad Request status and error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Invalid request payload");
    }
}