package az.aladdin.blogplatform.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentResponseDto {

    private String id;

    private String content;

    private Long likeCount;

    private String updatedAt;

}
