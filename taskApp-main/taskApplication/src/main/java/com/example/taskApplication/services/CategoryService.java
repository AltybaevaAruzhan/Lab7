package com.example.taskApplication.services;


import com.example.taskApplication.models.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAllCategories();
    Category saveCategory(Category category);
    Category findCategoryById(Long id);
    void deleteCategory(Long id);
}

