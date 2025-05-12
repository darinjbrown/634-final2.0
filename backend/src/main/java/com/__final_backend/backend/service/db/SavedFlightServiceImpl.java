package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.SavedFlight;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.SavedFlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the SavedFlightService interface
 */
@Service
@Transactional
public class SavedFlightServiceImpl implements SavedFlightService {

  private final SavedFlightRepository savedFlightRepository;

  @Autowired
  public SavedFlightServiceImpl(SavedFlightRepository savedFlightRepository) {
    this.savedFlightRepository = savedFlightRepository;
  }

  @Override
  public SavedFlight saveFlight(SavedFlight savedFlight) {
    return savedFlightRepository.save(savedFlight);
  }

  @Override
  public Optional<SavedFlight> getSavedFlightById(Long id) {
    return savedFlightRepository.findById(id);
  }

  @Override
  public List<SavedFlight> getSavedFlightsByUser(User user) {
    return savedFlightRepository.findByUser(user);
  }

  @Override
  public Page<SavedFlight> getSavedFlightsByUser(User user, Pageable pageable) {
    return savedFlightRepository.findByUser(user, pageable);
  }

  @Override
  public void deleteSavedFlightById(Long id) {
    savedFlightRepository.deleteById(id);
  }

  @Override
  public void deleteSavedFlightsByUser(User user) {
    savedFlightRepository.deleteByUser(user);
  }
}