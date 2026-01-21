package ru.practicum.client.comment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.MyFeignClientFallback;
import ru.practicum.dto.comment.CommentDto;

import java.util.List;

@FeignClient(name = "comment-service", fallback = MyFeignClientFallback.class)
public interface CommentPublicClient {

    @GetMapping("/comment")
    List<CommentDto> getComments(@RequestParam(required = false) String content,
                                 @RequestParam(required = false) Long userId,
                                 @RequestParam(required = false) Long eventId,
                                 @RequestParam(required = false) String rangeStart,
                                 @RequestParam(required = false) String rangeEnd,
                                 @RequestParam(required = false) Integer from,
                                 @RequestParam(required = false) Integer size);

    @GetMapping("/comment/{commentId}")
    CommentDto getCommentByID(@PathVariable Long commentId);
}
