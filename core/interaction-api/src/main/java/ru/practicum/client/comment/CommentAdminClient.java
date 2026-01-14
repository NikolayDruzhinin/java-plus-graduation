package ru.practicum.client.comment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.client.MyFeignClientFallback;
import ru.practicum.dto.comment.DeleteCommentsDto;

@FeignClient(name = "comment-service", fallback = MyFeignClientFallback.class)
public interface CommentAdminClient {

    @DeleteMapping("/admin/comments")
    void deleteCommentsByAdmin(@RequestBody DeleteCommentsDto deleteCommentsDto);
}
