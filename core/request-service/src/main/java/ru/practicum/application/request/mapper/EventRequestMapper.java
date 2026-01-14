package ru.practicum.application.request.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.application.api.dto.request.EventRequestDto;
import ru.practicum.application.request.model.EventRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.application.api.util.JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME;


@Component
@AllArgsConstructor
public class EventRequestMapper {
    public EventRequestDto mapRequest(EventRequest request) {
        EventRequestDto dto = new EventRequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequester());
        dto.setEvent(request.getEvent());
        dto.setCreated(request.getCreated().format(DateTimeFormatter.ofPattern(JSON_FORMAT_PATTERN_FOR_TIME)));
        dto.setStatus(request.getStatus());
        return dto;
    }

    public EventRequestDto mapRequestWithConfirmedAndRejected(List<EventRequestDto> confirmedRequests,
                                                              List<EventRequestDto> rejectedRequests) {
        EventRequestDto result = new EventRequestDto();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        return result;
    }
}
