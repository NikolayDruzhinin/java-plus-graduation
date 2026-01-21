package ru.practicum.client.comment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.MyFeignClientFallback;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;

import java.util.List;

@FeignClient(name = "comment-service", fallback = MyFeignClientFallback.class)
public interface CommentPrivateClient {

    @PostMapping("/users/{userId}/comment")
    CommentDto createComment(@PathVariable Long userId,
                             @RequestParam Long eventId,
                             @RequestBody NewCommentDto newCommentDto);

    @PatchMapping("/users/{userId}/comment/{commentId}")
    CommentDto updateComment(@PathVariable Long userId,
                             @PathVariable Long commentId,
                             @RequestBody UpdateCommentDto updateCommentDto);

    @DeleteMapping("/users/{userId}/comment/{commentId}")
    void deleteComment(@PathVariable Long userId,
                       @PathVariable Long commentId);

    @GetMapping("/users/{userId}/comment")
    List<CommentDto> getCommentsByUserId(@PathVariable Long userId);
}
