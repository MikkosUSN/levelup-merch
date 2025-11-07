package com.clc.levelup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Level Up Merch Spring Boot application.
 * <p>
 * This class bootstraps the entire system by launching the embedded server
 * and initializing all configured Spring components, services, and controllers.
 * </p>
 */
@SpringBootApplication
public class LevelupMerchApplication {

    /**
     * Main method that starts the Spring Boot application.
     * @param args command-line arguments (not typically used)
     */
    public static void main(String[] args) {
        SpringApplication.run(LevelupMerchApplication.class, args);
    }
}
