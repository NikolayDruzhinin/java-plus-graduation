package ru.practicum.stats.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.stats.aggregator.starter.AggregatorStarter;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AggregatorServer {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AggregatorServer.class, args);
        AggregatorStarter starter = context.getBean(AggregatorStarter.class);
        starter.start();
    }
}
