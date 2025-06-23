package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.model.dto.response.UserFollowResponseDto;

import java.util.List;

public interface BlockService {

    void blockUser(String blockerId, String blockedId);

    void unBlock(String blockerId, String blockedId);

    List<UserFollowResponseDto> blockList(String blockerId);

}
