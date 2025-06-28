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
        log.info("User {} tries to toggle follow status for user '{}'", userId, userName);
        User userToFollow = userRepository.findUserByUserName(userName)
                .orElseThrow(() -> {
                    log.error("User to follow '{}' not found", userName);
                    return new ResourceNotFoundException("User not found!");
                });

        User user = userServiceImpl.findUserById(userId);

        if (user.getFollowing().contains(userToFollow)) {
            user.getFollowing().remove(userToFollow);
            userToFollow.getFollowers().remove(user);
            log.info("User {} unfollowed user '{}'", userId, userName);
        } else {
            user.getFollowing().add(userToFollow);
            userToFollow.getFollowers().add(user);
            log.info("User {} followed user '{}'", userId, userName);
        }
        userRepository.save(user);
    }

    public List<UserFollowResponseDto> getUserFollowers(String userId) {
        log.info("Fetching followers for user ID: {}", userId);
        User user = userServiceImpl.findUserById(userId);
        List<UserFollowResponseDto> followersDto = userMapper.mapToFollowResponseDto(List.copyOf(user.getFollowers()));
        log.info("Found {} followers for user ID: {}", followersDto.size(), userId);
        return followersDto;
    }

    public List<UserFollowResponseDto> getUserFollowing(String userId) {
        log.info("Fetching following list for user ID: {}", userId);
        User user = userServiceImpl.findUserById(userId);
        List<UserFollowResponseDto> followingDto = userMapper.mapToFollowResponseDto(List.copyOf(user.getFollowing()));
        log.info("User ID: {} is following {} users", userId, followingDto.size());
        return followingDto;
    }

    public UserFollowStatsDto getUserFollowStats(String userId) {
        log.info("Calculating follow stats for user ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("User not found for ID: {}", userId);
            throw new ResourceNotFoundException("User not found!");
        }
        long followers = userRepository.countFollowersByUserId(userId);
        long following = userRepository.countFollowingByUserId(userId);
        log.info("User ID: {} has {} followers and follows {} users", userId, followers, following);
        return new UserFollowStatsDto(userId, followers, following);
    }
}
