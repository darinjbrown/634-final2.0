package com.__final_backend.backend.service;

import com.__final_backend.backend.dto.FlightDTO;
import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Airline;
import com.amadeus.resources.FlightOfferSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the FlightService interface that handles communication
 * with the Amadeus Flight Offers Search API.
 * 
 * This service provides methods to search for flights, convert API responses
 * to DTOs, and handle airline information caching.
 */
@Service
public class FlightServiceImpl implements FlightService {

    private static final Logger logger = LoggerFactory.getLogger(FlightServiceImpl.class);

    @Value("${amadeus.api.key}")
    private String apiKey;

    @Value("${amadeus.api.secret}")
    private String apiSecret;

    /**
     * Cache for airline names to reduce API calls for repeated airline codes.
     * Key: Airline code (e.g., "BA")
     * Value: Airline name (e.g., "British Airways")
     */
    private Map<String, String> airlineCache = new HashMap<>();

    /**
     * Singleton instance of the Amadeus API client
     */
    private Amadeus amadeusClient;

    /**
     * Gets or creates the Amadeus API client in a thread-safe manner.
     * Uses the test environment for development and testing purposes.
     * 
     * @return Configured Amadeus client instance
     */
    protected synchronized Amadeus getAmadeusClient() {
        if (amadeusClient != null) {
            return amadeusClient;
        }

        // Test environment is free tier and sufficient for this project
        amadeusClient = Amadeus.builder(apiKey, apiSecret)
                .setHostname("test")
                .setLogLevel("debug") // Keep debug for troubleshooting
                .setSsl(true) // Ensure SSL is enabled
                .build();

        logger.info("Amadeus client created with test environment");
        return amadeusClient;
    }

