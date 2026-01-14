package ru.practicum.application.comment.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.application.comment.api.CommonCommentInterface;

@FeignClient(name = "comment-service")
public interface CommentClient extends CommonCommentInterface {
}
