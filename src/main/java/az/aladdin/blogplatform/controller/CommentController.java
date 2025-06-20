package az.aladdin.blogplatform.controller;

import az.aladdin.blogplatform.model.dto.request.CommentDto;
import az.aladdin.blogplatform.model.dto.response.CommentResponseDto;
import az.aladdin.blogplatform.services.abstraction.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentServiceImpl;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "users/{userId}/posts/{postId}/comments")
    public CommentResponseDto createComment(@PathVariable String userId,
                                            @PathVariable String postId,
                                            @RequestBody CommentDto commentDto) {
        return commentServiceImpl.createComment(userId, postId, commentDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "users/{userId}/comments/{commentId}/reply")
    public CommentResponseDto replyComment(@PathVariable String userId,
                                           @PathVariable String commentId,
                                           @RequestBody CommentDto commentDto) {
        return commentServiceImpl.replyComment(userId, commentId, commentDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping(path = "users/{userId}/comments/{commentId}")
    public CommentResponseDto updateComment(@PathVariable String userId,
                                            @PathVariable String commentId,
                                            @RequestBody String content) {
        return commentServiceImpl.updateComment(userId, commentId, content);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "comments/posts/{postId}")
    public List<CommentResponseDto> getPostsComments(@PathVariable String postId) {
        return commentServiceImpl.getPostComments(postId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "comments/{commentId}/reply")
    public List<CommentResponseDto> getAllReplyComments(@PathVariable String commentId) {
        return commentServiceImpl.getAllReplyComments(commentId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "users/{userId}/comments/{commentId}/likes")
    public Long likeComment(@PathVariable String userId,
                            @PathVariable String commentId) {
        return commentServiceImpl.likeComment(userId, commentId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "users/{userId}/comments/{commentId}")
    public void deleteComment(@PathVariable String userId,
                              @PathVariable String commentId) {
        commentServiceImpl.deleteComment(userId, commentId);
    }


}
