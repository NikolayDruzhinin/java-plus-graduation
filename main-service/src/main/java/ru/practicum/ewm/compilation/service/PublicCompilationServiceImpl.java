package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.mapper.CompilationMapper;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long id) {
        CompilationDto compilationDto = CompilationMapper
                .toCompilationDto(compilationRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Подборка с " + id + "не найдена")));
        return compilationDto;
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from, size);
        Page<Compilation> pageCompilations;
        if (pinned != null) {
            pageCompilations = compilationRepository.findAllByPinned(pinned, page);
        } else {
            pageCompilations = compilationRepository.findAll(page);
        }

        List<CompilationDto> compilationsDto = CompilationMapper.toCompilationDto(pageCompilations);
        log.info("получен список compilationsDto from = " + from + " size " + size);

        return compilationsDto;
    }
}
