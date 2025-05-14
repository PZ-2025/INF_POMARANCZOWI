package com.orange.bookmanagment.user.security.jwt.service;

import com.orange.bookmanagment.user.exception.IllegalAccountAccessException;
import com.orange.bookmanagment.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Serwis odpowiedzialny za generowanie i analizowanie tokenów JWT.
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    /**
     * Generuje token JWT na podstawie danych uwierzytelniających użytkownika.
     *
     * @param authentication obiekt Authentication zawierający dane logowania
     * @param user obiekt użytkownika
     * @return token JWT jako String
     */
    public String generateJwtToken(Authentication authentication, User user){

        final Instant expiredTime = Instant.now().plus(7, ChronoUnit.DAYS);

        final JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(expiredTime)
                .subject(authentication.getName())
                .claim("id", user.getId())
                .claim("authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .claim("user_id", user.getId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    /**
     * Pobiera ID użytkownika z tokena JWT.
     *
     * @param token wartość tokena JWT
     * @return ID użytkownika
     * @throws IllegalAccountAccessException jeśli token jest niepoprawny
     */
    public Long getUserIdFromJwtToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaim("user_id");
        } catch (JwtException e) {
            throw new IllegalAccountAccessException("Wrong authentication");
        }
    }
}
