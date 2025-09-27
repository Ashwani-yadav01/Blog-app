package com.Ashwani.blog.controllers;

import com.Ashwani.blog.domain.dtos.CreateTagsRequest;
import com.Ashwani.blog.domain.dtos.TagDto;
import com.Ashwani.blog.domain.entities.Tag;
import com.Ashwani.blog.mappers.TagMapper;
import com.Ashwani.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PostMapping
    ResponseEntity<List<TagDto>> createTags(@RequestBody CreateTagsRequest createTagsRequest) {

        List<Tag> savedTags = tagService.createTags(createTagsRequest.getNames());
        List<TagDto> createdTagResponses = savedTags.stream().map(tagMapper::toTagResponse).toList();
        return new ResponseEntity<>(createdTagResponses, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
