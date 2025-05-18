package com.__final_backend.backend.security;

import com.__final_backend.backend.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Utility class for JWT token operations in the authentication flow.
 * <p>
 * This class provides functionality for generating, validating, and processing
 * JWT (JSON Web Token)
 * tokens used for stateless authentication. It handles token creation upon
 * successful authentication,
 * extraction of user details and authorities from tokens, token validation, and
 * token resolution
 * from HTTP requests.
 * <p>
 * The implementation uses the JJWT library and relies on HMAC-SHA512 signatures
 * for token security.
 */
@Component
public class JwtTokenUtil {
  /**
   * Configuration properties for JWT settings like secret key and expiration
   * times.
   */
  private final JwtProperties jwtProperties;

  /** The cryptographic key used for signing and verifying JWT tokens. */
  private final Key key;

  /**
   * Constructs a new JWT token utility with the provided properties.
   * <p>
   * Initializes the signing key from the secret key specified in the properties.
   * 
   * @param jwtProperties the JWT configuration properties containing the secret
   *                      key
   *                      and other settings
   */
  public JwtTokenUtil(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
    this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Generates a JWT token for an authenticated user.
   * <p>
   * Creates a token containing the user's identity (subject) and authorities
   * (roles),
   * with a configured expiration time. The token is signed using HMAC-SHA512
   * algorithm.
   * <p>
   * The token includes:
   * <ul>
   * <li>Subject: The username from the authentication object</li>
   * <li>Auth claim: A comma-separated list of the user's authorities</li>
   * <li>Issued at: Current timestamp</li>
   * <li>Expiration: Current time plus configured expiration period</li>
   * </ul>
   * 
   * @param authentication the authentication object containing user details and
   *                       authorities
   * @return the generated JWT token as a string
   */
  public String generateToken(Authentication authentication) {
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    return Jwts.builder()
        .setSubject(authentication.getName())
        .claim("auth", authorities)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpirationMs()))
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();
  }

  /**
   * Extracts authentication information from a JWT token.
   * <p>
   * This method parses a JWT token, extracts the user details and authorities,
   * and creates an Authentication object suitable for use with Spring Security.
   * <p>
   * The method performs these steps:
   * <ol>
   * <li>Parse the JWT token to extract claims</li>
   * <li>Extract and convert authorities from the "auth" claim</li>
   * <li>Create a UserDetails object with username and authorities</li>
   * <li>Create and return an authenticated
   * UsernamePasswordAuthenticationToken</li>
   * </ol>
   * 
   * @param token the JWT token to parse
   * @return an Authentication object populated with user details from the token
   * @throws JwtException if the token cannot be parsed or is invalid
   */
  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

    Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
        .filter(auth -> !auth.trim().isEmpty())
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    UserDetails principal = new User(claims.getSubject(), "", authorities);

    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }

  /**
   * Validates a JWT token.
   * <p>
   * This method attempts to parse and validate the token using the configured
   * signing key.
   * It handles various validation exceptions that might occur and returns a
   * boolean
   * indicating whether the token is valid.
   * <p>
   * The validation checks for:
   * <ul>
   * <li>Valid signature</li>
   * <li>Valid format and claims</li>
   * <li>Non-expired token</li>
   * <li>Supported JWT format</li>
   * <li>Non-empty claims</li>
   * </ul>
   * 
   * @param token the JWT token to validate
   * @return true if the token is valid, false if any validation checks fail
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      // Invalid signature or malformed token structure
    } catch (ExpiredJwtException e) {
      // Token has expired according to the expiration date
    } catch (UnsupportedJwtException e) {
      // Token format or algorithms not supported
    } catch (IllegalArgumentException e) {
      // Token is null, empty or has invalid claims
    }
    return false;
  }

  /**
   * Extracts a JWT token from an HTTP request.
   * <p>
   * This method looks for a Bearer token in the Authorization header (or
   * configured
   * alternative header) of the request. If found, it strips the prefix and
   * returns
   * the raw token.
   * <p>
   * For example, from "Authorization: Bearer xyz123", it extracts and returns
   * "xyz123".
   * 
   * @param request the HTTP servlet request potentially containing a JWT token
   * @return the extracted JWT token string, or null if no valid token is found
   */
  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(jwtProperties.getHeaderString());
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtProperties.getTokenPrefix())) {
      return bearerToken.substring(jwtProperties.getTokenPrefix().length());
    }
    return null;
  }
}