package com.orange.bookmanagment.user.security.config;

import com.orange.bookmanagment.user.security.jwt.filter.JwtAuthenticationFilter;
import com.orange.bookmanagment.user.security.provider.AccountAuthenticationProvider;
import com.orange.bookmanagment.user.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Główna konfiguracja bezpieczeństwa aplikacji (Spring Security).
 * Odpowiada za konfigurację JWT, CORS, endpointów i filtrowania żądań.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
class SecurityConfig {

    private final CustomUserDetailsService detailsService;
    private final AccountAuthenticationProvider authenticationProvider;
    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Konfiguracja łańcucha filtrów zabezpieczeń HTTP.
     *
     * @param security konfigurator HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception w przypadku błędów konfiguracji
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = security.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        authenticationManagerBuilder.userDetailsService(detailsService);
        security.csrf(AbstractHttpConfigurer::disable);
        security.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()));

        // Authenticate endpoint
        security.authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(POST, "/api/v1/auth/register").permitAll()
                                .requestMatchers(POST, "/api/v1/auth/login").permitAll()
                                .requestMatchers(GET, "/api/v1/auth/me").authenticated()
                                .requestMatchers(POST, "/api/v1/auth/changePassword").authenticated()
                                .requestMatchers(POST, "/api/v1/book/create").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(GET, "/api/v1/book/all").permitAll()
                                .requestMatchers(GET, "/api/v1/book/all/unpaged").permitAll()
                                .requestMatchers(GET, "/api/v1/book/{id}").permitAll()
                                .requestMatchers(GET, "/api/v1/book/lost").permitAll()
                                .requestMatchers(GET, "/api/v1/book/random/category/**").permitAll()
                                .requestMatchers(GET, "/api/v1/book/search").permitAll()
                                .requestMatchers(POST, "/api/v1/book/authors").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(POST, "/api/v1/book/publishers").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(GET, "/api/v1/book/authors").authenticated()
                                .requestMatchers(GET, "/api/v1/book/publishers").authenticated()
                                .requestMatchers(POST, "/api/v1/reservations/book/{bookId}").authenticated()
                                .requestMatchers(POST, "/api/v1/reservations/{reservationId}/cancel").authenticated()
                                .requestMatchers(POST, "/api/v1/reservations/{reservationId}/complete").authenticated()
                                .requestMatchers(POST, "/api/v1/reservations/{reservationId}/extend").authenticated()
                                .requestMatchers(POST, "/api/v1/reservations/{reservationId}/expire").authenticated()
                                .requestMatchers(GET, "/api/v1/reservations/my").authenticated()
                                .requestMatchers(GET, "/api/v1/reservations/queue-length/{bookId}").permitAll()
                                .requestMatchers(GET, "/api/v1/user/all").hasAuthority("ADMIN")
                                .requestMatchers(PUT, "/api/v1/user/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(PUT, "/api/v1/user/me").authenticated()
                                .requestMatchers(POST, "/api/v1/user/upload-avatar").authenticated()
                                .requestMatchers(DELETE, "/api/v1/user/delete-avatar").authenticated()
                                .requestMatchers("/uploads/**").permitAll()
                                .requestMatchers(GET, "/api/v1/reports/inventory").permitAll()
                                .requestMatchers(GET, "/api/v1/reports/filtered").permitAll()
                                .requestMatchers(GET, "/api/v1/reports/popularity").permitAll()
                                .requestMatchers(POST, "/api/v1/loans/borrow").permitAll()
                                .requestMatchers(POST, "/api/v1/loans/borrow/self").authenticated()
                                .requestMatchers(POST, "/api/v1/loans/{id}/return").permitAll()
                                .requestMatchers(POST, "/api/v1/loans/{id}/extend").permitAll()
                                .requestMatchers(POST, "/api/v1/loans/{loanId}/lost").authenticated()
                                .requestMatchers(POST, "/api/v1/loans/*/return").permitAll()
                                .requestMatchers("/api/v1/loans/my").authenticated()
                                .requestMatchers(POST,"/api/v1/order/place").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(GET, "/api/v1/order/**").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(GET,"/api/v1/order/all**").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(GET, "/api/v1/order/priority/**").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(GET, "/api/v1/order/status/**").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(POST, "/api/v1/order/finish/**").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(PATCH, "/api/v1/order/priority").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(PATCH, "/api/v1/order/status").hasAnyAuthority("ADMIN", "LIBRARIAN")
                                .requestMatchers(GET, "/api/v1/order/data/**").hasAnyAuthority("ADMIN", "LIBRARIAN")
                )
                .authenticationManager(authenticationManagerBuilder.build())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        security.oauth2ResourceServer(oauth -> oauth.jwt(jwtConfigurer -> {
                    jwtConfigurer.decoder(jwtDecoder);
                    jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter());
                })
        );

        return security.build();
    }

    /**
     * Konwerter JWT do uwzględniania uprawnień z tokena.
     *
     * @return JwtAuthenticationConverter
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        final org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities"); // defaults to "scope" or "scp"
        grantedAuthoritiesConverter.setAuthorityPrefix(""); // defaults to "SCOPE_"

        final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    /**
     * Konfiguracja CORS dla aplikacji.
     *
     * @return CorsConfigurationSource
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(("http://localhost:4200")));
        configuration.setAllowedMethods(List.of("POST","GET","PUT","DELETE","UPDATE"));
        configuration.setAllowedHeaders(List.of("*")); // Akceptuj wszystkie nagłówki
        configuration.setMaxAge(3600L);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
