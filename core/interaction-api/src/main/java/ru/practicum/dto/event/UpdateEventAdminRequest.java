package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.event.model.Location;
import ru.practicum.validation.UpdateGroup;

import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000, message = "Длина аннотации должна быть >= 20 символов и <= 2000", groups = UpdateGroup.class)
    @NotBlank
    @Nullable
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Длина описания должна быть >= 20 символов и <= 7000", groups = UpdateGroup.class)
    @NotBlank
    @Nullable
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Size(min = 3, max = 120, message = "Длина заголовка должна быть >= 3 символов и <= 120", groups = UpdateGroup.class)
    @NotBlank
    @Nullable
    private String title;

}
