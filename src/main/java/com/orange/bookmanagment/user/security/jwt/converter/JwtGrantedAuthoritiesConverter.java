package com.orange.bookmanagment.user.security.jwt.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Konwerter JWT, który odczytuje listę ról z tokena i mapuje je na obiekty {@link GrantedAuthority}.
 */
@Component
public class JwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * Konwertuje token JWT na kolekcję uprawnień (ról).
     *
     * @param source token JWT
     * @return lista uprawnień użytkownika
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        final List<String> roles = source.getClaim("authorities");

        return roles.stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
