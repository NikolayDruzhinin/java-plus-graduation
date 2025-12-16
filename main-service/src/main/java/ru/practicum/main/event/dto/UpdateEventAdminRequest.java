package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.main.event.entity.AdminStateAction;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;

    @Min(0)
    private Integer participantLimit;
    private Boolean requestModeration;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private AdminStateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;
}

