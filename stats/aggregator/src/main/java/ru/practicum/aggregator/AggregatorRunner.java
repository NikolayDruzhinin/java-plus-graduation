package ru.practicum.aggregator;

import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import lombok.RequiredArgsConstructor;
import ru.practicum.aggregator.service.AggregatorStarter;

@Component
@RequiredArgsConstructor
public class AggregatorRunner implements CommandLineRunner {
    final AggregatorStarter aggregationStarter;

    @Override
    public void run(String... args) throws Exception {
        aggregationStarter.run();
    }
}
