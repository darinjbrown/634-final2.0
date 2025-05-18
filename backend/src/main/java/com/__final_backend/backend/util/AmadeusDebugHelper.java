package com.__final_backend.backend.util;

import com.amadeus.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Helper class for debugging Amadeus API interactions.
 * <p>
 * This utility class provides methods to log and debug Amadeus API requests and
 * responses.
 * It centralizes logging logic for API interactions to help troubleshoot issues
 * and monitor
 * performance of external API calls.
 * <p>
 * All methods are static to facilitate easy access from across the application
 * without
 * requiring dependency injection.
 */
@Component
public class AmadeusDebugHelper {

  private static final Logger logger = LoggerFactory.getLogger(AmadeusDebugHelper.class);

  /**
   * Logs detailed information about Amadeus API parameters.
   * <p>
   * This method logs the details of an outgoing Amadeus API request, including
   * the endpoint
   * being called and the parameters being sent. The information is logged at
   * DEBUG level,
   * making it visible only when debug logging is enabled.
   * <p>
   * This logging is useful for troubleshooting API connectivity issues or
   * validating
   * that the correct parameters are being sent to the API.
   * 
   * @param params   the Amadeus API parameters object containing request
   *                 parameters
   * @param endpoint the API endpoint being called (e.g.,
   *                 "/shopping/flight-offers")
   */
  public static void logAmadeusParams(Params params, String endpoint) {
    logger.debug("=== AMADEUS API REQUEST DEBUG ===");
    logger.debug("Endpoint: {}", endpoint);
    logger.debug("Parameters: {}", params);
    logger.debug("==============================");
  }

  /**
   * Logs Amadeus API response information.
   * <p>
   * This method logs details about the response received from an Amadeus API
   * call,
   * including the response time in milliseconds and the type of response object.
   * The information is logged at DEBUG level, making it visible only when debug
   * logging is enabled.
   * <p>
   * This logging is useful for performance monitoring and for validating that
   * the correct response types are being received from the API.
   * 
   * @param response     the object returned from the Amadeus API call, may be
   *                     null if the call failed
   * @param responseTime the time in milliseconds that the API call took to
   *                     complete
   */
  public static void logAmadeusResponse(Object response, long responseTime) {
    logger.debug("=== AMADEUS API RESPONSE DEBUG ===");
    logger.debug("Response time: {}ms", responseTime);
    logger.debug("Response type: {}", response != null ? response.getClass().getName() : "null");
    logger.debug("==============================");
  }
}
