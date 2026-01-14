package ru.practicum.stats.analyzer.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.mapper.SimilarityMapper;
import ru.practicum.stats.analyzer.repository.SimilarityRepository;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SimilarityService {
    final SimilarityRepository repository;


    public void save(EventSimilarityAvro avro) {
        log.info("Сохранение сходства событий {} и {}: {}", avro.getEventA(), avro.getEventB(), avro.getScore());
        repository.deleteById(SimilarityMapper.mapAvroToKey(avro));
        repository.save(SimilarityMapper.mapAvroToEntity(avro));
    }
}
