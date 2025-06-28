package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.CommentMapper;
import az.aladdin.blogplatform.dao.entities.Comment;
import az.aladdin.blogplatform.dao.entities.CommentLike;
import az.aladdin.blogplatform.dao.entities.Post;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.repository.CommentLikeRepository;
import az.aladdin.blogplatform.dao.repository.CommentRepository;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.model.dto.request.CommentDto;
import az.aladdin.blogplatform.model.dto.response.CommentResponseDto;
import az.aladdin.blogplatform.services.abstraction.CommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserServiceImpl userServiceImpl;
    private final PostServiceImpl postServiceImpl;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    public CommentResponseDto createComment(String userId, String postId, CommentDto commentDto) {
        log.info("Creating comment by user {} on post {}", userId, postId);
        User user = userServiceImpl.findUserById(userId);

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUser(user);

        Post post = postServiceImpl.findPostById(postId);
        comment.setPost(post);
        post.getComments().add(comment);
        post.setCommentCount(post.getCommentCount() + 1);

        commentRepository.save(comment);
        log.info("Comment created with id {} by user {}", comment.getId(), userId);
        return commentMapper.toResponse(comment);
    }

    public CommentResponseDto replyComment(String userId, String commentId, CommentDto commentDto) {
        log.info("User {} replying to comment {}", userId, commentId);
        User user = userServiceImpl.findUserById(userId);
        if (!commentRepository.existsById(commentId)) {
            log.error("Reply failed: Comment {} not found", commentId);
            throw new ResourceNotFoundException("Comment not found!");
        }

        Comment replyComment = Comment.builder()
                .content(commentDto.getContent())
                .user(user)
                .parentId(commentId)
                .commentLikes(0L)
                .build();

        Comment savedReply = commentRepository.save(replyComment);
        log.info("Reply comment created with id {} by user {}", savedReply.getId(), userId);
        return commentMapper.toResponse(savedReply);
    }

    @Override
    public CommentResponseDto updateComment(String userId, String commentId, String content) {
        log.info("Updating comment {} by user {}", commentId, userId);
        validateUsersComment(userId, commentId);
        var comment = findCommentById(commentId);
        comment.setContent(content);
        commentRepository.save(comment);
        log.info("Comment {} updated by user {}", commentId, userId);
        return commentMapper.toResponse(comment);
    }

    @Cacheable(cacheNames = "comments", key = "#postId")
    public List<CommentResponseDto> getPostComments(String postId) {
        log.info("Fetching comments for post {}", postId);
        Post post = postServiceImpl.findPostById(postId);
        return commentMapper.toResponse(post.getComments());
    }

    public List<CommentResponseDto> getAllReplyComments(String commentId) {
        log.info("Fetching replies for comment {}", commentId);
        List<Comment> comments = commentRepository.findCommentsByParentId(commentId);
        return commentMapper.toResponse(comments);
    }

    @Override
    @Transactional
    public void deleteComment(String userId, String commentId) {
        log.info("Deleting comment {} by user {}", commentId, userId);
        validateUsersComment(userId, commentId);
        Comment comment = findCommentById(commentId);
        if (comment.getParentId() == null) {
            comment.getPost().getComments().remove(comment);
            commentRepository.deleteCommentsByParentId(commentId);
        }
        commentRepository.delete(comment);
        log.info("Comment {} deleted by user {}", commentId, userId);
    }

    public Long likeComment(String userId, String commentId) {
        log.info("User {} toggling like on comment {}", userId, commentId);
        User user = userServiceImpl.findUserById(userId);
        Comment comment = findCommentById(commentId);
        checkUserLike(user, comment);
        commentRepository.save(comment);
        log.info("Comment {} now has {} likes", commentId, comment.getCommentLikes());
        return comment.getCommentLikes();
    }

    public void validateUsersComment(String userId, String commentId) {
        if (!commentRepository.existsCommentByUser_IdAndId(userId, commentId)) {
            log.error("Validation failed: Comment {} does not belong to user {}", commentId, userId);
            throw new IllegalArgumentException("This comment does not belong to the user: " + userId);
        }
    }

    public void checkUserLike(User user, Comment comment) {
        Optional<CommentLike> optionalLike = commentLikeRepository.findByUserAndComment(user, comment);

        if (optionalLike.isPresent()) {
            comment.setCommentLikes(comment.getCommentLikes() - 1L);
            commentLikeRepository.delete(optionalLike.get());
            log.info("User {} removed like from comment {}", user.getId(), comment.getId());
        } else {
            comment.setCommentLikes(comment.getCommentLikes() + 1L);
            CommentLike commentLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(commentLike);
            log.info("User {} liked comment {}", user.getId(), comment.getId());
        }
    }

    public Comment findCommentById(String commentId) {
        log.debug("Looking for comment with id {}", commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment not found with id {}", commentId);
                    return new ResourceNotFoundException("Comment not found!");
                });
    }
}
