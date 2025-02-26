package com.orange.bookmanagment.user.web.controller;


import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthController {


    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(){
        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(){
        return null;
    }

    @GetMapping("/me")
    public ResponseEntity<HttpResponse> me(){
        return null;
    }

    @GetMapping("/logout")
    public ResponseEntity<HttpResponse> logout(){
        return null;
    }
    @PostMapping("/changePassword")
    public ResponseEntity<HttpResponse> changePassword(){
        return null;
    }




}
