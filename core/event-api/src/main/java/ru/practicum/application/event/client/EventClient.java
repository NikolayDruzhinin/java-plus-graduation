package ru.practicum.application.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.application.event.api.AbstractEventInterface;

@FeignClient(name = "event-service")
public interface EventClient extends AbstractEventInterface {
}
