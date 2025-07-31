package com.mykart.project.service;

import com.mykart.project.model.Category;
import com.mykart.project.payload.CategoryDTO;
import com.mykart.project.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories();
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    String deleteCategory(Long categoryId);
    Category updateCategory(Category category, Long categoryId);
}
