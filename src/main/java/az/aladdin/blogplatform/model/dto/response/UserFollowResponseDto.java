package az.aladdin.blogplatform.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserFollowResponseDto {
   private String userId;
   private String userName;
}
