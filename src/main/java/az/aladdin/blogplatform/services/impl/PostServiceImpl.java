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
import az.aladdin.blogplatform.services.abstraction.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ModelMapper modelMapper;
    private final UserService userServiceImpl;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    @Transactional
    public PostResponseDto createPost(String userId, PostDto postDto) {
        Post post = modelMapper.map(postDto, Post.class);
        User user = userServiceImpl.findUserById(userId);
        Category category = findAndCreateCategoryByCategoryType(postDto.getCategory());
        category.getPosts().add(post);
        post.setUser(user);
        post.setCategory(category);
        user.getPosts().add(post);
        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost);
    }

    @Override
    @CachePut(cacheNames = "posts", key = "#postId")
    public PostResponseDto updatePost(String postId, PostDto postDto) {
        Post findPost = findPostById(postId);
        findPost.setTitle(postDto.getTitle());
        findPost.setContent(postDto.getContent());
        findPost.setPostStatus(postDto.getPostStatus());
        findPost.setCategory(findAndCreateCategoryByCategoryType(postDto.getCategory()));
        postRepository.save(findPost);
        return postMapper.toResponse(findPost);
    }

    @Override
    @Cacheable(cacheNames = "posts", key = "#categoryType")
    public List<PostResponseDto> getPosts(CategoryType categoryType) {
        List<Post> posts = postRepository.findPostsByCategory_CategoryType(categoryType);
        return postMapper.toResponse(posts);
    }

    @CacheEvict(cacheNames = "postCache", key = "#postId")
    public void deletePost(String userId, String postId) {
        // TODO Security context holderden gelen user ile check edib sileciyik

        User user = userServiceImpl.findUserById(userId);
        checkUsersPost(postId, user);
        postRepository.deleteById(postId);
    }

    @Cacheable(cacheNames = "postCache", key = "#postId")
    public PostResponseDto getPostById(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found!"));
        return postMapper.toResponse(post);
    }

    @Override
    @Cacheable(cacheNames = "posts", key = "#postId")
    public Long likePost(String userId, String postId) {
        // Security context holderden gelen useri check edeciyikki bu posta like qoyub ya yox

        User user = userServiceImpl.findUserById(userId);
        Post post = findPostById(postId);

        checkUserLike(user, post);

        postRepository.save(post);
        return post.getLikeCount();
    }


    public void checkUserLike(User user, Post post) {
        Optional<PostLike> optionalLike = postLikeRepository.findByUserAndPost(user, post);

        if (optionalLike.isPresent()) {
            post.setLikeCount(post.getLikeCount() - 1L);
            post.getPostLikes().remove(optionalLike.get());
            postLikeRepository.delete(optionalLike.get());
        } else {
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
        return postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found!"));
    }

    public void checkUsersPost(String postId, User user) {
        if (!postRepository.existsPostByUserAndId(user, postId)) {
            throw new IllegalArgumentException("The post does not belong to the user.");
        }
    }

    public Category findAndCreateCategoryByCategoryType(CategoryType categoryType) {
        return categoryRepository.findCategoriesByCategoryType(categoryType).orElseGet(() ->
                categoryRepository.save(Category.builder()
                        .categoryType(categoryType)
                        .build())
        );
    }

}
