package com.__final_backend.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing flight search request parameters.
 * <p>
 * This class encapsulates search criteria for flights including locations,
 * dates,
 * number of travelers, and trip type. It is used for processing flight search
 * requests
 * through the API and includes validation constraints for the input fields.
 * </p>
 */
public class FlightSearchRequestDTO {
    /**
     * The starting location/airport code for the flight search.
     * Must be a valid 3-letter IATA airport code.
     */
    @NotNull
    @Size(min = 3, max = 3)
    private String startingLocation;

    /**
     * The destination location/airport code for the flight search.
     * Must be a valid 3-letter IATA airport code.
     */
    @NotNull
    @Size(min = 3, max = 3)
    private String endingLocation;

    /**
     * The departure date for the flight.
     * Formatted as yyyy-MM-dd for JSON serialization/deserialization.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate travelDate;
    /**
     * The return date for the flight (applicable for round-trip).
     * Formatted as yyyy-MM-dd for JSON serialization/deserialization.
     * May be null for one-way trips.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;

    /**
     * The number of passengers traveling.
     * Must be a positive integer.
     */
    @NotNull
    private Integer numberOfTravelers;

    /**
     * The type of trip.
     * Expected values include "one-way" or "round-trip".
     */
    @NotNull
    private String tripType;

    /**
     * Gets the starting location.
     *
     * @return the starting location/airport code (3-letter IATA code)
     */
    public String getStartingLocation() {
        return startingLocation;
    }

    /**
     * Sets the starting location.
     *
     * @param startingLocation the starting location/airport code (3-letter IATA
     *                         code)
     */
    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    /**
     * Gets the ending location.
     *
     * @return the ending location/airport code (3-letter IATA code)
     */
    public String getEndingLocation() {
        return endingLocation;
    }

    /**
     * Sets the ending location.
     *
     * @param endingLocation the ending location/airport code (3-letter IATA code)
     */
    public void setEndingLocation(String endingLocation) {
        this.endingLocation = endingLocation;
    }

    /**
     * Gets the travel date.
     *
     * @return the departure date for the flight
     */
    public LocalDate getTravelDate() {
        return travelDate;
    }

    /**
     * Sets the travel date.
     *
     * @param travelDate the departure date for the flight
     */
    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    /**
     * Gets the return date.
     *
     * @return the return date for round-trip flights, may be null for one-way trips
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date.
     *
     * @param returnDate the return date for round-trip flights, may be null for
     *                   one-way trips
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Gets the number of travelers.
     *
     * @return the number of passengers traveling
     */
    public Integer getNumberOfTravelers() {
        return numberOfTravelers;
    }

    /**
     * Sets the number of travelers.
     *
     * @param numberOfTravelers the number of passengers traveling
     */
    public void setNumberOfTravelers(Integer numberOfTravelers) {
        this.numberOfTravelers = numberOfTravelers;
    }

    /**
     * Gets the trip type.
     *
     * @return the type of trip (e.g., "one-way", "round-trip")
     */
    public String getTripType() {
        return tripType;
    }

    /**
     * Sets the trip type.
     *
     * @param tripType the type of trip (e.g., "one-way", "round-trip")
     */
    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    @Override
    public String toString() {
        return "FlightSearchRequestDTO{" +
                "startingLocation='" + startingLocation + '\'' +
                ", endingLocation='" + endingLocation + '\'' +
                ", travelDate=" + travelDate +
                ", returnDate=" + returnDate +
                ", numberOfTravelers=" + numberOfTravelers +
                ", tripType='" + tripType + '\'' +
                '}';
    }
}