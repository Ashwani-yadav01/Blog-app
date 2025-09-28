package com.Ashwani.blog.controllers;

import com.Ashwani.blog.domain.dtos.PostDto;
import com.Ashwani.blog.domain.entities.Post;
import com.Ashwani.blog.domain.entities.User;
import com.Ashwani.blog.mappers.PostMapper;
import com.Ashwani.blog.services.PostService;
import com.Ashwani.blog.services.UserService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostDto>> getDrafts(@RequestAttribute UUID userId){
        User loggedInUser = userService.getUserById(userId);
        List<Post> draftPosts = postService.getDraftPosts(loggedInUser);
        List<PostDto> draftPostDtos = draftPosts.stream().map(postMapper::toDto).toList();
        return ResponseEntity.ok(draftPostDtos);

    }
}
