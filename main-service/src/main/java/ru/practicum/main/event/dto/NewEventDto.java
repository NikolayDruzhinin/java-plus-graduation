package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid = false;

    @Min(0)
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}

