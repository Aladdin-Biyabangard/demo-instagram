package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.PostMapper;
import az.aladdin.blogplatform.dao.entities.Category;
import az.aladdin.blogplatform.dao.entities.Post;
import az.aladdin.blogplatform.dao.entities.PostLike;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.repository.CategoryRepository;
import az.aladdin.blogplatform.dao.repository.PostLikeRepository;
import az.aladdin.blogplatform.dao.repository.PostRepository;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.model.dto.request.PostDto;
import az.aladdin.blogplatform.model.dto.response.PostResponseDto;
import az.aladdin.blogplatform.model.enums.CategoryType;
import az.aladdin.blogplatform.services.abstraction.PostService;
import az.aladdin.blogplatform.utility.AuthHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final AuthHelper authHelper;

    @Override
    @Transactional
    public PostResponseDto createPost(PostDto postDto) {
        log.info("Creating post for category: {}", postDto.getCategory());
        User user = authHelper.getAuthenticatedUser();
        Post post = modelMapper.map(postDto, Post.class);
        Category category = findAndCreateCategoryByCategoryType(postDto.getCategory());

        category.getPosts().add(post);
        post.setUser(user);
        post.setCategory(category);
        user.getPosts().add(post);

        Post savedPost = postRepository.save(post);
        log.info("Post created successfully with ID: {}", savedPost.getId());
        return postMapper.toResponse(savedPost);
    }

    @Override
    @CachePut(cacheNames = "posts", key = "#postId")
    public PostResponseDto updatePost(String postId, PostDto postDto) {
        log.info("Updating post with ID: {}", postId);
        Post findPost = findPostById(postId);

        findPost.setTitle(postDto.getTitle());
        findPost.setContent(postDto.getContent());
        findPost.setPostStatus(postDto.getPostStatus());
        findPost.setCategory(findAndCreateCategoryByCategoryType(postDto.getCategory()));

        Post updatedPost = postRepository.save(findPost);
        log.info("Post updated successfully with ID: {}", updatedPost.getId());
        return postMapper.toResponse(updatedPost);
    }

    @Override
    @Cacheable(cacheNames = "posts", key = "#categoryType")
    public List<PostResponseDto> getPosts(CategoryType categoryType) {
        log.info("Fetching posts for category: {}", categoryType);
        List<Post> posts = postRepository.findPostsByCategory_CategoryType(categoryType);
        log.info("Found {} post(s) for category: {}", posts.size(), categoryType);
        return postMapper.toResponse(posts);
    }

    @CacheEvict(cacheNames = "postCache", key = "#postId")
    public void deletePost(String postId) {
        log.info("Deleting post with ID: {}", postId);
        User user = authHelper.getAuthenticatedUser();
        checkUsersPost(postId, user);
        postRepository.deleteById(postId);
        log.info("Post deleted successfully with ID: {}", postId);
    }

    @Cacheable(cacheNames = "postCache", key = "#postId")
    public PostResponseDto getPostById(String postId) {
        log.info("Fetching post with ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post with ID {} not found", postId);
                    return new ResourceNotFoundException("Post not found!");
                });
        return postMapper.toResponse(post);
    }

    @Override
    @Cacheable(cacheNames = "posts", key = "#postId")
    public Long likePost(String postId) {
        log.info("Toggling like for post ID: {}", postId);
        User user = authHelper.getAuthenticatedUser();
        Post post = findPostById(postId);
        checkUserLike(user, post);
        postRepository.save(post);
        log.info("Post ID {} now has {} likes", postId, post.getLikeCount());
        return post.getLikeCount();
    }

    public void checkUserLike(User user, Post post) {
        Optional<PostLike> optionalLike = postLikeRepository.findByUserAndPost(user, post);

        if (optionalLike.isPresent()) {
            log.info("User {} already liked post {}, removing like", user.getId(), post.getId());
            post.setLikeCount(post.getLikeCount() - 1L);
            post.getPostLikes().remove(optionalLike.get());
            postLikeRepository.delete(optionalLike.get());
        } else {
            log.info("User {} is liking post {}", user.getId(), post.getId());
            post.setLikeCount(post.getLikeCount() + 1L);
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(postLike);
            post.getPostLikes().add(postLike);
        }
    }

    public Post findPostById(String postId) {
        log.debug("Looking for post ID: {}", postId);
        return postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post not found with ID: {}", postId);
            return new ResourceNotFoundException("Post not found!");
        });
    }

    public void checkUsersPost(String postId, User user) {
        log.debug("Checking if post ID {} belongs to user {}", postId, user.getId());
        if (!postRepository.existsPostByUserAndId(user, postId)) {
            log.warn("Post ID {} does not belong to user {}", postId, user.getId());
            throw new IllegalArgumentException("The post does not belong to the user.");
        }
    }

    public Category findAndCreateCategoryByCategoryType(CategoryType categoryType) {
        log.debug("Looking for category: {}", categoryType);
        return categoryRepository.findCategoriesByCategoryType(categoryType)
                .orElseGet(() -> {
                    log.info("Category {} not found, creating new", categoryType);
                    return categoryRepository.save(Category.builder()
                            .categoryType(categoryType)
                            .build());
                });
    }
}
