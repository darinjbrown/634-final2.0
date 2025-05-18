package com.__final_backend.backend.service;

import com.__final_backend.backend.dto.FlightDTO;
import com.amadeus.Params;
import com.amadeus.resources.FlightOfferSearch;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for flight-related operations.
 * <p>
 * This interface defines the contract for flight search operations within the
 * application.
 * It provides methods to search for flights based on various criteria such as
 * origin,
 * destination, dates, and passenger count.
 * <p>
 * The implementation integrates with external flight data providers (such as
 * Amadeus)
 * to retrieve real-time flight information for users.
 */
public interface FlightService {
    /**
     * Searches for flights based on the provided travel parameters.
     * <p>
     * This method searches for available flights matching the specified criteria.
     * It returns flight information in a standardized DTO format that can be
     * easily consumed by frontend clients.
     *
     * @param startingLocation  The 3-letter IATA code of the departure airport/city
     * @param endingLocation    The 3-letter IATA code of the arrival airport/city
     * @param travelDate        The date of departure
     * @param returnDate        The date of return (for round trips only, can be
     *                          null for one-way trips)
     * @param numberOfTravelers The number of adult travelers
     * @param tripType          The type of trip, either "one-way" or "round-trip"
     * @return List of FlightDTO objects representing matching flights
     * @throws IllegalArgumentException if required parameters are invalid or
     *                                  missing
     * @throws RuntimeException         if there's an error communicating with the
     *                                  flight data provider
     */
    List<FlightDTO> searchFlights(
            String startingLocation,
            String endingLocation,
            LocalDate travelDate,
            LocalDate returnDate,
            Integer numberOfTravelers,
            String tripType);

    /**
     * Searches for flights using raw query parameters.
     * <p>
     * This advanced method provides direct access to all flight search parameters
     * supported by the underlying flight data provider. It's particularly useful
     * for
     * complex queries that require parameters not exposed in the standard search
     * method,
     * such as:
     * <ul>
     * <li>Fare family filtering
     * <li>Cabin class restrictions
     * <li>Airline alliance preferences
     * <li>Complex multi-city itineraries
     * </ul>
     *
     * @param params The query parameters for the flight search
     * @return An array of FlightOfferSearch objects containing detailed flight
     *         information
     * @throws RuntimeException if there's an error communicating with the flight
     *                          data provider
     */
    FlightOfferSearch[] searchFlightsWithParams(Params params);
}