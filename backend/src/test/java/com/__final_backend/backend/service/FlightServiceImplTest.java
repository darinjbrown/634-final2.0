package com.__final_backend.backend.service;

import com.__final_backend.backend.dto.FlightDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FlightServiceImplTest {

    @Test
    void testSearchFlights() {
        FlightService flightService = Mockito.mock(FlightService.class);
        List<FlightDTO> flights = flightService.searchFlights(
                "JFK", "LAX", LocalDate.now(), null, 1, "one-way");

        assertNotNull(flights, "Flight search should return a non-null list");
    }
}