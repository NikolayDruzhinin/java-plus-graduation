package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapperCustom;
import ru.practicum.category.CategoryService;
import ru.practicum.client.user.UserAdminClient;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdatedEventDto;
import ru.practicum.dto.event.enums.State;
import ru.practicum.dto.event.enums.StateAction;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.exception.ConflictPropertyConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.yandex.practicum.ewm.stats.proto.ActionTypeProto;
import ru.yandex.practicum.ewm.stats.proto.RecommendedEventProto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final CategoryService categoryService;
    private final EventDtoMapper eventDtoMapper;
    private final UserAdminClient userAdminClient;
    private final CategoryMapperCustom categoryMapperCustom;

    public EventFullDto saveEvent(NewEventDto newEventDto, long userId) {
        log.debug("Попытка сохранить новое событие: {}", newEventDto);

        UserDto user = checkUserId(userId);
        CategoryDto categoryDto = checkCategoryId(newEventDto.getCategory());

        Event event = eventDtoMapper.mapToModel(newEventDto, userId);
        event.setCreatedOn(LocalDateTime.now());

        if (newEventDto.getPaid() == null)
            event.setPaid(false);
        if (newEventDto.getRequestModeration() == null)
            event.setRequestModeration(true);
        log.debug("Событие после маппинга: {}", event);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);
        Event savedEvent = eventRepository.save(event);
        log.info("Событие сохранено с ID {}", savedEvent.getId());
        EventFullDto dto = eventDtoMapper.mapToFullDto(savedEvent, user, categoryDto);
        log.debug("Сформированный EventFullDto: {}", dto);
        return dto;
    }

    public EventFullDto updateEvent(UpdatedEventDto updatedEvent,
                                    long userId, long eventId) {
        log.info("Попытка обновить событие. userId={}, eventId={}", userId, eventId);

        UserDto userDto = checkUserId(userId);
        Event event = checkAndGetEventById(eventId);
        CategoryDto categoryDto = categoryService.getById(event.getCategory());

        if (!event.getState().equals(State.CANCELED) && !event.getState().equals(State.PENDING)) {
            log.warn("Событие нельзя изменить. Состояние: {}, Модерация: {}",
                    event.getState(), event.getRequestModeration());
            throw new ConflictPropertyConstraintException(
                    "Изменить можно только отменённое событие или находящееся на модерации");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Событие начинается слишком скоро: {}", event.getEventDate());
            throw new ConditionsNotMetException(
                    "Нельзя изменить событие, которое начинается в течение двух часов");
        }

        log.info("Применение обновлений к событию id={}", eventId);
        applyUpdate(event, updatedEvent);

        Event savedUpdatedEvent = eventRepository.save(event);
        log.info("Событие успешно обновлено. id={}", savedUpdatedEvent.getId());

        return eventDtoMapper.mapToFullDto(savedUpdatedEvent, userDto, categoryDto);
    }

    public EventFullDto updateAdminEvent(UpdatedEventDto updatedEvent,
                                         long eventId) {
        log.info("Админ запрашивает обновление события. eventId={}", eventId);

        Event event = checkAndGetEventById(eventId);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            log.warn("Событие начинается в течение часа: {}", event.getEventDate());
            throw new ConditionsNotMetException(
                    "Нельзя изменить событие, которое начинается в течение часа");
        }

        if (event.getState().equals(State.PUBLISHED))
            throw new ConflictPropertyConstraintException("Нельзя менять статус у уже опубликованного события");
        if (event.getState().equals(State.CANCELED))
            throw new ConflictPropertyConstraintException("Нельзя менять статус у уже отмененного события");

        if (updatedEvent.getStateAction() != null
                && updatedEvent.getStateAction().equals(StateAction.PUBLISH_EVENT)
                && !event.getState().equals(State.PENDING)) {
            log.warn("Попытка опубликовать событие без модерации. eventId={}", eventId);
            throw new ConflictPropertyConstraintException(
                    "Нельзя опубликовать событие, которое не находится в ожидании публикации");
        }

        log.info("Применение обновлений админом к событию id={}", eventId);
        applyUpdate(event, updatedEvent);
        Event savedUpdatedEvent = eventRepository.save(event);
        log.info("Событие успешно обновлено админом. id={}", savedUpdatedEvent.getId());
        UserDto user = userAdminClient.getUserById(savedUpdatedEvent.getInitiatorId());
        CategoryDto categoryDto = categoryService.getById(savedUpdatedEvent.getCategory());
        return eventDtoMapper.mapToFullDto(savedUpdatedEvent, user, categoryDto);
    }


    public List<EventFullDto> getEvents(String text, List<Long> categories, Boolean paid,
                                        String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                        String sort, Integer from, Integer size, String user) {
        log.info("Получен запрос на получение событий. Пользователь: {}, параметры: [text: {}, categories: {}, paid: {}, rangeStart: {}, rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}]",
                user, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (rangeStart == null || rangeStart.isBlank()) {
            rangeStart = LocalDateTime.now().format(formatter);
            log.info("rangeStart не был задан, использовано текущее время: {}", rangeStart);
        }

        LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = null;

        if (rangeEnd != null && !rangeEnd.isBlank()) {
            end = LocalDateTime.parse(rangeEnd, formatter);
            if (start.isAfter(end)) {
                throw new BadRequestException("Время начала события не может быть позже даты окончания");
            }
        }

        log.info("Финальный диапазон дат: start={}, end={}", start, end);

        boolean isAdmin = !"user".equalsIgnoreCase(user);


        Sort sortParam;
        if (sort != null && sort.equals("EVENT_DATE")) {
            sortParam = Sort.by("eventDate").ascending();
        } else {
            sortParam = Sort.unsorted();
        }

        int safeFrom = (from != null) ? from : 0;
        int safeSize = (size != null) ? size : 10;
        PageRequest page = PageRequest.of(safeFrom / safeSize, safeSize, sortParam);

        List<Event> events = eventRepository.getEvents(text, categories, paid,
                        start, end, onlyAvailable, isAdmin, page)
                .stream()
                .toList();

        List<Long> userIds = events.stream()
                .map(Event::getInitiatorId)
                .toList();

        List<Long> categoryIds = events.stream()
                .map(Event::getCategory)
                .toList();

        Map<Long, UserDto> users = userAdminClient.getUsersByIds(userIds)
                .stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));
        Map<Long, CategoryDto> c = categoryService.getByIds(categoryIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, Function.identity()));

        List<EventFullDto> eventsDto = eventRepository.getEvents(text, categories, paid,
                        start, end, onlyAvailable, isAdmin, page)
                .stream()
                .map(e -> eventDtoMapper.mapToFullDto(e, users.get(e.getInitiatorId()),
                        c.get(Math.toIntExact(e.getCategory()))))
                .toList();

        log.info("Получено {} событий после фильтрации", events.size());

        Map<Long, Double> eventRating = getEventRating(events);

        List<EventFullDto> eventsWithViews = eventsDto.stream()
                .map(e -> {
                    double rating = eventRating.getOrDefault(e.getId(), 0.0);
                    e.setRating(rating);
                    return e;
                }).toList();

        if (sort != null && sort.equals("RATING")) {
            log.info("Сортировка по количеству просмотров");
            return eventsDto.stream()
                    .sorted(Comparator.comparing(EventFullDto::getRating))
                    .toList();
        }

        return eventsWithViews;
    }

    public List<EventShortDto> getEventsByUserId(long userId, Integer from, Integer size) {
        log.debug("Получен запрос на получение событий пользователя с id={} (from={}, size={})", userId, from, size);

        log.debug("Проверка существования пользователя с id={}", userId);
        UserDto userDto = userAdminClient.getUserById(userId);
        if (userDto == null) {
            log.warn("Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        log.debug("Пользователь с id {} найден", userId);

        int safeFrom = (from != null) ? from : 0;
        int safeSize = (size != null) ? size : 10;
        PageRequest page = PageRequest.of(safeFrom / safeSize, safeSize);

        log.debug("Ищем события пользователя с id={} с пагинацией from={}, size={}", userId, safeFrom, safeSize);
        List<Event> userEvents = eventRepository.findAllByInitiatorId(userId, page);
        log.debug("Найдено {} событий", userEvents.size());

        Map<Long, Double> eventRating = getEventRating(userEvents);
        List<Long> categoryIds = userEvents.stream()
                .map(Event::getCategory)
                .toList();

        Map<Long, CategoryDto> c = categoryService.getByIds(categoryIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, Function.identity()));

        return userEvents.stream()
                .map(e -> {
                    double rating = eventRating.getOrDefault(e.getId(), 0.0);
                    e.setRating(rating);
                    return e;
                })
                .map(e -> eventDtoMapper.mapToShortDto(e, userDto, c.get(e.getCategory())))
                .toList();
    }

    public EventFullDto getEventByUserIdAndEventId(long userId, long eventId) {
        log.debug("Получен запрос на получение события с id={} пользователя с id={}",
                eventId, userId);
        UserDto u = userAdminClient.getUserById(userId);
        if (u == null) {
            log.warn("Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        Optional<Event> eventOpt = eventRepository.findByInitiatorIdAndId(userId, eventId);
        if (eventOpt.isEmpty()) {
            log.warn("Событие с id={} для пользователя с id={} не найдено", eventId, userId);
            throw new NotFoundException("Событие с id " + eventId + " не найдено для пользователя " + userId);
        }

        log.debug("Событие с id={} найдено для пользователя с id={}", eventId, userId);
        CategoryDto cat = categoryService.getById(eventOpt.get().getCategory());
        return eventDtoMapper.mapToFullDto(eventOpt.get(), u, cat);
    }


    public EventFullDto getEventById(Long eventId, Long userId) {
        log.debug("Получен запрос на получение события с eventId={} от userId={}", eventId, userId);

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            log.warn("Событие с id={} не найдено", eventId);
            throw new NotFoundException("Событие с id " + eventId + " не найдено");
        }

        Event event = eventOpt.get();

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с id " + eventId + " не опубликовано");
        }

        log.debug("Событие с id={} найдено", eventId);
        UserDto u = userAdminClient.getUserById(userId);
        CategoryDto c = categoryService.getById(event.getCategory());
        EventFullDto dto = eventDtoMapper.mapToFullDto(event, u, c);
        Map<Long, Double> eventRating = getEventRating(List.of(event));
        double rating = eventRating.getOrDefault(event.getId(), 0.0);
        dto.setRating(rating);

        log.debug("Отправляем статистику по просмотру события с eventId " + eventId
                + " пользователем с userId " + userId);
        if (userId != null) {
            statsClient.sendUserAction(userId, eventId, ActionTypeProto.ACTION_VIEW);
        }
        log.debug("Отправили статистику");

        log.debug("Рейтинг события с id={}: {}", eventId, rating);
        return dto;
    }

    public List<EventShortDto> getRecommendations(long userId) {
        UserDto u = checkUserId(userId);
        UserShortDto userShortDto = userAdminClient.getUserShortById(userId);
        log.debug("Спрашиваем statsClient о рекомендациях для пользователя");
        Map<Long, Double> eventRating = statsClient.getRecommendationsForUser(userId, 10)
                .collect(
                        Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore)
                );
        log.debug("Получили рекомендации для пользователя");
        List<Event> events = eventRepository.findByIdIn(eventRating.keySet()).stream()
                .map(event -> {
                    double rating = eventRating.getOrDefault(event.getId(), 0.0);
                    event.setRating(rating);
                    return event;
                })
                .toList();
        Map<Long, CategoryDto> eventCategory = categoryService.getByIds(events
                        .stream().map(Event::getCategory).toList())
                .stream().collect(Collectors.toMap(CategoryDto::getId, Function.identity()));

        return events.stream()
                .map(e -> eventDtoMapper.mapToShortDto(e, u, eventCategory.get(e.getCategory())))
                .toList();
    }

    public Integer checkInitiatorEvent(Long eventId, Long initiatorId) {
        Optional<Event> found = eventRepository.findByInitiatorIdAndId(initiatorId, eventId);
        if (found.isEmpty()) {
            return Integer.valueOf(0);
        } else {
            return Integer.valueOf(1);
        }
    }

    public EventFullDto saveFullEvent(EventFullDto event) {
        log.info("Начат процесс полного сохранения события");
        Event foundEvent = checkAndGetEventById(event.getId());
        log.debug("Количество запросов до обновления: " + foundEvent.getConfirmedRequests());
        applyUpdateFromFull(foundEvent, event);
        Event saved = eventRepository.saveAndFlush(foundEvent);
        log.debug("Количество запросов после обновления: " + saved.getConfirmedRequests());
        UserDto u = userAdminClient.getUserById(saved.getInitiatorId());
        CategoryDto c = categoryService.getById(event.getCategory().getId());
        return eventDtoMapper.mapToFullDto(saved, u, c);
    }

    public void likeEvent(Long eventId, Long userId) {
        checkAndGetEventById(eventId);
        checkUserId(userId);
        log.debug("Отправляем статистику о действии пользователя с userId " + userId);
        if (userId != null) {
            statsClient.sendUserAction(eventId, userId, ActionTypeProto.ACTION_LIKE);
        }
        log.debug("Отправляем статистику о действии пользователя");
    }

    private void applyUpdateFromFull(Event event, EventFullDto dto) {
        log.info("Начато полное обновление Event из EventFullDto");

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            event.setCategory(dto.getCategory().getId());
        }

        if (dto.getConfirmedRequests() >= 0) {
            event.setConfirmedRequests(dto.getConfirmedRequests());
        }

        if (dto.getParticipantLimit() >= 0) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getCreatedOn() != null) {
            event.setCreatedOn(dto.getCreatedOn());
        }

        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }

        if (dto.getPublishedOn() != null) {
            event.setPublishedOn(dto.getPublishedOn());
        }

        if (dto.getInitiator() != null && dto.getInitiator().getId() != null) {
            event.setInitiatorId(dto.getInitiator().getId());
        }

        if (dto.getLocation() != null) {
            if (dto.getLocation().getLat() != 0.0) {
                event.setLocationLat(dto.getLocation().getLat());
            }
            if (dto.getLocation().getLon() != 0.0) {
                event.setLocationLon(dto.getLocation().getLon());
            }
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getState() != null) {
            event.setState(dto.getState());
        }

        if (dto.getRating() != null) {
            event.setRating(dto.getRating());
        }

        log.info("Обновление Event завершено успешно");
    }


    private void applyUpdate(Event event, UpdatedEventDto dto) {
        log.info("Начат процесс обновления события, просматриваются поля на изменения");
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocationLat(dto.getLocation().getLat());
            event.setLocationLon(dto.getLocation().getLon());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() > 0) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getCategory() > 0) {
            categoryService.getById(dto.getCategory());
            event.setCategory(dto.getCategory());
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                case PUBLISH_EVENT -> event.setState(State.PUBLISHED);
                case REJECT_EVENT -> event.setState(State.CANCELED);
                default -> throw new BadRequestException("Неизвестное действие: " + dto.getStateAction());
            }
        }
        log.info("Событие обновлено");
    }

    private UserDto checkUserId(long userId) {
        log.debug("Отправляем в userClient вопрос о существовании пользователя с userId " + userId);
        UserDto userDto = userAdminClient.getUserById(userId);
        if (userDto == null) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        return userDto;
    }

    private CategoryDto checkCategoryId(long catId) {
        log.debug("Отправляем в userClient вопрос о существовании категории с catId " + catId);
        CategoryDto categoryDto = categoryService.getById(catId);
        return categoryDto;
    }

    private Event checkAndGetEventById(long eventId) {
        log.debug("Ищем событие с eventId " + eventId);
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            log.warn("Событие с id {} не найдено", eventId);
            throw new NotFoundException("Событие с id " + eventId + " не найдено");
        }
        return eventOpt.get();
    }

    private Map<Long, Double> getEventRating(List<Event> events) {
        log.debug("Ищем id для событий для поиска рейтинга");
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();
        log.debug("Нашли id для событий, ищем рейтинг");
        Map<Long, Double> res = statsClient.getInteractionsCount(eventIds)
                .collect(Collectors.toMap(
                        RecommendedEventProto::getEventId,
                        RecommendedEventProto::getScore
                ));
        log.debug("Нашли рейтинг");
        return res;
    }
}
