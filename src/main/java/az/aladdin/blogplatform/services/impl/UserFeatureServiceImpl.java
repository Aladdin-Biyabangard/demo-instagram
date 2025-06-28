package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.UserMapper;
import az.aladdin.blogplatform.dao.entities.ComplainInfo;
import az.aladdin.blogplatform.dao.entities.Post;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.entities.UserPostIgnore;
import az.aladdin.blogplatform.dao.repository.ComplainInfoRepository;
import az.aladdin.blogplatform.dao.repository.PostRepository;
import az.aladdin.blogplatform.dao.repository.UserPostIgnoreRepository;
import az.aladdin.blogplatform.dao.repository.UserRepository;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.model.dto.response.UserAboutDto;
import az.aladdin.blogplatform.model.dto.response.UserFollowResponseDto;
import az.aladdin.blogplatform.model.enums.Complain;
import az.aladdin.blogplatform.services.abstraction.PostService;
import az.aladdin.blogplatform.services.abstraction.UserFeatureService;
import az.aladdin.blogplatform.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFeatureServiceImpl implements UserFeatureService {

    private final AuthHelper authHelper;
    private final PostService postServiceImpl;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ComplainInfoRepository complainInfoRepository;
    private final UserPostIgnoreRepository userPostIgnoreRepository;
    private final UserMapper userMapper;

    public void savePostToSaved(String postId) {
        log.info("Saving post with ID: {} to saved posts", postId);
        User user = authHelper.getAuthenticatedUser();
        Post post = postServiceImpl.findPostById(postId);

        user.getSavedPost().add(post);
        userRepository.save(user);
        log.info("Post with ID: {} saved successfully by user: {}", postId, user.getId());
    }

    public void ignorePost(String postId) {
        log.info("User is ignoring post with ID: {}", postId);
        User user = authHelper.getAuthenticatedUser();
        Post post = postServiceImpl.findPostById(postId);

        UserPostIgnore ignore = new UserPostIgnore();
        ignore.setUser(user);
        ignore.setPost(post);

        userPostIgnoreRepository.save(ignore);
        log.info("User {} successfully ignored post {}", user.getId(), post.getId());
    }

    public UserAboutDto aboutTheAccount(String postId) {
        log.info("Fetching account info for post ID: {}", postId);
        User user = postRepository.findUserByPostId(postId)
                .orElseThrow(() -> {
                    log.warn("No user found for post ID: {}", postId);
                    return new ResourceNotFoundException("User not found!");
                });

        String formattedDate = createdAt(user.getCreatedAt());
        log.info("User info retrieved: {} created at {}", user.getUsername(), formattedDate);
        return new UserAboutDto(user.getUsername(), formattedDate);
    }

    public void complainPostById(String postId, Complain complain) {
        log.info("User is complaining about post ID: {} with reason: {}", postId, complain);
        User user = authHelper.getAuthenticatedUser();
        Post post = postServiceImpl.findPostById(postId);

        ComplainInfo complainInfo = new ComplainInfo();
        complainInfo.setUser(user);
        complainInfo.setPost(post);
        complainInfo.setComplain(complain);

        complainInfoRepository.save(complainInfo);
        log.info("Complain saved successfully by user {} for post {}", user.getId(), post.getId());
    }

    public List<UserFollowResponseDto> recommendedUser() {
        log.info("Generating recommended users for following");
        User mainUser = authHelper.getAuthenticatedUser();

        Set<User> following = mainUser.getFollowing();
        Set<User> recommended = new HashSet<>();

        for (User user : following) {
            List<User> candidates = user.getFollowing().stream().toList();
            int count = 0;
            for (User candidate : candidates) {
                if (!following.contains(candidate) && !candidate.equals(mainUser)) {
                    recommended.add(candidate);
                    count++;
                }
                if (count >= 5) break;
            }
        }

        log.info("Found {} recommended user(s) for user {}", recommended.size(), mainUser.getId());
        return userMapper.mapToFollowResponseDto(new ArrayList<>(recommended));
    }

    public String createdAt(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH);
        String formatted = localDateTime.format(formatter);
        log.debug("Formatted date: {}", formatted);
        return formatted;
    }
}
