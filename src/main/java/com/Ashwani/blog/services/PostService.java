package com.Ashwani.blog.services;

import com.Ashwani.blog.domain.CreatePostRequest;
import com.Ashwani.blog.domain.UpdatePostRequest;
import com.Ashwani.blog.domain.entities.Post;
import com.Ashwani.blog.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {

    List<Post> getAllPosts(UUID categoryId, UUID tagId);
    List<Post> getDraftPosts(User user);

    Post createPost(User user, CreatePostRequest createPostRequest);

    Post updatePost(UUID id, UpdatePostRequest updatePostRequest);

    Post getPost(UUID id);

    void deletePost(UUID id);
}
