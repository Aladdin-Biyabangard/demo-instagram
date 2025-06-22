package az.aladdin.blogplatform.controller;

import az.aladdin.blogplatform.model.dto.response.UserProfileResponseDto;
import az.aladdin.blogplatform.model.enums.Gender;
import az.aladdin.blogplatform.services.abstraction.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/profiles")
@RequiredArgsConstructor
public class UserProfileControllerImpl {

    private final UserProfileService userProfileService;

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateUserProfile(@PathVariable String userId,
                                  @RequestPart(value = "file", required = false) MultipartFile file,
                                  @RequestPart(required = false) String bio,
                                  @RequestPart(required = false) Gender gender) throws IOException {
        userProfileService.updateUserProfile(userId, file, bio, gender);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "users/{userId}")
    public UserProfileResponseDto getUserProfile(@PathVariable String userId) {
        return userProfileService.getUserProfile(userId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "users/{userId}")
    public void deleteUserPhoto(@PathVariable String userId) {
        userProfileService.deleteUserPhoto(userId);
    }

}
