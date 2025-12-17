package ru.practicum.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.main",
        "ru.practicum.stat"
})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
