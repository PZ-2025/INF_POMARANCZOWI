package com.orange.bookmanagment.user.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.user.security.exception.InvalidCredentialsException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authenticationHeader = request.getHeader("Authorization");

        if (authenticationHeader != null) {
            if (authenticationHeader.startsWith("Bearer ")) {
                try {
                    //Token is substring 7 because Bearer = char[6]
                    final String token = authenticationHeader.substring(7);

                    final Jwt jwt = jwtDecoder.decode(token);

                    if(jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(now())){
                        handleJwtException(response, new InvalidCredentialsException("Token expired"));
                        return;
                    }

                    final Collection<GrantedAuthority> authorities = parseAuthoritiesFromToken(jwt);

                    final Authentication authentication = new JwtAuthenticationToken(jwt, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }catch (JwtException | AccessDeniedException e){
                    handleJwtException(response,e);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);

    }

    Collection<GrantedAuthority> parseAuthoritiesFromToken(Jwt jwt){
        List<String> roles = jwt.getClaim("authorities");
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    void handleJwtException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), HttpResponse.builder()
                .httpStatus(UNAUTHORIZED)
                .statusCode(UNAUTHORIZED.value())
                .reason(e.getMessage())
                .message(e.getMessage())
                .build());
    }
}
