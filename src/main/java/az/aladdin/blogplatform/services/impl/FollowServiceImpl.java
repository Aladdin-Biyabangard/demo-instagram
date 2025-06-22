package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.UserMapper;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.repository.UserRepository;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.model.dto.response.UserFollowResponseDto;
import az.aladdin.blogplatform.model.dto.response.UserFollowStatsDto;
import az.aladdin.blogplatform.services.abstraction.FollowService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final UserServiceImpl userServiceImpl;
    private final UserMapper userMapper;

    @Transactional
    public void toggleFollow(String userId, String userName) {
        User userToFollow = userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        User user = userServiceImpl.findUserById(userId);

        if (user.getFollowing().contains(userToFollow)) {
            user.getFollowing().remove(userToFollow);
            userToFollow.getFollowers().remove(user);
        } else {
            user.getFollowing().add(userToFollow);
            userToFollow.getFollowers().add(user);
        }
        userRepository.save(user);
    }

    public List<UserFollowResponseDto> getUserFollowers(String userId) {
        User user = userServiceImpl.findUserById(userId);
        return userMapper.mapToFollowResponseDto(List.copyOf(user.getFollowers()));
    }

    public List<UserFollowResponseDto> getUserFollowing(String userId) {
        User user = userServiceImpl.findUserById(userId);
        return userMapper.mapToFollowResponseDto(List.copyOf(user.getFollowing()));
    }

    public UserFollowStatsDto getUserFollowStats(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found!");
        }
        long followers = userRepository.countFollowersByUserId(userId);
        long following = userRepository.countFollowingByUserId(userId);
        return new UserFollowStatsDto(userId, followers, following);
    }


}
