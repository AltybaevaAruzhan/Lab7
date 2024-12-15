package com.example.taskApplication.services;

import com.example.taskApplication.models.Task;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

public interface TaskService {
    List<Task> findAllTasks();
    Task saveTask(Task task);
    List<Task> getTasksForCurrentUser();
    Task findTaskById(Long id);
    void deleteTask(Long id);

    List<Task> findTasksByCategoryAndStatus(String category, String status);

    List<Task> findOverdueTasks();
    List<Task> getTasksSortedByDueDate(Sort sort);
    List<Task> getTasksByStatus(String status);
    List<Task> getTasksByCategory(Long categoryId);
    List<Task> getTasksByFilters(String status, Long categoryId);
    List<Task> getTasksWithFiltersAndSorting(String status, Long categoryId, Sort sort);
}
