package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryService;
import ru.practicum.client.user.UserAdminClient;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final UserAdminClient userAdminClient;

    @Override
    public CompilationDto create(NewCompilationDto dto) {
        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events.addAll(eventRepository.findAllById(dto.getEvents()));
        }

        Compilation compilation = Compilation.builder()
                .title(dto.getTitle())
                .pinned(Boolean.TRUE.equals(dto.getPinned()))
                .events(events)
                .build();

        if (dto.getPinned() == null)
            compilation.setPinned(false);

        Compilation saved = compilationRepository.save(compilation);
        List<EventShortDto> eventDtos = mapToShortDtos(saved.getEvents());

        return CompilationMapperCustom.toDto(saved, eventDtos);
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = checkAndGetCompilationById(compId);

        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
            compilation.setEvents(events);
        }

        Compilation updated = compilationRepository.save(compilation);
        return CompilationMapperCustom.toDto(updated, mapToShortDtos(updated.getEvents()));
    }

    @Override
    public void delete(Long compId) {
        checkAndGetCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Compilation> comps = (pinned != null)
                ? compilationRepository.findAllByPinned(pinned, page)
                : compilationRepository.findAll(page).getContent();

        return comps.stream()
                .map(comp -> CompilationMapperCustom.toDto(comp, mapToShortDtos(comp.getEvents())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation comp = checkAndGetCompilationById(compId);
        return CompilationMapperCustom.toDto(comp, mapToShortDtos(comp.getEvents()));
    }

    private List<EventShortDto> mapToShortDtos(Collection<Event> events) {
        List<Long> initiatorIds = events.stream()
                .map(Event::getInitiatorId)
                .toList();

        Set<Long> categoryIds = events.stream()
                .map(Event::getCategory)
                .collect(Collectors.toSet());

        Map<Long, UserShortDto> users = userAdminClient.getUsers(initiatorIds,0, 10)
                .stream()
                .map(u -> new UserShortDto(u.getId(), u.getName()))
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));

        Map<Long, CategoryDto> categories = categoryService.getByIds(categoryIds.stream().toList())
                .stream()
                .collect(Collectors.toMap(CategoryDto::getId, Function.identity()));

        return events.stream()
                .map(event -> EventShortDto.builder()
                        .id(event.getId()) // уже Long
                        .annotation(event.getAnnotation())
                        .title(event.getTitle())
                        .eventDate(event.getEventDate())
                        .paid(event.getPaid())
                        .confirmedRequests(event.getConfirmedRequests())
                        .rating(event.getRating())
                        .initiator(users.get(event.getInitiatorId()))
                        .category(categories.get(event.getCategory()))
                        .build())
                .toList();
    }

    private Compilation checkAndGetCompilationById(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + compId + " not found"));
    }

}
