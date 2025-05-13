package com.orange.bookmanagment.user.web.controller;

import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import com.orange.bookmanagment.user.exception.IllegalAccountAccessException;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import com.orange.bookmanagment.user.security.jwt.service.TokenService;
import com.orange.bookmanagment.user.security.provider.AccountAuthenticationProvider;
import com.orange.bookmanagment.user.service.UserService;
import com.orange.bookmanagment.user.web.mapper.UserDtoMapper;
import com.orange.bookmanagment.user.web.requests.ChangePasswordRequest;
import com.orange.bookmanagment.user.web.requests.UserLoginRequest;
import com.orange.bookmanagment.user.web.requests.UserRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * Kontroler odpowiedzialny za obsługę uwierzytelniania i rejestracji użytkowników.
 * <p>
 * Udostępnia punkty końcowe dla logowania, rejestracji, zmiany hasła i pobierania danych o zalogowanym użytkowniku.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthController {

    private final UserService userService;
    private final AccountAuthenticationProvider accountAuthenticationProvider;
    private final UserDtoMapper userDtoMapper;
    private final TokenService tokenService;

    /**
     * Endpoint do logowania użytkownika.
     *
     * @param userLoginRequest żądanie logowania zawierające email i hasło
     * @return mapa zawierająca access token, refresh token (opcjonalnie) oraz dane użytkownika
     * @throws IllegalAccountAccessException w przypadku błędnych danych logowania
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        try {
            Authentication authentication = accountAuthenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginRequest.email(), userLoginRequest.password())
            );

            User user = userService.getUserByEmail(userLoginRequest.email());
            String accessToken = tokenService.generateJwtToken(authentication, user);
            String refreshToken = "";

            Map<String, Object> response = Map.of(
                    "access_token", accessToken,
                    "refresh_token", refreshToken,
                    "data", Map.of(
                            "user", userDtoMapper.toDto(user)
                    )
            );

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new IllegalAccountAccessException("Nieprawidłowy email lub hasło");
        }
    }

    /**
     * Endpoint do rejestracji nowego użytkownika.
     *
     * @param userRegisterRequest dane rejestracyjne użytkownika
     * @return odpowiedź HTTP z danymi nowo zarejestrowanego użytkownika
     */
    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        final User user = userService.registerUser(userRegisterRequest, UserType.READER);

        return ResponseEntity.status(CREATED).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .httpStatus(CREATED)
                        .statusCode(CREATED.value())
                        .message("User register finished")
                        .reason("User has been registered")
                        .data(Map.of("user",userDtoMapper.toDto(user)))
                        .build());
    }

    /**
     * Endpoint zwracający dane aktualnie zalogowanego użytkownika.
     *
     * @param authentication obiekt uwierzytelnienia dostarczony przez Spring Security
     * @return dane użytkownika w odpowiedzi HTTP
     */
    @GetMapping("/me")
    public ResponseEntity<HttpResponse> me(Authentication authentication) {
        final User user = userService.getUserByEmail(authentication.getName());

        return ResponseEntity.status(OK).body(HttpResponse
                .builder()
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .statusCode(OK.value())
                .httpStatus(OK)
                .reason("User information request")
                .message("User data")
                .data(Map.of("user",userDtoMapper.toDto(user)))
                .build());
    }

    /**
     * Endpoint do zmiany hasła użytkownika.
     *
     * @param changePasswordRequest żądanie zmiany hasła zawierające stare i nowe hasło
     * @param authHeader nagłówek Authorization zawierający JWT token
     * @return odpowiedź z potwierdzeniem zmiany hasła
     */
    @PostMapping("/changePassword")
    public ResponseEntity<HttpResponse> changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest,
            @RequestHeader("Authorization") String authHeader) {

        final String token = authHeader.replace("Bearer ", "");
        userService.changeUserPassword(tokenService.getUserIdFromJwtToken(token), changePasswordRequest);

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .httpStatus(OK)
                .statusCode(OK.value())
                .message("Password has been changed")
                .reason("Password changed")
                .build());
    }
}
