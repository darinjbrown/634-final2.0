package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.FlightSearch;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.FlightSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the FlightSearchService interface
 */
@Service
@Transactional
public class FlightSearchServiceImpl implements FlightSearchService {

  private final FlightSearchRepository flightSearchRepository;

  @Autowired
  public FlightSearchServiceImpl(FlightSearchRepository flightSearchRepository) {
    this.flightSearchRepository = flightSearchRepository;
  }

  @Override
  public FlightSearch saveFlightSearch(FlightSearch flightSearch) {
    return flightSearchRepository.save(flightSearch);
  }

  @Override
  public Optional<FlightSearch> getFlightSearchById(Long id) {
    return flightSearchRepository.findById(id);
  }

  @Override
  public List<FlightSearch> getFlightSearchesByUser(User user) {
    return flightSearchRepository.findByUser(user);
  }

  @Override
  public Page<FlightSearch> getFlightSearchesByUser(User user, Pageable pageable) {
    return flightSearchRepository.findByUser(user, pageable);
  }

  @Override
  public List<FlightSearch> getFlightSearchesByOriginAndDestination(String origin, String destination) {
    return flightSearchRepository.findByOriginAndDestination(origin, destination);
  }

  @Override
  public List<FlightSearch> getFlightSearchesByDepartureDateBetween(LocalDate startDate, LocalDate endDate) {
    return flightSearchRepository.findByDepartureDateBetween(startDate, endDate);
  }

  @Override
  public List<FlightSearch> getFlightSearchesByUserAndOriginAndDestination(User user, String origin,
      String destination) {
    return flightSearchRepository.findByUserAndOriginAndDestination(user, origin, destination);
  }

  @Override
  public void deleteFlightSearchById(Long id) {
    flightSearchRepository.deleteById(id);
  }

  @Override
  public void deleteFlightSearchesByUser(User user) {
    List<FlightSearch> searches = flightSearchRepository.findByUser(user);
    flightSearchRepository.deleteAll(searches);
  }
}