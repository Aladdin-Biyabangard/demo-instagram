package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.UserMapper;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.entities.UserProfile;
import az.aladdin.blogplatform.dao.repository.UserProfileRepository;
import az.aladdin.blogplatform.dao.repository.UserRepository;
import az.aladdin.blogplatform.exception.ResourceAlreadyExistException;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.model.dto.request.UserDto;
import az.aladdin.blogplatform.model.dto.response.UserResponseDto;
import az.aladdin.blogplatform.model.enums.user.Gender;
import az.aladdin.blogplatform.services.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final UserProfileRepository userProfileRepository;

    @Override
    public UserResponseDto createUser(UserDto userDto) {
        log.info("Attempting to create user with email: {}", userDto.getEmail());
        existsUserByEmail(userDto.getEmail());

        User user = modelMapper.map(userDto, User.class);
        User savedUser = userRepository.save(user);

        userProfileRepository.save(UserProfile.builder()
                .gender(Gender.I_PREFER_NOT_TO_SPECIFY)
                .user(user)
                .build());

        log.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    public void existsUserByEmail(String email) {
        log.debug("Checking existence of user with email: {}", email);
        if (userRepository.existsUserByEmail(email)) {
            log.warn("User already exists with email: {}", email);
            throw new ResourceAlreadyExistException("User already exists!");
        }
    }

    public User findUserById(String id) {
        log.info("Finding user with ID: {}", id);
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with ID: {}", id);
            return new ResourceNotFoundException("User not found");
        });
    }

    public void deleteUser(String userId) {
        log.info("Deleting user with ID: {}", userId);
        User user = findUserById(userId);

        log.debug("Removing user {} from followers and following lists", user.getId());
        removeUserFromFollowersAndFollowing(user);

        user.getFollowers().clear();
        user.getFollowing().clear();

        userRepository.deleteById(userId);
        log.info("User with ID: {} deleted successfully", userId);
    }

    public void removeUserFromFollowersAndFollowing(User user) {
        log.debug("Cleaning up followers and following for user ID: {}", user.getId());

        for (User follower : user.getFollowers()) {
            follower.getFollowing().remove(user);
            log.trace("Removed user {} from follower {}'s following list", user.getId(), follower.getId());
        }

        for (User following : user.getFollowing()) {
            following.getFollowers().remove(user);
            log.trace("Removed user {} from following {}'s follower list", user.getId(), following.getId());
        }

        log.debug("Cleanup completed for user ID: {}", user.getId());
    }
}
