package com.mykart.project.service;

import com.mykart.project.payload.CategoryDTO;
import com.mykart.project.payload.CategoryResponse;

public interface CategoryService {

    CategoryResponse getAllCategories();
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
