package com.orange.bookmanagment;

import com.orange.bookmanagment.shared.properties.RsaKeyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableAsync;

@Modulith

@EnableCaching
@EnableAsync
@EnableConfigurationProperties({RsaKeyProperties.class})

@RequiredArgsConstructor

public class BookManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookManagementApplication.class, args);
    }

}
