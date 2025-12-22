package ru.practicum.compilation.mapper;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public CompilationDto toCompilationDto(Compilation compilation, Map<Long, User> users) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(mapEventsToShortDtos(compilation.getEvents(), users));
        return compilationDto;
    }

    public List<CompilationDto> toCompilationDto(List<Compilation> compilations, Map<Long, User> users) {
        return compilations.stream()
                .map(c -> toCompilationDto(c, users))
                .collect(Collectors.toList());
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setEvents(new HashSet<>());
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

    private Set<EventShortDto> mapEventsToShortDtos(Set<Event> events, Map<Long, User> users) {
        return events.stream()
                .map(e -> eventMapper.toEventShortDto(e, users.get(e.getInitiatorId())))
                .collect(Collectors.toSet());
    }
}