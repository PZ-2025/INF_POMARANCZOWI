package com.orange.bookmanagment.user.web.controller;

import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import com.orange.bookmanagment.user.service.UserService;
import com.orange.bookmanagment.user.web.mapper.UserDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.orange.bookmanagment.user.web.requests.UpdateUserRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

/**
 * Kontroler użytkownika odpowiadający za operacje związane z profilem użytkownika.
 * <p>
 * Obsługuje pobieranie danych użytkownika, aktualizację danych, zarządzanie zdjęciem profilowym.
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    /**
     * Pobiera dane użytkownika na podstawie jego ID.
     *
     * @param id identyfikator użytkownika
     * @return odpowiedź HTTP zawierająca dane użytkownika
     */
    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getUserById(@PathVariable("id") long id) {
        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("User data by id request")
                        .message("User by id")
                        .data(Map.of("user", userDtoMapper.toDto(userService.getUserById(id))))
                        .build());
    }

    /**
     * Aktualizuje dane zalogowanego użytkownika.
     *
     * @param request obiekt zawierający nowe dane użytkownika
     * @return potwierdzenie wykonania aktualizacji
     */
    @PutMapping("/me")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody UpdateUserRequest request) {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = jwt.getClaim("user_id");
        userService.updateUserData(userId, request);

        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("User data update request")
                        .message("User updated successfully")
                        .data(Map.of())
                        .build());
    }

    /**
     * Przesyła i zapisuje zdjęcie profilowe użytkownika.
     *
     * @param file plik graficzny (awatar)
     * @param authHeader nagłówek autoryzacyjny zawierający token JWT
     * @return odpowiedź potwierdzająca zapisanie pliku
     * @throws IOException w przypadku błędu zapisu pliku
     */
    @PostMapping("/upload-avatar")
    public ResponseEntity<HttpResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) throws IOException {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = jwt.getClaim("user_id");

        String fileName = "user-" + userId + ".jpg";
        Path uploadPath = Paths.get("uploads/avatars/" + fileName);
        Files.createDirectories(uploadPath.getParent());
        Files.write(uploadPath, file.getBytes());
        userService.updateAvatarPath(userId, "/uploads/avatars/" + fileName);

        return ResponseEntity.ok(HttpResponse.builder()
                .message("Photo saved")
                .statusCode(200)
                .build());
    }

    /**
     * Usuwa zdjęcie profilowe zalogowanego użytkownika.
     *
     * @param jwt token JWT zawierający identyfikator użytkownika
     * @return odpowiedź HTTP potwierdzająca usunięcie zdjęcia
     * @throws IOException w przypadku błędu usunięcia pliku
     */
    @DeleteMapping("/delete-avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteAvatar(@AuthenticationPrincipal Jwt jwt) throws IOException {
        Long userId = Long.parseLong(jwt.getClaimAsString("id"));
        userService.deleteUserAvatar(userId);
        return ResponseEntity.ok().build();
    }
}
