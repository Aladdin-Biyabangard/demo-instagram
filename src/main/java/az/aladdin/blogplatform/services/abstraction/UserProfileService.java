package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.dao.entities.UserProfile;
import az.aladdin.blogplatform.model.dto.response.UserProfileResponseDto;
import az.aladdin.blogplatform.model.enums.user.Gender;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserProfileService {

    void updateUserProfile(MultipartFile multipartFile, String bio, Gender gender) throws IOException;

    void uploadUserProfilePhoto(UserProfile userProfile, MultipartFile multipartFile) throws IOException;

    void rewriteBio(UserProfile userProfile, String bio);

    void gender(UserProfile userProfile, Gender gender);

    UserProfileResponseDto getUserProfile();

    void deleteUserPhoto();

    UserProfile findUserProfileByUserId(String userId);
}
