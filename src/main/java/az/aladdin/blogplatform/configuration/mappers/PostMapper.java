package az.aladdin.blogplatform.configuration.mappers;

import az.aladdin.blogplatform.dao.entities.Post;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.model.dto.response.PostResponseDto;
import az.aladdin.blogplatform.model.dto.response.PostResponseShortDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class PostMapper {


    public PostResponseDto toResponse(Post post) {
        return new PostResponseDto(post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getPostStatus(),
                post.getCommentCount(),
                post.getLikeCount(),
                readingTime(post.getReadingTime()),
                createdAt(post.getCreatedAt()),
                post.getUser().getId(),
                post.getCategory().getCategoryType()
        );
    }

    public List<PostResponseDto> toResponse(List<Post> posts) {
        return posts.stream().map(this::toResponse).toList();
    }

    public PostResponseShortDto toShortResponse(Post post) {
        return new PostResponseShortDto(
                post.getId(),
                userFullName(post.getUser()),
                post.getTitle(),
                post.getPostStatus(),
                readingTime(post.getReadingTime()),
                post.getCreatedAt()
        );
    }

    public List<PostResponseShortDto> toShortResponse(List<Post> posts) {
        return posts.stream().map(this::toShortResponse).toList();
    }

    public String readingTime(Integer readingTime) {
        return readingTime.toString() + " min read";
    }

    public String userFullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    public String createdAt(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH);
        return localDateTime.format(formatter);
    }
}
