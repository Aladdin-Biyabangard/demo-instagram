package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.model.dto.request.CommentDto;
import az.aladdin.blogplatform.model.dto.response.CommentResponseDto;

import java.util.List;

public interface CommentService {

    CommentResponseDto createComment(String userId, String postId, CommentDto commentDto);

    CommentResponseDto replyComment(String userId, String commentId, CommentDto commentDto);

    CommentResponseDto updateComment(String userId, String commentId, String content);

    List<CommentResponseDto> getPostComments(String postId);

    List<CommentResponseDto> getAllReplyComments(String commentId);

    Long likeComment(String userId, String commentId);

    void deleteComment(String userId, String commentId);

}
