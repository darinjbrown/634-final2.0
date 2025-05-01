package com.__final_backend.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing flight search request parameters.
 * This class encapsulates search criteria for flights including locations,
 * dates,
 * number of travelers, and trip type.
 */
public class FlightSearchRequestDTO {
    /** The starting location/airport code for the flight search */
    @NotNull
    @Size(min = 3, max = 3)
    private String startingLocation;

    /** The destination location/airport code for the flight search */
    @NotNull
    @Size(min = 3, max = 3)
    private String endingLocation;

    /** The departure date for the flight */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate travelDate;

    /** The return date for the flight (applicable for round-trip) */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;

    /** The number of passengers traveling */
    @NotNull
    private Integer numberOfTravelers;

    /** The type of trip (e.g., "one-way", "round-trip") */
    @NotNull
    private String tripType;

    /**
     * Gets the starting location.
     * 
     * @return The starting location/airport code
     */
    public String getStartingLocation() {
        return startingLocation;
    }

    /**
     * Sets the starting location.
     * 
     * @param startingLocation The starting location/airport code
     */
    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    /**
     * Gets the ending location.
     * 
     * @return The ending location/airport code
     */
    public String getEndingLocation() {
        return endingLocation;
    }

    /**
     * Sets the ending location.
     * 
     * @param endingLocation The ending location/airport code
     */
    public void setEndingLocation(String endingLocation) {
        this.endingLocation = endingLocation;
    }

    /**
     * Gets the travel date.
     * 
     * @return The departure date for the flight
     */
    public LocalDate getTravelDate() {
        return travelDate;
    }

    /**
     * Sets the travel date.
     * 
     * @param travelDate The departure date for the flight
     */
    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    /**
     * Gets the return date.
     * 
     * @return The return date for round-trip flights
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date.
     * 
     * @param returnDate The return date for round-trip flights
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Gets the number of travelers.
     * 
     * @return The number of passengers traveling
     */
    public Integer getNumberOfTravelers() {
        return numberOfTravelers;
    }

    /**
     * Sets the number of travelers.
     * 
     * @param numberOfTravelers The number of passengers traveling
     */
    public void setNumberOfTravelers(Integer numberOfTravelers) {
        this.numberOfTravelers = numberOfTravelers;
    }

    /**
     * Gets the trip type.
     * 
     * @return The type of trip (e.g., "one-way", "round-trip")
     */
    public String getTripType() {
        return tripType;
    }

    /**
     * Sets the trip type.
     * 
     * @param tripType The type of trip (e.g., "one-way", "round-trip")
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