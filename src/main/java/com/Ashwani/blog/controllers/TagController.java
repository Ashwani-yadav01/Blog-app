package com.Ashwani.blog.controllers;

import com.Ashwani.blog.domain.dtos.TagDto;
import com.Ashwani.blog.domain.entities.Tag;
import com.Ashwani.blog.mappers.TagMapper;
import com.Ashwani.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/tags")
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    ResponseEntity<List<TagDto>> getAllTags() {
        List<Tag> tags = tagService.getTags();
        List<TagDto> tagResponse = tags.stream().map(tagMapper::toTagResponse).toList();

        return ResponseEntity.ok(tagResponse);

    }
}