    /**
     * Searches for flights based on the provided parameters.
     * 
     * @param startingLocation  The 3-letter IATA code of the departure airport/city
     * @param endingLocation    The 3-letter IATA code of the arrival airport/city
     * @param travelDate        The date of departure
     * @param returnDate        The date of return (for round trips only)
     * @param numberOfTravelers The number of adult travelers
     * @param tripType          The type of trip, either "one-way" or "round-trip"
     * @return List of FlightDTO objects representing matching flights
     * @throws IllegalArgumentException if required parameters are invalid or
     *                                  missing
     * @throws RuntimeException         if there's an error communicating with the
     *                                  Amadeus API
     */
    @Override
    public List<FlightDTO> searchFlights(
            String startingLocation,
            String endingLocation,
            LocalDate travelDate,
            LocalDate returnDate,
            Integer numberOfTravelers,
            String tripType) {

        try {
            // Parameter validation
            if (startingLocation == null || !startingLocation.matches("[A-Z]{3}") ||
                    endingLocation == null || !endingLocation.matches("[A-Z]{3}") ||
                    travelDate == null || numberOfTravelers == null || numberOfTravelers < 1) {
                throw new IllegalArgumentException(
                        "Invalid parameters: origin, destination, date and adults are required");
            }

            // Get API client
            Amadeus amadeus = getAmadeusClient();

            // Build params for the API call
            Params params = Params.with("originLocationCode", startingLocation)
                    .and("destinationLocationCode", endingLocation)
                    .and("departureDate", travelDate.toString())
                    .and("adults", numberOfTravelers.toString())
                    .and("max", "20")
                    .and("currencyCode", "USD") // Add currency for better results
                    .and("nonStop", "true"); // Filter for non-stop flights only

            if (returnDate != null && "round-trip".equals(tripType)) {
                params.and("returnDate", returnDate.toString())
                        .and("nonStop", "true");
            }

            // Log the full request parameters for debugging
            logger.info("Searching flights with params: {}", params);

            // Make the API call with more detailed error handling
            try {
                FlightOfferSearch[] flightOffersSearches = amadeus.shopping.flightOffersSearch.get(params);
                logger.info("Successfully retrieved {} flight offers", flightOffersSearches.length);
                return mapToFlightDTOs(flightOffersSearches, amadeus);
            } catch (ResponseException e) {
                logger.error("Amadeus API error: {}", e);
                logger.error("Error details - Code: {}, Error: {}, Description: {}",
                        e.getCode(), e.getMessage(), e.getDescription());
                throw new RuntimeException("Error from Amadeus API: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error("Error searching flights: {}", e.getMessage());
            throw new RuntimeException("Error searching flights: " + e.getMessage(), e);
        }
    }

    /**
     * Converts FlightOfferSearch objects from the Amadeus API to FlightDTO objects
     * that can be consumed by the frontend.
     * 
     * @param flightOffers Array of FlightOfferSearch objects from Amadeus API
     * @param amadeus      The Amadeus client for fetching airline information
     * @return List of FlightDTO objects with flight details
     */
    private List<FlightDTO> mapToFlightDTOs(FlightOfferSearch[] flightOffers, Amadeus amadeus) {
        List<FlightDTO> flightDTOs = new ArrayList<>();
        Set<String> airlineCodes = new HashSet<>();

        // Extract airline codes first
        for (FlightOfferSearch offer : flightOffers) {
            for (FlightOfferSearch.Itinerary itinerary : offer.getItineraries()) {
                for (FlightOfferSearch.SearchSegment segment : itinerary.getSegments()) {
                    airlineCodes.add(segment.getCarrierCode());
                }
            }
        }

        // Load airline names
        loadAirlineNames(airlineCodes, amadeus);

        // Map flight offers to DTOs
        for (FlightOfferSearch offer : flightOffers) {
            double price = Double.parseDouble(offer.getPrice().getTotal());

            for (FlightOfferSearch.Itinerary itinerary : offer.getItineraries()) {
                for (FlightOfferSearch.SearchSegment segment : itinerary.getSegments()) {
                    FlightDTO dto = new FlightDTO();

                    String airlineCode = segment.getCarrierCode();
                    dto.setAirline(airlineCache.getOrDefault(airlineCode, airlineCode));
                    dto.setFlightNumber(airlineCode + segment.getNumber());

                    dto.setDeparture(segment.getDeparture().getIataCode());
                    dto.setArrival(segment.getArrival().getIataCode());

                    dto.setDepartureTime(extractTimeFromDateTime(segment.getDeparture().getAt()));
                    dto.setArrivalTime(extractTimeFromDateTime(segment.getArrival().getAt()));

                    dto.setPrice(price);

                    flightDTOs.add(dto);
                }
            }
        }

        return flightDTOs;
    }

    /**
     * Fetches airline names for a set of airline codes and caches the results.
     * If an airline name cannot be fetched, the code itself is stored in the cache.
     * 
     * @param airlineCodes Set of airline IATA codes to fetch names for
     * @param amadeus      The Amadeus client for making API calls
     */
    private void loadAirlineNames(Set<String> airlineCodes, Amadeus amadeus) {
        for (String code : airlineCodes) {
            if (airlineCache.containsKey(code)) {
                continue;
            }

            try {
                Airline[] airlines = amadeus.referenceData.airlines.get(
                        Params.with("airlineCodes", code));

                if (airlines.length > 0) {
                    String airlineName = airlines[0].getCommonName();
                    airlineCache.put(code, airlineName);
                } else {
                    airlineCache.put(code, code);
                }
            } catch (Exception e) {
                logger.warn("Failed to get airline name for code {}: {}", code, e.getMessage());
                airlineCache.put(code, code);
            }
        }
    }

    /**
     * Extracts a formatted time string (HH:mm) from an ISO datetime string.
     * Handles different datetime formats including those with and without
     * timezones.
     * 
     * @param dateTimeString The datetime string to extract time from (e.g.,
     *                       "2025-05-01T14:30:00")
     * @return A formatted time string (e.g., "14:30") or "N/A" if parsing fails
     */
    private String extractTimeFromDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return "N/A";
        }

        try {
            // Try to parse as ISO datetime with timezone
            return ZonedDateTime.parse(dateTimeString)
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e1) {
            try {
                // Try to parse as local datetime without timezone
                return LocalDateTime.parse(dateTimeString)
                        .format(DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e2) {
                return dateTimeString.substring(11, 16); // Try to extract time part directly
            }
        }
    }

    /**
     * Advanced search method that allows direct access to all Amadeus API
     * parameters.
     * Useful for complex queries that require parameters not exposed in the
     * standard search method.
     * 
     * @param params Amadeus Params object containing all search parameters
     * @return Array of FlightOfferSearch objects from Amadeus API
     * @throws RuntimeException if there's an error communicating with the Amadeus
     *                          API
     */
    @Override
    public FlightOfferSearch[] searchFlightsWithParams(Params params) {
        try {
            Amadeus amadeus = getAmadeusClient();
            return amadeus.shopping.flightOffersSearch.get(params);
        } catch (ResponseException e) {
            logger.error("Amadeus API error in searchFlightsWithParams: {} - {}", e.getCode(), e.getMessage());
            throw new RuntimeException("Error from Amadeus API: " + e.getMessage(), e);
        }
    }
}