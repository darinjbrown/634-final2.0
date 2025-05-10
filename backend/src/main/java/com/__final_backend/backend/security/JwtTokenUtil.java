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
 * Utility class for JWT token generation and validation
 */
@Component
public class JwtTokenUtil {

  private final JwtProperties jwtProperties;
  private final Key key;

  public JwtTokenUtil(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
    this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Generate a JWT token for a user
   * 
   * @param authentication the authentication object containing user details
   * @return the generated JWT token
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
   * Get authentication from JWT token
   * 
   * @param token the JWT token
   * @return the authentication object
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
   * Validate a JWT token
   * 
   * @param token the JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      // Invalid signature or claims
    } catch (ExpiredJwtException e) {
      // Expired token
    } catch (UnsupportedJwtException e) {
      // Unsupported token
    } catch (IllegalArgumentException e) {
      // JWT claims string is empty
    }
    return false;
  }

  /**
   * Extract token from request
   * 
   * @param request the HTTP request
   * @return the extracted token, or null if not found
   */
  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(jwtProperties.getHeaderString());
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtProperties.getTokenPrefix())) {
      return bearerToken.substring(jwtProperties.getTokenPrefix().length());
    }
    return null;
  }
}