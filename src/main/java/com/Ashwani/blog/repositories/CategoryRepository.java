package com.Ashwani.blog.repositories;

import com.Ashwani.blog.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("Select c FROM Category c LEFT JOIN FETCH c.posts")
    List<Category> findAllWithPostCount();
    boolean existsByNameIgnoreCase(String name);
}
