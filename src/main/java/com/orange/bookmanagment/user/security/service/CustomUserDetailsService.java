package com.orange.bookmanagment.user.security.service;

import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import com.orange.bookmanagment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementacja {@link UserDetailsService} używana przez Spring Security
 * do ładowania danych użytkownika na podstawie adresu e-mail.
 */
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * Ładuje użytkownika na podstawie adresu e-mail.
     *
     * @param email adres e-mail użytkownika
     * @return obiekt {@link UserDetails} zawierający informacje o użytkowniku
     * @throws UsernameNotFoundException jeśli użytkownik nie został znaleziony
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = userService.getUserByEmail(email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true,
                true,
                true,
                !user.isBlocked(),
                getAuthorities(user.getUserType())
        );
    }

    /**
     * Przekształca typ użytkownika na listę uprawnień.
     *
     * @param userRole typ użytkownika
     * @return kolekcja uprawnień
     */
    private Collection<? extends GrantedAuthority> getAuthorities(UserType userRole){
        return userRole.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
