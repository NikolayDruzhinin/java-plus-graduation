package ru.practicum.application.api.dto.event;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.application.api.dto.category.CategoryDto;
import ru.practicum.application.api.dto.enums.EventState;
import ru.practicum.application.api.dto.user.UserDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    Long id;
    @Size(min = 1, max = 128)
    String title;
    @Size(min = 1, max = 1024)
    String annotation;
    CategoryDto category;
    Boolean paid;
    String eventDate;
    UserDto initiator;
    @Size(min = 1, max = 1024)
    String description;
    Integer participantLimit;
    EventState state;
    String createdOn;
    LocationDto location;
    Boolean requestModeration;
    Long confirmedRequests;
    String publishedOn;
    Double rating;
}
