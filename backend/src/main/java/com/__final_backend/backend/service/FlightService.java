package com.__final_backend.backend.service;

import com.__final_backend.backend.dto.FlightDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for flight-related operations.
 * Provides methods to search for flights based on various criteria.
 */
public interface FlightService {
    List<FlightDTO> searchFlights(
            String startingLocation,
            String endingLocation,
            LocalDate travelDate,
            LocalDate returnDate,
            Integer numberOfTravelers,
            String tripType);
}