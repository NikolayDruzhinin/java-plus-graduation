package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import ru.practicum.dto.event.annotation.ValidEventDate;

import java.time.LocalDateTime;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Length(min = 20, max = 2000)
    String annotation;
    @NotNull
    long category;
    @NotBlank
    @Length(min = 20, max = 7000)
    String description;
    @NotNull
    @ValidEventDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull
    Location location;
    @JsonSetter(nulls = Nulls.SKIP) // если поле null, то
    Boolean paid;
    @PositiveOrZero
    int participantLimit;
    Boolean requestModeration;
    @NotBlank
    @Length(min = 3, max = 120)
    String title;
}
