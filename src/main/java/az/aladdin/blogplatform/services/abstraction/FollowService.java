package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.model.dto.response.UserFollowResponseDto;
import az.aladdin.blogplatform.model.dto.response.UserFollowStatsDto;

import java.util.List;

public interface FollowService {

    void toggleFollow(String userId, String userName);

    List<UserFollowResponseDto> getUserFollowers(String userId);

    List<UserFollowResponseDto> getUserFollowing(String userId);

    UserFollowStatsDto getUserFollowStats(String userId);
}
