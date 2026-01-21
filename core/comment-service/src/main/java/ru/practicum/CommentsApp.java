package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(clients = {
        ru.practicum.client.user.UserAdminClient.class,
        ru.practicum.client.event.EventClient.class
})
public class CommentsApp {
    public static void main(String[] args) {
        SpringApplication.run(CommentsApp.class, args);
    }
}
