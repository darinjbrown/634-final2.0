package com.__final_backend.backend.util;

import com.amadeus.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Helper class for debugging Amadeus API interactions
 */
@Component
public class AmadeusDebugHelper {

  private static final Logger logger = LoggerFactory.getLogger(AmadeusDebugHelper.class);

  /**
   * Logs detailed information about Amadeus API parameters
   */
  public static void logAmadeusParams(Params params, String endpoint) {
    logger.debug("=== AMADEUS API REQUEST DEBUG ===");
    logger.debug("Endpoint: {}", endpoint);
    logger.debug("Parameters: {}", params);
    logger.debug("==============================");
  }

  /**
   * Logs Amadeus API response information
   */
  public static void logAmadeusResponse(Object response, long responseTime) {
    logger.debug("=== AMADEUS API RESPONSE DEBUG ===");
    logger.debug("Response time: {}ms", responseTime);
    logger.debug("Response type: {}", response != null ? response.getClass().getName() : "null");
    logger.debug("==============================");
  }
}
