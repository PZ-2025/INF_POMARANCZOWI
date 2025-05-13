package com.orange.bookmanagment.user.security.provider;

import com.orange.bookmanagment.user.security.exception.InvalidCredentialsException;
import com.orange.bookmanagment.user.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Provider uwierzytelniania konta oparty o implementację Spring Security
 * {@link AbstractUserDetailsAuthenticationProvider}.
 */
@Component
@RequiredArgsConstructor
public class AccountAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Wykonuje dodatkową weryfikację hasła.
     *
     * @param userDetails dane użytkownika z bazy
     * @param authentication dane uwierzytelniające z żądania
     * @throws AuthenticationException jeśli dane są nieprawidłowe
     */
    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        final String password = userDetails.getPassword();
        final String credentials = (String) authentication.getCredentials();

        if (credentials == null || password == null) {
            throw new InvalidCredentialsException("Credentials are null or empty");
        }

//        if (!passwordEncoder.matches(credentials,password)) {
//            throw new InvalidCredentialsException("Typed password is wrong");
//        }

        if (!credentials.equals(password)) {
            throw new InvalidCredentialsException("Typed password is wrong");
        }
    }

    /**
     * Pobiera dane użytkownika na podstawie adresu e-mail.
     *
     * @param email adres e-mail użytkownika
     * @param authentication token z danymi uwierzytelniającymi
     * @return dane użytkownika
     */
    @Override
    protected UserDetails retrieveUser(String email, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return customUserDetailsService.loadUserByUsername(email);
    }
}
