package com.example.taskApplication.services;

import com.example.taskApplication.models.Task;
import com.example.taskApplication.models.Category;
import com.example.taskApplication.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface TaskService {
    List<Task> getTasksWithFiltersAndSorting(String status, Long categoryId, Sort sort);
    List<Category> getAllCategories();
    List <Task> getTasksForCurrentUser();
    void updateTask(Long id, Task taskDetails);
    void notifyUser(Long taskId, String message);
    Task saveTask(Task task);
    void deleteTaskById(Long id);
    Task findTaskById(Long id);
    List<Task> findTasksByCategoryAndStatus(String category, String status);
    List<Task> findOverdueTasks();
    List<Task> findAllTasks();
    Task saveTaskWithUsers(Task task, User createdBy, User assignedTo);
    Page<Task> getTasksForAdmin(Pageable pageable);
    Page<Task> getPaginatedTasks(Pageable pageable);
    void sendTaskNotificationToUser(Long userId, String message);
    List<Task> findByUser(User user);
}
