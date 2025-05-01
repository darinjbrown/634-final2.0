package com.__final_backend.backend.service;

import com.__final_backend.backend.dto.FlightDTO;
import com.amadeus.Params;
import com.amadeus.resources.FlightOfferSearch;
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

    /**
     * Searches for flights using query parameters.
     *
     * @param params The query parameters for the flight search
     * @return An array of FlightOfferSearch objects
     */
    FlightOfferSearch[] searchFlightsWithParams(Params params);
}