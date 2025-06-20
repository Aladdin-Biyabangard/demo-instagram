package az.aladdin.blogplatform.model.dto.response;

import az.aladdin.blogplatform.model.enums.CategoryType;
import az.aladdin.blogplatform.model.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostResponseDto {

    private String id;
    private String title;
    private String content;
    private PostStatus postStatus;
    private Long commentCount;
    private Long likeCount;
    private String readingTime;
    private String updatedAt;
    private String userId;
    private CategoryType category;

}
