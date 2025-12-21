package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.dto.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {

    private static final Integer HOURS_BEFORE_EVENT_START = 1;

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventFullDto> findEventByParams(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size) {

        Pageable pageable = PageRequest.of(from.intValue(), size.intValue());
        Page<Event> events = eventRepository.findByParams(userIds, states, categoryIds, rangeStart, rangeEnd, pageable);

        return EventMapper.toEventFullDto(events);
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event existEvent = checkEventExist(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            existEvent.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            existEvent.setCategory(categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категории c id = " + updateEventAdminRequest.getCategory() + " не существует")));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            existEvent.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            checkIsStartAfterNowPlusHours(updateEventAdminRequest, HOURS_BEFORE_EVENT_START);
            existEvent.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            existEvent.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            existEvent.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            existEvent.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            existEvent.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            setEventState(existEvent, updateEventAdminRequest);
        }
        if (updateEventAdminRequest.getTitle() != null) {
            existEvent.setTitle(updateEventAdminRequest.getTitle());
        }

        return EventMapper.toEventFullDto(eventRepository.save(existEvent));
    }

    private Event checkEventExist(Long id) {
        Optional<Event> maybeEvent = eventRepository.findById(id);
        if (maybeEvent.isPresent()) {
            return maybeEvent.get();
        } else {
            throw new NotFoundException("События с id = " + id + " не существует");
        }
    }

    private void checkIsStartAfterNowPlusHours(UpdateEventAdminRequest updateEventAdminRequest, Integer hours) {
        if (!updateEventAdminRequest.getEventDate().isAfter(LocalDateTime.now().plusHours(hours))) {
            throw new BadRequestException("Дата начала изменяемого события должна быть не ранее чем за " + hours + " час(ов) от даты публикации (текущего времени)");
        }
    }

    private void setEventState(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        switch (updateEventAdminRequest.getStateAction()) {
            case StateAction.REJECT_EVENT:
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ValidationException("Нельзя отклонить событие, которое находится в статусе опубликовано");
                }
                event.setState(EventState.CANCELED);
                break;
            case StateAction.PUBLISH_EVENT:
                if (event.getState() != EventState.PENDING) {
                    throw new ValidationException("Опубликовать событие можно только если оно находится в статусе ожидания");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
        }
    }

}
