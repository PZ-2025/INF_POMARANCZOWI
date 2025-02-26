package com.orange.bookmanagment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@Modulith
public class BookManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookManagementApplication.class, args);
    }

}
