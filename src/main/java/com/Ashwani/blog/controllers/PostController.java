package com.Ashwani.blog.controllers;

import com.Ashwani.blog.domain.CreatePostRequest;
import com.Ashwani.blog.domain.UpdatePostRequest;
import com.Ashwani.blog.domain.dtos.CreatePostRequestDto;
import com.Ashwani.blog.domain.dtos.PostDto;
import com.Ashwani.blog.domain.dtos.UpdatePostRequestDto;
import com.Ashwani.blog.domain.entities.Post;
import com.Ashwani.blog.domain.entities.User;
import com.Ashwani.blog.mappers.PostMapper;
import com.Ashwani.blog.services.PostService;
import com.Ashwani.blog.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId) {

        List<Post> posts = postService.getAllPosts(categoryId, tagId);

        List<PostDto> postDtos = posts.stream().map(postMapper::toDto).toList();

        return ResponseEntity.ok(postDtos);
    }

    //Todo: implement authorization
    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostDto>> getDrafts(@RequestAttribute UUID userId) {
        User loggedInUser = userService.getUserById(userId);
        List<Post> draftPosts = postService.getDraftPosts(loggedInUser);
        List<PostDto> draftPostDtos = draftPosts.stream().map(postMapper::toDto).toList();
        return ResponseEntity.ok(draftPostDtos);
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
         @Valid   @RequestBody CreatePostRequestDto createPostRequestDto,
            @RequestAttribute UUID userId
    ) {
        User loggedInUser = userService.getUserById(userId);
        CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDto);
        Post createdPost = postService.createPost(loggedInUser, createPostRequest);
        PostDto createdPostDto = postMapper.toDto(createdPost);
        return new ResponseEntity<>(createdPostDto, HttpStatus.CREATED);
    }

    //todo: authorization implement krna hai
    @PutMapping(path = "/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequestDto updatePostRequestDto) {
        UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(updatePostRequestDto);
        Post updatedPost = postService.updatePost(id, updatePostRequest);
        PostDto updatedPostDto = postMapper.toDto(updatedPost);
        return ResponseEntity.ok(updatedPostDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostDto> getPost(
            @PathVariable UUID id
    ) {
        Post post = postService.getPost(id);
        PostDto postDto = postMapper.toDto(post);
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
