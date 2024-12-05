package com.example.taskApplication.services;

import com.example.taskApplication.models.Task;

import java.util.Date;
import java.util.List;

public interface TaskService {
    List<Task> findAllTasks();
    Task saveTask(Task task);
    List<Task> getTasksForCurrentUser();
    Task findTaskById(Long id);
    void deleteTask(Long id);

    // New methods for filtering tasks
    List<Task> findTasksByCategoryAndStatus(String category, String status);

    // New method for overdue tasks
    List<Task> findOverdueTasks();
}
