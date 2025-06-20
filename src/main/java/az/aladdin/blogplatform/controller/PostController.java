package az.aladdin.blogplatform.controller;

import az.aladdin.blogplatform.model.dto.request.PostDto;
import az.aladdin.blogplatform.model.dto.response.PostResponseDto;
import az.aladdin.blogplatform.model.enums.CategoryType;
import az.aladdin.blogplatform.services.abstraction.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postServiceImpl;

    @PostMapping(path = "{userId}")
    public ResponseEntity<PostResponseDto> createPost(@PathVariable String userId, @RequestBody PostDto postDto) {
        return new ResponseEntity<>(postServiceImpl.createPost(userId, postDto), HttpStatus.CREATED);
    }

    @PutMapping(path = "{postId}/")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable String postId, @RequestBody PostDto postDto) {
        PostResponseDto postResponseDto = postServiceImpl.updatePost(postId, postDto);
        return ResponseEntity.ok(postResponseDto);
    }

    @GetMapping(path = "{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable String postId) {
        PostResponseDto post = postServiceImpl.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getPosts(@RequestParam CategoryType categoryType) {
        List<PostResponseDto> posts = postServiceImpl.getPosts(categoryType);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping(path = "{postId}/users/{userId}")
    public ResponseEntity<Void> deletePost(@PathVariable String userId, @PathVariable String postId) {
        postServiceImpl.deletePost(userId, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{postId}/users/{userId}/likes")
    public Long likePost(@PathVariable String postId, @PathVariable String userId) {
        return postServiceImpl.likePost(userId, postId);
    }


}
