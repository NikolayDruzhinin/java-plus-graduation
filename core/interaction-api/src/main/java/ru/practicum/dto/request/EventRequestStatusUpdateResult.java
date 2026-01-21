package ru.practicum.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class EventRequestStatusUpdateResult {
     List<ParticipationRequestDto> confirmedRequests;
     List<ParticipationRequestDto> rejectedRequests;
}
