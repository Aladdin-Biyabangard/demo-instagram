package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.UserProfileMapper;
import az.aladdin.blogplatform.dao.entities.UserProfile;
import az.aladdin.blogplatform.dao.repository.UserProfileRepository;
import az.aladdin.blogplatform.exception.FileSizeExceededException;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.model.dto.response.FileUploadResponse;
import az.aladdin.blogplatform.model.dto.response.UserProfileResponseDto;
import az.aladdin.blogplatform.model.enums.Gender;
import az.aladdin.blogplatform.services.abstraction.FileLoadService;
import az.aladdin.blogplatform.services.abstraction.UserProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.cache.annotation.CacheEvict;
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

    @CacheEvict(cacheNames = "userProfile", key = "#userId")
    @Transactional
    public void updateUserProfile(String userId, MultipartFile multipartFile, String bio, Gender gender) throws IOException {
        UserProfile userProfile = findUserProfileByUserId(userId);

        if (multipartFile != null) {
            uploadUserProfilePhoto(userId, userProfile, multipartFile);
        }
        if (bio != null) {
            rewriteBio(userProfile, bio);
        }

        if (gender != null) {
            gender(userProfile, gender);
        }

        userProfileRepository.save(userProfile);
    }

    public void uploadUserProfilePhoto(String userId, UserProfile userProfile, MultipartFile multipartFile) throws IOException {
        String contentType = multipartFile.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            throw new InvalidContentTypeException("Incorrect image format");
        }

        long size = multipartFile.getSize();
        if (size > 10485760) /* 10 mb */ {
            throw new FileSizeExceededException("The file size can be a maximum of 10 MB.");
        }
        FileUploadResponse fileUploadResponse = fileLoadServiceImpl.uploadFile(multipartFile, userId);
        userProfile.setProfilePhotoKey(fileUploadResponse.getKey());
        userProfile.setProfilePhotoUrl(fileUploadResponse.getUrl());
    }

    public void rewriteBio(UserProfile userProfile, String bio) {
        userProfile.setBio(bio);
        userProfileRepository.save(userProfile);
    }

    public void gender(UserProfile userProfile, Gender gender) {
        userProfile.setGender(gender);
        userProfileRepository.save(userProfile);
    }

    public void deleteUserPhoto(String userId) {
        UserProfile userProfile = findUserProfileByUserId(userId);
        fileLoadServiceImpl.deleteFileFromAws(userProfile.getProfilePhotoKey());
        userProfile.setProfilePhotoUrl("");
        userProfile.setProfilePhotoKey("");
        userProfileRepository.save(userProfile);
    }

    public UserProfileResponseDto getUserProfile(String userId) {
        UserProfile userProfile = findUserProfileByUserId(userId);
        return userProfileMapper.response(userProfile);
    }

    public UserProfile findUserProfileByUserId(String userId) {
        return userProfileRepository
                .findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User profile not found!"));

    }
}
