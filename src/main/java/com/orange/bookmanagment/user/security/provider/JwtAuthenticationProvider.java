package com.orange.bookmanagment.user.security.provider;

import com.orange.bookmanagment.user.security.exception.InvalidCredentialsException;
import com.orange.bookmanagment.user.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Własny provider uwierzytelnienia JWT używany do weryfikacji danych logowania (email + hasło).
 */
@Component
@RequiredArgsConstructor
class JwtAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService detailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Przeprowadza proces uwierzytelnienia użytkownika.
     *
     * @param authentication dane uwierzytelniające
     * @return uwierzytelniony token z uprawnieniami
     * @throws AuthenticationException jeśli uwierzytelnienie się nie powiedzie
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String email = authentication.getName();
        final String password = authentication.getCredentials().toString();

        final UserDetails userDetails = detailsService.loadUserByUsername(email);

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(email, password, userDetails.getAuthorities());
    }

    /**
     * Sprawdza, czy provider obsługuje dany typ uwierzytelnienia.
     *
     * @param authentication typ klasy uwierzytelnienia
     * @return true, jeśli wspierany; false w przeciwnym razie
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
