package com.__final_backend.backend.dto;

/**
 * Data Transfer Object (DTO) for flight information.
 * Encapsulates details about a flight such as airline, flight number, departure
 * and arrival locations, times, and price.
 */
public class FlightDTO {
    /** The name of the airline operating the flight */
    private String airline;

    /** The flight identification number */
    private String flightNumber;

    /** The departure location/airport code */
    private String departure;

    /** The arrival location/airport code */
    private String arrival;

    /** The departure time of the flight */
    private String departureTime;

    /** The arrival time of the flight */
    private String arrivalTime;

    /** The price of the flight ticket */
    private Double price;

    /**
     * Gets the airline name.
     * 
     * @return The name of the airline operating the flight
     */
    public String getAirline() {
        return airline;
    }

    /**
     * Sets the airline name.
     * 
     * @param airline The name of the airline operating the flight
     */
    public void setAirline(String airline) {
        this.airline = airline;
    }

    /**
     * Gets the flight number.
     * 
     * @return The flight identification number
     */
    public String getFlightNumber() {
        return flightNumber;
    }

    /**
     * Sets the flight number.
     * 
     * @param flightNumber The flight identification number
     */
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    /**
     * Gets the departure location.
     * 
     * @return The departure location/airport code
     */
    public String getDeparture() {
        return departure;
    }

    /**
     * Sets the departure location.
     * 
     * @param departure The departure location/airport code
     */
    public void setDeparture(String departure) {
        this.departure = departure;
    }

    /**
     * Gets the arrival location.
     * 
     * @return The arrival location/airport code
     */
    public String getArrival() {
        return arrival;
    }

    /**
     * Sets the arrival location.
     * 
     * @param arrival The arrival location/airport code
     */
    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    /**
     * Gets the departure time.
     * 
     * @return The departure time of the flight
     */
    public String getDepartureTime() {
        return departureTime;
    }

    /**
     * Sets the departure time.
     * 
     * @param departureTime The departure time of the flight
     */
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    /**
     * Gets the arrival time.
     * 
     * @return The arrival time of the flight
     */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Sets the arrival time.
     * 
     * @param arrivalTime The arrival time of the flight
     */
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * Gets the flight price.
     * 
     * @return The price of the flight ticket
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the flight price.
     * 
     * @param price The price of the flight ticket
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