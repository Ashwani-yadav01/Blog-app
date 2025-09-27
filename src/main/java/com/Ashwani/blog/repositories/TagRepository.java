package com.Ashwani.blog.repositories;

import com.Ashwani.blog.domain.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    @Query("SELECT t FROM TAG t LEFT JOIN FETCH t.posts")
    List<Tag> findAllWithPostCount();
}
