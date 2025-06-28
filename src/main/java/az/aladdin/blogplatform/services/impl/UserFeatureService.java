package az.aladdin.blogplatform.services.impl;

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
import az.aladdin.blogplatform.model.enums.Complain;
import az.aladdin.blogplatform.services.abstraction.PostService;
import az.aladdin.blogplatform.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFeatureService {

    private final AuthHelper authHelper;
    private final PostService postServiceImpl;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ComplainInfoRepository complainInfoRepository;
    private final UserPostIgnoreRepository userPostIgnoreRepository;

    public void savePostToSaved(String postId) {
        User user = authHelper.getAuthenticatedUser();
        Post post = postServiceImpl.findPostById(postId);

        user.getSavedPost().add(post);

        userRepository.save(user);
    }

    public void ignorePost(String postId) {

        User user = authHelper.getAuthenticatedUser();
        Post post = postServiceImpl.findPostById(postId);

        UserPostIgnore ignore = new UserPostIgnore();
        ignore.setUser(user);
        ignore.setPost(post);

        userPostIgnoreRepository.save(ignore);
    }

    public UserAboutDto aboutTheAccount(String postId) {
        User user = postRepository.findUserByPostId(postId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        return new UserAboutDto(user.getUsername(), createdAt(user.getCreatedAt()));
    }

    public void complainPostById(String postId, Complain complain) {
        User user = authHelper.getAuthenticatedUser();
        Post post = postServiceImpl.findPostById(postId);
        ComplainInfo complainInfo = new ComplainInfo();
        complainInfo.setUser(user);
        complainInfo.setPost(post);
        complainInfo.setComplain(complain);
        complainInfoRepository.save(complainInfo);
    }


    public String createdAt(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH);
        return localDateTime.format(formatter);
    }


}
