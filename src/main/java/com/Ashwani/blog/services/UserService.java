package com.Ashwani.blog.services;

import com.Ashwani.blog.domain.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
}
