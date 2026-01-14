package ru.practicum.stats.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.stats.analyzer.starter.SimilarityStarter;
import ru.practicum.stats.analyzer.starter.UserActionStarter;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyzerServer {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerServer.class, args);
        SimilarityStarter similarityStarter = context.getBean(SimilarityStarter.class);
        UserActionStarter userActionStarter = context.getBean(UserActionStarter.class);

        Thread similarity = new Thread(similarityStarter);
        similarity.setName("similarity");
        similarity.start();

        Thread action = new Thread(userActionStarter);
        action.setName("action");
        action.start();
    }
}
