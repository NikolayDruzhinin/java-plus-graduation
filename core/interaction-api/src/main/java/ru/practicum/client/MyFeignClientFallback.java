package ru.practicum.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.category.AdminCategoryClient;
import ru.practicum.client.category.PublicCategoryClient;
import ru.practicum.client.comment.CommentAdminClient;
import ru.practicum.client.comment.CommentPrivateClient;
import ru.practicum.client.comment.CommentPublicClient;
import ru.practicum.client.compilation.AdminCompilationClient;
import ru.practicum.client.compilation.PublicCompilationClient;
import ru.practicum.client.event.EventClient;
import ru.practicum.client.request.RequestClient;
import ru.practicum.client.user.UserAdminClient;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.DeleteCommentsDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdatedEventDto;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;

@Component
public class MyFeignClientFallback implements UserAdminClient, RequestClient, EventClient,
        PublicCompilationClient, AdminCompilationClient,
        AdminCategoryClient, PublicCategoryClient,
        CommentAdminClient, CommentPublicClient,
        CommentPrivateClient {
    // ===== UserAdminClient =====
    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        throw new RuntimeException("Сервис user-service временно недоступен (createUser)");
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        throw new RuntimeException("Сервис user-service временно недоступен (getUsers)");
    }

    @Override
    public void deleteUserById(Long userId) {
        throw new RuntimeException("Сервис user-service временно недоступен (deleteUserById)");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(Long id) {
        throw new RuntimeException("Сервис user-service временно недоступен (getUserById)");
    }

    @GetMapping("/short")
    @ResponseStatus(HttpStatus.OK)
    public UserShortDto getUserShortById(@RequestParam Long id) {
        throw new RuntimeException("Сервис user-service временно недоступен (getUserShortById)");
    }



    // === RequestClient ===
    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        throw new RuntimeException("Сервис request-service временно недоступен (getUserRequests)");
    }

    @Override
    public ParticipationRequestDto addUserRequest(Long userId, Long eventId) {
        throw new RuntimeException("Сервис request-service временно недоступен (addUserRequest)");
    }

    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        throw new RuntimeException("Сервис request-service временно недоступен (cancelUserRequest)");
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        throw new RuntimeException("Сервис request-service временно недоступен (changeRequestStatus)");
    }

    @Override
    public List<ParticipationRequestDto> getRequestByInitiator(Long userId, Long eventId) {
        throw new RuntimeException("Сервис request-service временно недоступен (getRequestByInitiator)");
    }



    // === EventClient ===
    @Override
    public EventFullDto saveEvent(NewEventDto newEventDto, Long userId) {
        throw new RuntimeException("Сервис event-service временно недоступен (saveEvent)");
    }

    @Override
    public EventFullDto saveFullEvent(EventFullDto event) {
        throw new RuntimeException("Сервис event-service временно недоступен (saveFullEvent)");
    }

    @Override
    public EventFullDto updateEventByIdAndUserId(UpdatedEventDto updatedEventDto, Long userId, Long eventId) {
        throw new RuntimeException("Сервис event-service временно недоступен (updateEventByIdAndUserId)");
    }

    @Override
    public EventFullDto updateAdminEventByIdAndUserId(UpdatedEventDto updatedEventDto, Long eventId) {
        throw new RuntimeException("Сервис event-service временно недоступен (updateAdminEventByIdAndUserId)");
    }

    @Override
    public EventFullDto getEventById(Long eventId, Long userId) {
        throw new RuntimeException("Сервис event-service временно недоступен (getEventById)");
    }

    @Override
    public List<EventShortDto> getRecommendations(Long userId) {
        throw new RuntimeException("Сервис event-service временно недоступен (getRecommendations)");
    }

    @Override
    public List<EventFullDto> getEvents(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                                        String rangeStart, String rangeEnd, String sort, Integer from, Integer size) {
        throw new RuntimeException("Сервис event-service временно недоступен (getEvents)");
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        throw new RuntimeException("Сервис event-service временно недоступен (getEventsByUserId)");
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        throw new RuntimeException("Сервис event-service временно недоступен (getEventByUserIdAndEventId)");
    }

    @Override
    public List<EventFullDto> getAdminEvents(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                                             String rangeStart, String rangeEnd, String sort, Integer from, Integer size) {
        throw new RuntimeException("Сервис event-service временно недоступен (getAdminEvents)");
    }



    // === CompilationClient ===
    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        throw new RuntimeException("Сервис event-service временно недоступен (getAll)");
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        throw new RuntimeException("Сервис event-service временно недоступен (getById)");
    }



    // === AdminCompilationClient ===
    @Override
    public CompilationDto create(NewCompilationDto dto) {
        throw new RuntimeException("Сервис event-service временно недоступен (create)");
    }

    @Override
    public void deleteCompilationById(Long compId) {
        throw new RuntimeException("Сервис event-compilation временно недоступен (delete)");
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        throw new RuntimeException("Сервис event-service временно недоступен (update)");
    }



    // === AdminCategoryClient ===
    @Override
    public CategoryDto create(NewCategoryDto dto) {
        throw new RuntimeException("Сервис event-service временно недоступен (create)");
    }

    @Override
    public CategoryDto update(Long catId, NewCategoryDto dto) {
        throw new RuntimeException("Сервис event-service временно недоступен (update)");
    }

    @Override
    public void delete(Long id) {
        throw new RuntimeException("Сервис event-service временно недоступен (delete)");
    }



    // === PublicCategoryClient ===
    @Override
    public List<CategoryDto> getAll(int from, int size) {
        throw new RuntimeException("Сервис event-service временно недоступен (getAll)");
    }

    @Override
    public CategoryDto getById(Long catId) {
        throw new RuntimeException("Сервис event-service временно недоступен (getById)");
    }



    // === CommentAdminClient ===
    @Override
    public void deleteCommentsByAdmin(DeleteCommentsDto deleteCommentsDto) {
        throw new RuntimeException("Сервис comment-service временно недоступен (deleteCommentsByAdmin)");
    }



    // === CommentPrivateClient ===
    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        throw new RuntimeException("Сервис comment-service временно недоступен (createComment)");
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        throw new RuntimeException("Сервис comment-service временно недоступен (updateComment)");
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        throw new RuntimeException("Сервис comment-service временно недоступен (deleteComment)");
    }

    @Override
    public List<CommentDto> getCommentsByUserId(Long userId) {
        throw new RuntimeException("Сервис comment-service временно недоступен (getCommentsByUserId)");
    }



    // === CommentPublicClient ===
    @Override
    public List<CommentDto> getComments(String content, Long userId, Long eventId,
                                        String rangeStart, String rangeEnd, Integer from, Integer size) {
        throw new RuntimeException("Сервис comment-service временно недоступен (getComments)");
    }

    @Override
    public CommentDto getCommentByID(Long commentId) {
        throw new RuntimeException("Сервис comment-service временно недоступен (getCommentByID)");
    }
}