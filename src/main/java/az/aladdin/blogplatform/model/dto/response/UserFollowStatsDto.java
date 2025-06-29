package az.aladdin.blogplatform.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserFollowStatsDto {
    private String userId;
    private long followersCount;
    private long followingCount;

}
