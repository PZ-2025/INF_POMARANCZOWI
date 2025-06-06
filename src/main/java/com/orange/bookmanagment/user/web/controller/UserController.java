package com.orange.bookmanagment.user.web.controller;

import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.service.UserService;
import com.orange.bookmanagment.user.web.mapper.UserDtoMapper;
import com.orange.bookmanagment.user.web.model.UserDto;
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
import java.util.List;
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
    @PreAuthorize("isAuthenticated()")
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
     * Aktualizuje dane zalogowanego użytkownika (jako ADMIN).
     *
     * @param request obiekt zawierający nowe dane użytkownika
     * @return potwierdzenie wykonania aktualizacji
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<HttpResponse> updateUserById(
            @PathVariable("id") Long id,
            @RequestBody UpdateUserRequest request) {

        userService.updateUserData(id, request);

        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("User update by admin")
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

    /**
     * Zwraca wszystkich użytkowników w systemie.
     *
     * @return odpowiedź HTTP zawierająca listę użytkowników w formacie DTO
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<HttpResponse> getAllUsers() {
        List<UserDto> userDtos = userService.getAllUsers()
                .stream()
                .map(userDtoMapper::toDto)
                .toList();

        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("All users")
                        .message("Returned all users")
                        .data(Map.of("users", userDtos))
                        .build()
        );
    }

    /**
     * Blokuje użytkownika (blocked = true).
     *
     * @param id identyfikator użytkownika
     * @return odpowiedź potwierdzająca zablokowanie użytkownika
     */
    @PutMapping("/{id}/block")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<HttpResponse> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok(HttpResponse.builder()
                .message("User has been blocked")
                .statusCode(200)
                .build());
    }

    /**
     * Odblokowuje użytkownika (blocked = false).
     *
     * @param id identyfikator użytkownika
     * @return odpowiedź potwierdzająca odblokowanie użytkownika
     */
    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<HttpResponse> unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return ResponseEntity.ok(HttpResponse.builder()
                .message("User has been unblocked")
                .statusCode(200)
                .build());
    }

    /**
     * Weryfikuje użytkownika (locked = true).
     *
     * @param id identyfikator użytkownika
     * @return odpowiedź potwierdzająca zweryfikowanie użytkownika
     */
    @PutMapping("/{id}/verify")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<HttpResponse> verifyUser(@PathVariable Long id) {
        userService.verifyUser(id);
        return ResponseEntity.ok(HttpResponse.builder()
                .message("User has been verified (unlocked)")
                .statusCode(200)
                .build());
    }
}
