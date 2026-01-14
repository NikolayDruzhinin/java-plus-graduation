package ru.practicum.dto.request;

import lombok.Data;
import ru.practicum.request.model.RequestStatus;


import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
