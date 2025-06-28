package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.UserProfileMapper;
import az.aladdin.blogplatform.dao.entities.UserProfile;
import az.aladdin.blogplatform.dao.repository.UserProfileRepository;
import az.aladdin.blogplatform.exception.FileSizeExceededException;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.model.dto.response.FileUploadResponse;
import az.aladdin.blogplatform.model.dto.response.UserProfileResponseDto;
import az.aladdin.blogplatform.model.enums.user.Gender;
import az.aladdin.blogplatform.services.abstraction.FileLoadService;
import az.aladdin.blogplatform.services.abstraction.UserProfileService;
import az.aladdin.blogplatform.utility.AuthHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final FileLoadService fileLoadServiceImpl;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final AuthHelper authHelper;

    @Override
    @Transactional
    public void updateUserProfile(MultipartFile multipartFile, String bio, Gender gender) throws IOException {
        String userId = authHelper.getAuthenticatedUser().getId();
        log.info("Updating user profile for user ID: {}", userId);
        UserProfile userProfile = findUserProfileByUserId(userId);

        if (multipartFile != null) {
            log.info("Uploading profile photo for user ID: {}", userId);
            uploadUserProfilePhoto(userProfile, multipartFile);
        }

        if (bio != null) {
            log.info("Updating bio for user ID: {}", userId);
            rewriteBio(userProfile, bio);
        }

        if (gender != null) {
            log.info("Updating gender for user ID: {}", userId);
            gender(userProfile, gender);
        }

        userProfileRepository.save(userProfile);
        log.info("User profile successfully updated for user ID: {}", userId);
    }

    public void uploadUserProfilePhoto(UserProfile userProfile, MultipartFile multipartFile) throws IOException {
        String userId = authHelper.getAuthenticatedUser().getId();
        String contentType = multipartFile.getContentType();
        long size = multipartFile.getSize();

        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            log.warn("Invalid image format for user ID: {}. Provided: {}", userId, contentType);
            throw new InvalidContentTypeException("Incorrect image format");
        }

        if (size > 10 * 1024 * 1024) {
            log.warn("Image too large ({} bytes) for user ID: {}", size, userId);
            throw new FileSizeExceededException("The file size can be a maximum of 10 MB.");
        }

        FileUploadResponse fileUploadResponse = fileLoadServiceImpl.uploadFile(multipartFile, userId);
        userProfile.setProfilePhotoKey(fileUploadResponse.getKey());
        userProfile.setProfilePhotoUrl(fileUploadResponse.getUrl());
        log.info("Profile photo uploaded successfully for user ID: {}", userId);
    }

    public void rewriteBio(UserProfile userProfile, String bio) {
        userProfile.setBio(bio);
        userProfileRepository.save(userProfile);
        log.info("Bio updated for user ID: {}", userProfile.getUser().getId());
    }

    public void gender(UserProfile userProfile, Gender gender) {
        userProfile.setGender(gender);
        userProfileRepository.save(userProfile);
        log.info("Gender updated for user ID: {}", userProfile.getUser().getId());
    }

    public void deleteUserPhoto() {
        String userId = authHelper.getAuthenticatedUser().getId();
        log.info("Deleting profile photo for user ID: {}", userId);
        UserProfile userProfile = findUserProfileByUserId(userId);

        fileLoadServiceImpl.deleteFileFromAws(userProfile.getProfilePhotoKey());

        userProfile.setProfilePhotoUrl("");
        userProfile.setProfilePhotoKey("");
        userProfileRepository.save(userProfile);
        log.info("Profile photo deleted successfully for user ID: {}", userId);
    }

    @Override
    public UserProfileResponseDto getUserProfile() {
        String userId = authHelper.getAuthenticatedUser().getId();
        log.info("Fetching profile info for user ID: {}", userId);
        UserProfile userProfile = findUserProfileByUserId(userId);
        return userProfileMapper.response(userProfile);
    }

    public UserProfile findUserProfileByUserId(String userId) {
        log.debug("Looking for user profile by user ID: {}", userId);
        return userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("User profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("User profile not found!");
                });
    }
}
