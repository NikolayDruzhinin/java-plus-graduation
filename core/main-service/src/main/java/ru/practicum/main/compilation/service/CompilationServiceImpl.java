package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.FindCompilationParams;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.entity.Compilation;
import ru.practicum.main.compilation.mapper.CompilationMapper;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.compilation.repository.CompilationSpecs;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto dto) {
        Compilation newCompilation = CompilationMapper.toEntity(dto);
        Set<Long> newEventIds = dto.getEvents();

        if (newEventIds != null) {
            newCompilation.setEvents(new HashSet<>(eventRepository.findAllById(newEventIds)));
        } else {
            newCompilation.setEvents(Set.of());
        }
        newCompilation.setCreated(LocalDateTime.now());

        Compilation savedCompilation = repository.save(newCompilation);
        return CompilationMapper.toDto(savedCompilation);
    }

    @Override
    @Transactional
    public CompilationDto update(UpdateCompilationRequest dto, Long compId) {
        Compilation oldCompilation = repository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        String newTitle = dto.getTitle();
        Boolean newPinned = dto.getPinned();
        Set<Long> newEvents = dto.getEvents();

        if (newTitle != null) {
            oldCompilation.setTitle(newTitle);
        }
        if (newPinned != null) {
            oldCompilation.setPinned(newPinned);
        }
        if (newEvents != null) {
            oldCompilation.setEvents(new HashSet<>(eventRepository.findAllById(newEvents)));
        }

        Compilation updatedCompilation = repository.save(oldCompilation);
        return CompilationMapper.toDto(updatedCompilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        if (repository.existsById(compId)) {
            repository.deleteById(compId);
        } else {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
    }

    // For public endpoints
    @Override
    public CompilationDto findById(Long compId) {
        Compilation foundCompilation = repository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        return CompilationMapper.toDto(foundCompilation);
    }

    @Override
    public List<CompilationDto> getAll(FindCompilationParams params) {
        List<Compilation> compilations;
        if (params.getPinned() != null) {
            compilations = repository.findAll(CompilationSpecs.isPinned(params.getPinned()),
                    PageRequest.of(params.getFrom(),
                            params.getSize())).getContent();
        } else {
            compilations = repository.findAll(PageRequest.of(params.getFrom(),
                    params.getSize())).getContent();
        }
        return compilations.stream().map(CompilationMapper::toDto).toList();
    }
}
