package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.UserClient;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final UserClient userServiceClient;

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с " + id + "не найдена"));

        Map<Long, User> users = loadUsersForEvents(compilation.getEvents());
        return compilationMapper.toCompilationDto(compilation, users);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from, size);
        Page<Compilation> pageCompilations = pinned != null
                ? compilationRepository.findAllByPinned(pinned, page)
                : compilationRepository.findAll(page);

        List<Compilation> compilations = pageCompilations.getContent();
        Map<Long, User> users = loadUsersForAllEvents(compilations);

        List<CompilationDto> compilationsDto = compilationMapper.toCompilationDto(compilations, users);
        log.info("получен список compilationsDto from = {} size {}", from, size);

        return compilationsDto;
    }

    private Map<Long, User> loadUsersForEvents(Set<Event> events) {
        List<Long> userIds = events.stream()
                .map(Event::getInitiatorId)
                .collect(Collectors.toList());
        return userServiceClient.getUsersWithIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private Map<Long, User> loadUsersForAllEvents(List<Compilation> compilations) {
        List<Long> userIds = compilations.stream()
                .flatMap(c -> c.getEvents().stream())
                .map(Event::getInitiatorId)
                .distinct()
                .collect(Collectors.toList());
        return userServiceClient.getUsersWithIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
