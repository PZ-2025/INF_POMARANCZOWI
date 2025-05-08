package com.orange.bookmanagment.user.web.controller;

import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import com.orange.bookmanagment.user.service.UserService;
import com.orange.bookmanagment.user.web.mapper.UserDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/v1/user")

@RequiredArgsConstructor
class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

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

    @PostMapping("/upload-avatar")
    public ResponseEntity<HttpResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
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
}
