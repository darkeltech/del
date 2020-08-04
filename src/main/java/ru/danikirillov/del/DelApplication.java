package ru.danikirillov.del;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
public class DelApplication {

    public static void main(String[] args) {
        SpringApplication.run(DelApplication.class, args);
    }

}
