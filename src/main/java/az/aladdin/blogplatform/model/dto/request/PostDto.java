package az.aladdin.blogplatform.model.dto.request;

import az.aladdin.blogplatform.model.enums.CategoryType;
import az.aladdin.blogplatform.model.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDto {

    private String title;
    private String content;
    private PostStatus postStatus;
    private CategoryType category;
}
