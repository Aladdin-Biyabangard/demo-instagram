package az.aladdin.blogplatform.configuration.mappers;

import az.aladdin.blogplatform.dao.entities.Comment;
import az.aladdin.blogplatform.model.dto.response.CommentResponseDto;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class CommentMapper {

    public CommentResponseDto toResponse(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getCommentLikes(),
                createdAt(comment.getCreatedAt())
        );
    }

    public List<CommentResponseDto> toResponse(List<Comment> comments) {
        return comments.stream().map(this::toResponse).toList();
    }

    public String createdAt(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime, now);
        long days = duration.toDays();
        if (days >= 1 && days <= 31) {
            return days + " days.";
        } else if (days > 31) {
            return days / 31 + " month.";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
        return localDateTime.format(formatter);
    }
}
