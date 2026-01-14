package ru.practicum.request.mapper;


import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        dto.setEvent(request.getEventId());
        dto.setRequester(request.getRequesterId());
        dto.setStatus(request.getStatus().name());
        return dto;
    }
}
