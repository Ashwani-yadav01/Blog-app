package com.Ashwani.blog.services.impl;

import com.Ashwani.blog.domain.entities.Tag;
import com.Ashwani.blog.repositories.TagRepository;
import com.Ashwani.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAllWithPostCount();
    }
}
