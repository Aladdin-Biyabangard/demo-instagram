package az.aladdin.blogplatform.model.dto.response;

import az.aladdin.blogplatform.model.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@AllArgsConstructor
@Data
public class PostResponseShortDto {

    private String id;

    private String userFullName;

    private String postTitle;

    private PostStatus postStatus;

    private String readingTime;

    private LocalDateTime createdAt;

}
