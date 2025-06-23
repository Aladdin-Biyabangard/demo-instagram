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
        existsUserByEmail(userDto.getEmail());
        User user = modelMapper.map(userDto, User.class);
        User savedUser = userRepository.save(user);


        userProfileRepository.save(UserProfile.builder()
                .gender(Gender.I_PREFER_NOT_TO_SPECIFY)
                .user(user)
                .build());
        return userMapper.toResponse(savedUser);
    }

    public void blockUser(String userId, String userName) {
        User user = findUserById(userId);

    }

    public void existsUserByEmail(String email) {
        if (userRepository.existsUserByEmail(email)) {
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
        User user = findUserById(userId);

        removeUserFromFollowersAndFollowing(user);

        user.getFollowers().clear();
        user.getFollowing().clear();

        userRepository.deleteById(userId);
    }

    public void removeUserFromFollowersAndFollowing(User user) {
        for (User follower : user.getFollowers()) {
            follower.getFollowing().remove(user);
        }

        for (User following : user.getFollowing()) {
            following.getFollowers().remove(user);
        }
    }
}
