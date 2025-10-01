package com.Ashwani.blog.services;

import com.Ashwani.blog.domain.entities.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> listCategories();

    Category createCategory(Category category);

    Category updateCategory(Category category,UUID id);

    void deleteCategory(UUID id);

    Category getCategoryById(UUID id);

}
