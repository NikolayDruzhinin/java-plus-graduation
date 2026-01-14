package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteCommentsDto {
    @NotEmpty
    List<Long> commentsIds;
}