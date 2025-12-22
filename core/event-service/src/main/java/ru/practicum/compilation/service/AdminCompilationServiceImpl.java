package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.UserClient;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final UserClient userServiceClient;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        Set<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
        compilation.setEvents(events);

        Compilation savedCompilation = compilationRepository.save(compilation);
        Map<Long, User> users = loadUsersForEvents(savedCompilation.getEvents());

        return compilationMapper.toCompilationDto(savedCompilation, users);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long id) {
        Compilation compilation = checkExistCompilationById(id);

        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            Set<Event> events = eventRepository.findByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);
        Map<Long, User> users = loadUsersForEvents(updatedCompilation.getEvents());

        return compilationMapper.toCompilationDto(updatedCompilation, users);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long id) {
        checkExistCompilationById(id);
        compilationRepository.deleteById(id);
    }

    private Compilation checkExistCompilationById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборки событий с id = " + id + " не существует"));
    }

    private Map<Long, User> loadUsersForEvents(Set<Event> events) {
        List<Long> userIds = events.stream()
                .map(Event::getInitiatorId)
                .collect(Collectors.toList());
        return userServiceClient.getUsersWithIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
