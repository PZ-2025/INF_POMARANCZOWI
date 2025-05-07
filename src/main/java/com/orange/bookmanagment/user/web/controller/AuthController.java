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

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthController {
    private final UserService userService;
    private final AccountAuthenticationProvider accountAuthenticationProvider;
    private final UserDtoMapper userDtoMapper;
    private final TokenService tokenService;

    /**
     * @param userLoginRequest - login request with params
     * @return As response.Message we will receive token, as data we will have {@link com.orange.bookmanagment.user.web.model.UserDto} model
     */
    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@Valid  @RequestBody UserLoginRequest userLoginRequest) {
        try {
            final Authentication authentication = accountAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequest.email(), userLoginRequest.password()));
            final User user = userService.getUserByEmail(userLoginRequest.email());

            return ResponseEntity.status(OK).body(HttpResponse.builder()
                            .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                            .statusCode(OK.value())
                            .httpStatus(OK)
                            .reason("User login request")
                            .message(tokenService.generateJwtToken(authentication, user))
                            .data(Map.of("user", userDtoMapper.toDto(user)))
                    .build());
        } catch (AuthenticationException e) {
            throw new IllegalAccountAccessException(e.getMessage());
        }
    }

    /**
     * @param userRegisterRequest - User register request with params
     *
     * @return As data endpoint returns registered user data as {@link com.orange.bookmanagment.user.web.model.UserDto}
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
     *
     * @param changePasswordRequest - Change password request includes old and new password
     * @param authHeader - Authorization header with JWT token
     * @return Status of password change
     */
    @PostMapping("/changePassword")
    public ResponseEntity<HttpResponse> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest,
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
