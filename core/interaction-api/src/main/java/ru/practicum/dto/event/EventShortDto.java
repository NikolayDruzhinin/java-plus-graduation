package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;


import java.time.LocalDateTime;

@Data
public class EventShortDto {
    private Long id;
    private String annotation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private boolean paid;
    private String title;
    private Long confirmedRequests;
    private Long views;
    private UserShortDto initiator;
    private CategoryDto category;
}
