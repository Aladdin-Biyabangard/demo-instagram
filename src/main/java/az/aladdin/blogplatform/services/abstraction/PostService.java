package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.model.dto.request.PostDto;
import az.aladdin.blogplatform.model.dto.response.PostResponseDto;
import az.aladdin.blogplatform.model.enums.CategoryType;

import java.util.List;

public interface PostService {

    PostResponseDto createPost(String userId, PostDto postDto);

    PostResponseDto updatePost(String postId, PostDto postDto);

    List<PostResponseDto> getPosts(CategoryType categoryType);

    PostResponseDto getPostById(String postId);

    void deletePost(String userId, String postId);

    Long likePost(String userId, String postId);


}
