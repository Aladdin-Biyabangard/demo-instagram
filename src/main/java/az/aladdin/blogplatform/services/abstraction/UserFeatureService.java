package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.model.dto.response.UserAboutDto;
import az.aladdin.blogplatform.model.dto.response.UserFollowResponseDto;
import az.aladdin.blogplatform.model.enums.Complain;

import java.time.LocalDateTime;
import java.util.List;

public interface UserFeatureService {

    void savePostToSaved(String postId);

    void ignorePost(String postId);

    UserAboutDto aboutTheAccount(String postId);

    void complainPostById(String postId, Complain complain);

    List<UserFollowResponseDto> recommendedUser();

    String createdAt(LocalDateTime localDateTime);
}
