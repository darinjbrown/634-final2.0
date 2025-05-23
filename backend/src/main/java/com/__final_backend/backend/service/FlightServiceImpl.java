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
 * Implementation of the FlightService interface that provides flight search
 * capabilities
 * by integrating with the Amadeus Flight Offers Search API.
 * <p>
 * This service is responsible for:
 * <ul>
 * <li>Creating and managing the Amadeus API client
 * <li>Searching for flights based on user criteria (origin, destination, dates,
 * etc.)
 * <li>Converting Amadeus API responses to application DTOs for client
 * consumption
 * <li>Caching airline information to optimize API usage and improve performance
 * <li>Handling date/time formatting and validation
 * </ul>
 * <p>
 * The service uses the Amadeus test environment which provides free access to
 * flight data
 * suitable for development and testing purposes.
 */
@Service
public class FlightServiceImpl implements FlightService {
    /** Logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(FlightServiceImpl.class);

    /** The Amadeus API key from application properties. */
    @Value("${amadeus.api.key}")
    private String apiKey;

    /** The Amadeus API secret from application properties. */
    @Value("${amadeus.api.secret}")
    private String apiSecret;

    /**
     * Cache for airline names to reduce API calls for repeated airline codes.
     * <p>
     * This improves performance by avoiding redundant API calls for the same
     * airline.
     * <p>
     * Key: Airline IATA code (e.g., "BA", "AA", "DL")
     * Value: Full airline name (e.g., "British Airways", "American Airlines",
     * "Delta Air Lines")
     */
    private Map<String, String> airlineCache = new HashMap<>();

    /**
     * Singleton instance of the Amadeus API client.
     * <p>
     * Created on-demand and reused across requests for efficiency.
     */
    private Amadeus amadeusClient;

    /**
     * Gets or creates the Amadeus API client in a thread-safe manner.
     * <p>
     * This method implements lazy initialization with synchronization
     * to ensure thread safety while maintaining performance. The client is only
     * created
     * once and then reused for subsequent API calls.
     * 
     * @return Configured Amadeus client instance ready for API calls
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
     * Searches for flights based on the provided travel parameters.
     * <p>
     * This method validates input parameters, constructs the appropriate API
     * request,
     * calls the Amadeus Flight Offers Search API, and transforms the results into
     * application-specific DTOs. It includes robust error handling and logging.
     * <p>
     * The search is limited to non-stop flights and returns at most 20 results
     * to optimize response time and focus on the most relevant options.
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
     * Converts FlightOfferSearch objects from the Amadeus API to FlightDTO objects.
     * <p>
     * This method transforms the complex Amadeus API response structure into a
     * simplified
     * DTO format suitable for frontend consumption. The conversion includes:
     * <ul>
     * <li>Extracting flight details from nested objects
     * <li>Resolving airline codes to full airline names using the airline cache
     * <li>Formatting departure and arrival times
     * <li>Organizing price information
     * </ul>
     * 
     * @param flightOffers Array of FlightOfferSearch objects from Amadeus API
     * @param amadeus      The Amadeus client for fetching airline information
     * @return List of FlightDTO objects with flight details
     */
    private List<FlightDTO> mapToFlightDTOs(FlightOfferSearch[] flightOffers, Amadeus amadeus) {
        List<FlightDTO> flightDTOs = new ArrayList<>();
        Set<String> airlineCodes = new HashSet<>();

        // Extract airline codes first for batch processing
        for (FlightOfferSearch offer : flightOffers) {
            for (FlightOfferSearch.Itinerary itinerary : offer.getItineraries()) {
                for (FlightOfferSearch.SearchSegment segment : itinerary.getSegments()) {
                    airlineCodes.add(segment.getCarrierCode());
                }
            }
        }

        // Load airline names in batch to minimize API calls
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
     * Fetches airline names for a set of airline codes and populates the cache.
     * <p>
     * This method makes batch requests to the Amadeus Airlines API to retrieve
     * airline names for the given IATA codes. Results are stored in the cache
     * to avoid redundant API calls in future requests. If an airline name
     * cannot be fetched, the code itself is used as a fallback.
     * <p>
     * The method checks the cache first before making any API calls to optimize
     * performance and reduce API usage.
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
     * <p>
     * This method handles various datetime formats that might be returned by the
     * API,
     * including those with and without timezone information. It attempts multiple
     * parsing
     * strategies in this order:
     * <ol>
     * <li>Parse as ZonedDateTime (with timezone)
     * <li>Parse as LocalDateTime (without timezone)
     * <li>Direct substring extraction as fallback
     * </ol>
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
     * Advanced flight search method that accepts raw Amadeus API parameters.
     * <p>
     * This method provides direct access to all Amadeus API parameters for advanced
     * search scenarios not covered by the standard search method. It's particularly
     * useful for complex queries that require parameters not exposed in the
     * standard
     * interface, such as:
     * <ul>
     * <li>Fare family filtering
     * <li>Cabin class restrictions
     * <li>Airline alliance preferences
     * <li>Complex multi-city itineraries
     * </ul>
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