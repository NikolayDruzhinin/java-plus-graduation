package ru.practicum.ewm.mapper;

import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.model.EndpointHit;

public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitInputDto endpointHitInputDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setIp(endpointHitInputDto.getIp());
        endpointHit.setApp(endpointHitInputDto.getApp());
        endpointHit.setUri(endpointHitInputDto.getUri());
        endpointHit.setTimestamp(endpointHitInputDto.getTimestamp());

        return endpointHit;
    }
}
