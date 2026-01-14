package ru.practicum.application.compilation.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.api.dto.category.CategoryDto;
import ru.practicum.application.api.dto.compilation.NewCompilationDto;
import ru.practicum.application.api.dto.compilation.ResponseCompilationDto;
import ru.practicum.application.api.dto.event.EventShortDto;
import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.api.exception.ValidationException;
import ru.practicum.application.api.request.compilation.UpdateCompilationRequest;
import ru.practicum.application.category.client.CategoryClient;
import ru.practicum.application.compilation.mapper.CompilationMapper;
import ru.practicum.application.compilation.model.Compilation;
import ru.practicum.application.compilation.repository.CompilationRepository;
import ru.practicum.application.event.mapper.EventMapper;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.event.repository.EventRepository;
import ru.practicum.application.user.client.UserClient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationServiceImpl implements CompilationService {
    final CompilationRepository compilationRepository;
    final EventRepository eventRepository;

    final UserClient userClient;
    final CategoryClient categoryClient;

    @Override
    @Transactional
    public ResponseCompilationDto addCompilation(NewCompilationDto dto) throws NotFoundException {
        Compilation compilation = CompilationMapper.mapToCompilation(dto);
        compilation.setPinned(Optional.ofNullable(compilation.getPinned()).orElse(false));

        List<Event> events = getEventsFromDto(dto);
        compilation.setEvents(events);
        Compilation savedCompilation = compilationRepository.save(compilation);

        Set<Long> categoryIds = events.stream()
                .map(Event::getCategory)
                .collect(Collectors.toSet());
        Set<Long> userIds = events.stream()
                .map(Event::getInitiator)
                .collect(Collectors.toSet());

        Map<Long, CategoryDto> categories = categoryIds.isEmpty() ?
                Collections.emptyMap() :
                categoryClient.getCategoriesByIds(categoryIds).stream()
                        .collect(Collectors.toMap(CategoryDto::getId, Function.identity()));

        Map<Long, UserDto> users = userIds.isEmpty() ?
                Collections.emptyMap() :
                userClient.getUsersList(new ArrayList<>(userIds), 0, userIds.size()).stream()
                        .collect(Collectors.toMap(UserDto::getId, Function.identity()));

        ResponseCompilationDto response = CompilationMapper.mapToResponseCompilation(savedCompilation);
        response.setEvents(events.stream()
                .map(event -> EventMapper.mapEventToShortDto(
                        event,
                        categories.get(event.getCategory()),
                        users.get(event.getInitiator())
                ))
                .collect(Collectors.toList()));

        return response;
    }

    @Override
    public ResponseCompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilation) throws NotFoundException {
        Compilation old = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Указанная подборка не найдена " + compId));

        Compilation update = new Compilation();
        update.setId(compId);
        update.setPinned(compilation.getPinned() == null ? old.getPinned() : compilation.getPinned());
        update.setTitle(compilation.getTitle() == null ? old.getTitle() : compilation.getTitle());

        List<Event> events = getEventsFromDto(compilation);
        update.setEvents(events == null ? old.getEvents() : events);

        return CompilationMapper.mapToResponseCompilation(compilationRepository.save(update));
    }

    private List<Event> getEventsFromDto(NewCompilationDto compilation) {
        List<Event> events = Collections.emptyList();
        if (compilation.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilation.getEvents());
            log.info("EventIDS: {}", events.toString());
        }
        return events;
    }

    private List<Event> getEventsFromDto(UpdateCompilationRequest compilation) {
        List<Event> events = new ArrayList<>();
        if (compilation.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilation.getEvents());
        }
        return events;
    }

    @Override
    public ResponseCompilationDto getCompilationById(Long id) throws NotFoundException {
        log.info("Получение информации о подборке, id={}", id);
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Подборка не найдена " + id)
        );

        List<Long> usersIds = compilation.getEvents().stream().map(Event::getInitiator).toList();
        Set<Long> categoriesIds = compilation.getEvents().stream().map(Event::getCategory).collect(Collectors.toSet());
        Map<Long, UserDto> users = userClient.getUsersList(usersIds, 0, Math.max(compilation.getEvents().size(), 1)
        ).stream().collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
        Map<Long, CategoryDto> categories = categoryClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

        return compileDtoWithEvents(compilation, categories, users);
    }

    @Override
    public List<ResponseCompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("pinned {}", pinned);
        List<Compilation> allWithPinned = compilationRepository.findAllWithPinned(pinned, Pageable.ofSize(size + from));
        return compileDtosWithEvents(allWithPinned);
    }

    @Override
    public void deleteCompilation(Long id) throws ValidationException, NotFoundException {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Указанная категория не найдена " + id));
        try {
            compilationRepository.delete(compilation);
        } catch (Exception e) {
            throw new ValidationException("Невозможно удаление используемой категории события " + e.getMessage());
        }
    }

    private List<ResponseCompilationDto> compileDtosWithEvents(List<Compilation> compilations) {
        List<Event> events = new ArrayList<>();
        for (Compilation compilation : compilations) {
            events.addAll(compilation.getEvents());
        }

        List<Long> usersIds = events.stream().map(Event::getInitiator).toList();
        Set<Long> categoriesIds = events.stream().map(Event::getCategory).collect(Collectors.toSet());
        Map<Long, UserDto> users = userClient.getUsersList(usersIds, 0, Math.max(events.size(), 1)).stream()
                .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
        Map<Long, CategoryDto> categories = categoryClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

        return compilations.stream()
                .map(compilation -> compileDtoWithEvents(compilation, categories, users))
                .collect(Collectors.toList());
    }

    private ResponseCompilationDto compileDtoWithEvents(Compilation compilation,
                                                        Map<Long, CategoryDto> categories,
                                                        Map<Long, UserDto> users) {
        ResponseCompilationDto result = CompilationMapper.mapToResponseCompilation(compilation);
        List<EventShortDto> eventDtos = new ArrayList<>();
        for (Event event : compilation.getEvents()) {
            eventDtos.add(EventMapper.mapEventToShortDto(event,
                    categories.get(event.getCategory()), users.get(event.getInitiator())));
        }
        result.setEvents(eventDtos);
        return result;
    }
}
