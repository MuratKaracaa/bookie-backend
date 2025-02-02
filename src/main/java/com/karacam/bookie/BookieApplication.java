package com.karacam.bookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@SpringBootApplication
@EnableCaching
@EnableSpringConfigured
public class BookieApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookieApplication.class, args);
    }

}
