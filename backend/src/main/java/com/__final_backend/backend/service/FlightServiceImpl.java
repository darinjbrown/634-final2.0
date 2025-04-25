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
 * Implementation of the FlightService interface that uses the Amadeus API
 * to search for flight offers and convert them into application-specific DTOs.
 * <p>
 * This service handles communication with the Amadeus flight search API,
 * processes the responses, and maps the data to our internal flight data model.
 */
@Service
public class FlightServiceImpl implements FlightService {

    /** Logger for this class */
    private static final Logger logger = LoggerFactory.getLogger(FlightServiceImpl.class);

    /** Amadeus API key from application properties */
    @Value("${amadeus.api.key}")
    private String apiKey;

    /** Amadeus API secret from application properties */
    @Value("${amadeus.api.secret}")
    private String apiSecret;

    /** Cache to store airline codes and their corresponding names */
    private Map<String, String> airlineCache = new HashMap<>();

    /**
     * Creates and returns an Amadeus API client with configured credentials.
     *
     * @return An initialized Amadeus client
     */
    protected Amadeus getAmadeusClient() {
        return Amadeus.builder(apiKey, apiSecret).build();
    }

    /**
     * Searches for flights based on the provided parameters using the Amadeus API.
     * <p>
     * This method validates input parameters, builds the API request, executes it,
     * and processes the response into a list of FlightDTO objects.
     *
     * @param startingLocation  The origin airport code
     * @param endingLocation    The destination airport code
     * @param travelDate        The departure date
     * @param returnDate        The return date (for round-trip flights)
     * @param numberOfTravelers The number of adult passengers
     * @param tripType          The type of trip ("one-way" or "round-trip")
     * @return A list of FlightDTO objects matching the search criteria
     * @throws IllegalArgumentException If required parameters are missing
     * @throws RuntimeException         If there is an API error or unexpected
     *                                  exception
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
            // Validate mandatory parameters
            if (startingLocation == null || endingLocation == null || travelDate == null || numberOfTravelers == null) {
                logger.error("Missing required parameters for flight search");
                throw new IllegalArgumentException(
                        "Missing required parameters: originLocationCode, destinationLocationCode, departureDate, and adults are mandatory");
            }

            logger.info("Searching flights from {} to {} on {}", startingLocation, endingLocation, travelDate);

            Amadeus amadeus = getAmadeusClient();

            // Build mandatory search parameters
            Params params = Params.with("originLocationCode", startingLocation)
                    .and("destinationLocationCode", endingLocation)
                    .and("departureDate", travelDate.toString())
                    .and("adults", numberOfTravelers.toString());

            // Add optional parameters
            if (returnDate != null && "round-trip".equalsIgnoreCase(tripType)) {
                params.and("returnDate", returnDate.toString());
            }

            // Add additional parameters to improve results
            params.and("max", "20")
                    .and("currencyCode", "USD")
                    .and("nonStop", "true");

            // Execute GET API call
            FlightOfferSearch[] flightOffersSearches = amadeus.shopping.flightOffersSearch.get(params);

            logger.info("Retrieved {} flight offers", flightOffersSearches.length);

            if (flightOffersSearches.length == 0) {
                logger.warn("No flights found for the specified criteria");
                return new ArrayList<>();
            }

            // Process the flight offers
            return mapToFlightDTOs(flightOffersSearches, amadeus);

        } catch (ResponseException e) {
            logger.error("Amadeus API error: {} - {}", e.getCode(), e.getMessage(), e);
            throw new RuntimeException("Failed to fetch flight data: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error in flight search: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error in flight search", e);
        }
    }

    /**
     * Maps Amadeus FlightOfferSearch objects to application-specific FlightDTO
     * objects.
     * <p>
     * This method:
     * 1. Extracts airline codes from flight offers
     * 2. Loads airline names for these codes
     * 3. Maps flight segment data to FlightDTO objects
     *
     * @param flightOffers Array of flight offers from Amadeus API
     * @param amadeus      The Amadeus client to fetch additional data
     * @return List of FlightDTO objects with mapped flight information
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
     * Loads airline names for a set of airline codes using Amadeus API.
     * <p>
     * This method queries the Amadeus API for each airline code and stores
     * the results in the airline cache for future use.
     *
     * @param airlineCodes Set of airline codes to lookup
     * @param amadeus      The Amadeus client to use for lookups
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
     * Extracts a formatted time string (HH:mm) from a datetime string.
     * <p>
     * This method attempts to parse the input string as a ZonedDateTime or
     * LocalDateTime,
     * and falls back to direct string manipulation if parsing fails.
     *
     * @param dateTimeString The datetime string to extract time from
     * @return A formatted time string in "HH:mm" format
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
}