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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
class SecurityConfig {
    private final CustomUserDetailsService detailsService;
    private final AccountAuthenticationProvider authenticationProvider;
    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = security.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        authenticationManagerBuilder.userDetailsService(detailsService);
        security.csrf(AbstractHttpConfigurer::disable);
        security.oauth2ResourceServer(Customizer.withDefaults());
        security.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()));
        security.oauth2ResourceServer(o2auth -> o2auth.jwt(jwtConfigurer -> {
            jwtConfigurer.decoder(jwtDecoder);
            jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter());
        }));
        //Authenticate endpoint
        security.authorizeHttpRequests(
                        auth ->
                                auth
                                        .requestMatchers(POST,"/api/v1/auth/register").permitAll()
                                        .requestMatchers(POST,"/api/v1/auth/login").permitAll()
                                        .requestMatchers(GET,"/api/v1/auth/me").authenticated()
                                        .requestMatchers(POST,"/api/v1/auth/changePassword").authenticated()
                                        .requestMatchers(POST,"/api/v1/book/create").permitAll() //zabezpieczyc
                                        .requestMatchers(GET,"/api/v1/book/all").permitAll() //zabezpieczyc
                                        .requestMatchers(GET,"/api/v1/book/{id}").permitAll() //zabezpieczyc
                                        .requestMatchers(POST,"/api/v1/reservations/book/{bookId}").authenticated()
                )
                .authenticationManager(authenticationManagerBuilder.build())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //here

        return security.build();
    }
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        final org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities"); // defaults to "scope" or "scp"
        grantedAuthoritiesConverter.setAuthorityPrefix(""); // defaults to "SCOPE_"

        final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
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
