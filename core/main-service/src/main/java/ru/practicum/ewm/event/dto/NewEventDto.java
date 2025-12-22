package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.practicum.ewm.event.model.Location;

import java.time.LocalDateTime;

@Data
public class NewEventDto {

    @NotBlank(message = "Аннотация не должна быть пустой")
    @Size(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000 символов")
    private String annotation;
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @Future(message = "Дата события должна быть в будущем")
    private LocalDateTime eventDate;
    @NotNull(message = "Локация должна быть указана")
    @Valid
    private Location location;
    @NotNull(message = "Поле оплаты должно быть указано")

    private Boolean paid = false;
    @NotNull
    @PositiveOrZero(message = "Лимит участников должен быть положительным числом или равен нулю")
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotBlank(message = "Заголовок не должен быть пустым")
    @Size(min = 3, max = 120, message = "Длина заголовка должна быть от 3 до 120 символов")
    private String title;

    @NotNull(message = "Категория не должна быть пустой")
    private Long category;
}
