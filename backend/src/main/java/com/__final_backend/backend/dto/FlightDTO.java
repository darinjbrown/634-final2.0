package com.__final_backend.backend.dto;

/**
 * Data Transfer Object (DTO) for flight information.
 * <p>
 * Encapsulates details about a flight such as airline, flight number, departure
 * and arrival locations, times, and price. This class is used to transfer
 * flight data
 * between the service layer and API controllers in the SkyExplorer application.
 * </p>
 */
public class FlightDTO {
    /** The name of the airline operating the flight (e.g., "American Airlines"). */
    private String airline;

    /** The flight identification number (e.g., "AA123"). */
    private String flightNumber;

    /** The departure location/airport code (3-letter IATA code, e.g., "JFK"). */
    private String departure;

    /** The arrival location/airport code (3-letter IATA code, e.g., "LAX"). */
    private String arrival;
    /**
     * The departure time of the flight in ISO format or human-readable format.
     * May include date and time information.
     */
    private String departureTime;

    /**
     * The arrival time of the flight in ISO format or human-readable format.
     * May include date and time information.
     */
    private String arrivalTime;

    /**
     * The price of the flight ticket in the default currency (typically USD).
     * Represented as a floating-point value.
     */
    private Double price;

    /**
     * Gets the airline name.
     *
     * @return the name of the airline operating the flight
     */
    public String getAirline() {
        return airline;
    }

    /**
     * Sets the airline name.
     *
     * @param airline the name of the airline operating the flight
     */
    public void setAirline(String airline) {
        this.airline = airline;
    }

    /**
     * Gets the flight number.
     *
     * @return the flight identification number
     */
    public String getFlightNumber() {
        return flightNumber;
    }

    /**
     * Sets the flight number.
     *
     * @param flightNumber the flight identification number
     */
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    /**
     * Gets the departure location.
     *
     * @return the departure location/airport code (3-letter IATA code)
     */
    public String getDeparture() {
        return departure;
    }

    /**
     * Sets the departure location.
     *
     * @param departure the departure location/airport code (3-letter IATA code)
     */
    public void setDeparture(String departure) {
        this.departure = departure;
    }

    /**
     * Gets the arrival location.
     *
     * @return the arrival location/airport code (3-letter IATA code)
     */
    public String getArrival() {
        return arrival;
    }

    /**
     * Sets the arrival location.
     *
     * @param arrival the arrival location/airport code (3-letter IATA code)
     */
    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    /**
     * Gets the departure time.
     *
     * @return the departure time of the flight
     */
    public String getDepartureTime() {
        return departureTime;
    }

    /**
     * Sets the departure time.
     *
     * @param departureTime the departure time of the flight
     */
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    /**
     * Gets the arrival time.
     *
     * @return the arrival time of the flight
     */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Sets the arrival time.
     *
     * @param arrivalTime the arrival time of the flight
     */
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * Gets the flight price.
     *
     * @return the price of the flight ticket
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the flight price.
     *
     * @param price the price of the flight ticket
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "FlightDTO{" +
                "airline='" + airline + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                ", departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", price=" + price +
                '}';
    }
}