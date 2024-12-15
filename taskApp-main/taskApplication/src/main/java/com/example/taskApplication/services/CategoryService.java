package com.example.taskApplication.services;


import com.example.taskApplication.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    Page<Category> getPaginatedCategories(Pageable pageable);
    List<Category> findAllCategories();
    Category saveCategory(Category category);
    Category findCategoryById(Long id);
    void deleteCategory(Long id);
}

