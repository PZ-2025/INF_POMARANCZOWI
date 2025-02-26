package com.orange.bookmanagment;

import com.orange.bookmanagment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@Modulith
@RequiredArgsConstructor
public class BookManagementApplication {

    private final UserService userService;
    public static void main(String[] args) {
        SpringApplication.run(BookManagementApplication.class, args);
    }

}
