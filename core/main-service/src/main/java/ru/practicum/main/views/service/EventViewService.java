package ru.practicum.main.views.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.views.dto.ViewDto;
import ru.practicum.main.views.model.EventView;
import ru.practicum.main.views.repository.EventViewRepository;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Transactional
public class EventViewService {
    private final EventViewRepository eventViewRepository;
    private final EventRepository eventRepository;

    public void increaseViews(Long eventId, String ip) {
        addViews(eventId, ip);
        ViewDto views = totalViews(eventId);
        eventRepository.setViews(eventId, views.getViews());
    }

    private void addViews(Long eventId, String ip) {
        if (eventViewRepository.existsByIpAndEventId(ip, eventId)) {
            return;
        }

        EventView view = new EventView();
        view.setIp(ip);
        view.setEvent(eventRepository.findById(eventId).orElseThrow());
        view.setDate(LocalDateTime.now());
        eventViewRepository.save(view);
    }

    public ViewDto totalViews(Long eventId) {
        Long count = eventViewRepository.countByEventId(eventId);
        ViewDto views = new ViewDto();
        views.setEventId(eventId);
        views.setViews(count);
        return views;
    }
}

