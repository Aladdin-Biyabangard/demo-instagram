package az.aladdin.blogplatform.configuration.mappers;

import az.aladdin.blogplatform.dao.entities.UserProfile;
import az.aladdin.blogplatform.model.dto.response.UserProfileResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public UserProfileResponseDto response(UserProfile userProfile) {
        return new UserProfileResponseDto(
                userProfile.getId(),
                userProfile.getBio(),
                userProfile.getGender(),
                userProfile.getProfilePhotoUrl(),
                userProfile.getProfilePhotoKey()
        );
    }
}
