package com.__final_backend.backend.config;

import com.__final_backend.backend.security.JwtAuthenticationEntryPoint;
import com.__final_backend.backend.security.JwtAuthenticationFilter;
import com.__final_backend.backend.security.JwtTokenUtil;
import com.__final_backend.backend.security.RememberMeAuthenticationFilter;
import com.__final_backend.backend.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration for the application.
 *
 * <p>
 * This class configures authentication, authorization, and security filters for
 * the application.
 * It defines URL-based access rules, authentication mechanisms, and security
 * headers.
 *
 * <p>
 * Note: PasswordEncoder bean has been moved to CommonSecurityConfig to prevent
 * circular dependency between SecurityConfig and AuthServiceImpl.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AuthService authService;

    /**
     * Creates a new SecurityConfig instance with the required dependencies.
     *
     * @param jwtTokenUtil                JWT utility for token operations
     * @param jwtAuthenticationEntryPoint Entry point for handling authentication
     *                                    failures
     * @param authService                 Service for authentication operations
     */
    public SecurityConfig(JwtTokenUtil jwtTokenUtil,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            AuthService authService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.authService = authService;
    }

    /**
     * Configures the security filter chain with appropriate settings.
     *
     * <p>
     * This method sets up the HTTP security configuration including CORS, CSRF,
     * authorization rules, security headers, and authentication filters.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Enable CSRF with cookie-based repository for form submissions
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        // Setting the CSRF token attribute name to a standard value
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Configure CSRF selectively - enable for web forms, disable for API
                .csrf(csrf -> csrf
                        .csrfTokenRepository(tokenRepository)
                        .csrfTokenRequestHandler(requestHandler)
                        // Ignore CSRF for stateless API endpoints that use token auth
                        .ignoringRequestMatchers("/api/auth/login", "/api/auth/logout",
                                "/api/auth/register")
                        .ignoringRequestMatchers("/api/flights/**", "/api/airports/**",
                                "/api/saved-flights/**", "/api/bookings/**")
                        .ignoringRequestMatchers("/h2-console/**"))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) -> response
                                        .sendRedirect("/login"),
                                request -> !request.getRequestURI()
                                        .startsWith("/api/")))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/", "/index.html", "/static/**", "/h2-console/**",
                                "/favicon.ico")
                        .permitAll()
                        .requestMatchers("/css/**", "/js/**").permitAll() // Allow access to CSS and JS resources
                        .requestMatchers("/login", "/register").permitAll() // Allow access to login and register pages
                        .requestMatchers("/login.html", "/register.html").permitAll() // Also allow direct HTML file
                                                                                      // access
                        .requestMatchers("/saved-flights").permitAll() // Allow access to saved-flights page
                        .requestMatchers("/bookings", "/bookings/new").permitAll() // Allow access to bookings pages
                        .requestMatchers("/api/flights/**", "/api/airports/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register",
                                "/api/auth/logout")
                        .permitAll()
                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll() // Database endpoints requiring authentication
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/flight-searches/**").authenticated()
                        .requestMatchers("/api/saved-flights/**").authenticated()
                        .requestMatchers("/api/bookings/**").authenticated()
                        .requestMatchers("/api/booking-records/**").authenticated()
                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .headers(headers -> headers
                        // Replace deprecated frameOptions with newer API
                        .frameOptions(frame -> frame.sameOrigin())
                        // Update content security policy with newer API
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                "script-src 'self' https://cdnjs.cloudflare.com https://cdn.jsdelivr.net 'unsafe-inline'; "
                                                +
                                                "style-src 'self' https://fonts.googleapis.com https://cdn.jsdelivr.net 'unsafe-inline'; "
                                                +
                                                "img-src 'self' data:; " +
                                                "font-src 'self' https://fonts.gstatic.com"))
                        // Update referrer policy with proper enum value
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                        // Update permissions policy with newer API
                        .permissionsPolicy(permissions -> permissions
                                .policy("camera=(), microphone=(), geolocation=()")));

        // Add JWT and Remember-Me filters
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(rememberMeAuthenticationFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Creates a JWT authentication filter for processing JWT tokens.
     *
     * <p>
     * This filter extracts and validates JWT tokens from HTTP requests.
     *
     * @return a configured JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil);
    }

    /**
     * Creates a remember-me authentication filter.
     *
     * <p>
     * This filter handles persistent logins via remember-me tokens.
     *
     * @return a configured RememberMeAuthenticationFilter
     */
    @Bean
    public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() {
        return new RememberMeAuthenticationFilter(authService);
    }

    /**
     * Creates an authentication manager bean.
     *
     * <p>
     * The authentication manager coordinates the authentication process
     * using the configured authentication providers.
     *
     * @param authenticationConfiguration the Spring Security authentication
     *                                    configuration
     * @return the authentication manager
     * @throws Exception if an error occurs retrieving the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Creates a CORS configuration source with secure settings.
     *
     * <p>
     * This configuration restricts cross-origin requests to specific origins,
     * methods, and headers to prevent security vulnerabilities while enabling
     * legitimate cross-origin functionality.
     *
     * @return a configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration
                .setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001",
                        "http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-CSRF-Token"));
        configuration.setExposedHeaders(List.of("Authorization", "X-CSRF-Token"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}