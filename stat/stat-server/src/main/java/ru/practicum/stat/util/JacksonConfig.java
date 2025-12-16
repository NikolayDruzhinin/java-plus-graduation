package ru.practicum.stat.util;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class JacksonConfig {

    @Bean
    public Module customDateTimeModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
        return module;
    }
}