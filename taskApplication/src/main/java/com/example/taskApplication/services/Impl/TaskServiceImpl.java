package com.example.taskApplication.services.Impl;

import com.example.taskApplication.models.Task;
import com.example.taskApplication.repositories.TaskRepository;
import com.example.taskApplication.repositories.UserRepository;
import com.example.taskApplication.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task saveTask(Task task) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        task.setUser(userRepository.findByUsername(username).orElseThrow());
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getTasksForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return taskRepository.findByUserUsername(username);
    }

    @Override
    public Task findTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteTask(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Task task = taskRepository.findById(id).orElseThrow();
        if (task.getUser().getUsername().equals(username)) {
            taskRepository.deleteById(id);
        }
    }

    @Override
    public List<Task> findTasksByCategoryAndStatus(String category, String status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return taskRepository.findByUserUsernameAndCategoryAndStatus(username, category, status);
    }
    @Override
    public List<Task> findOverdueTasks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Date currentDate = new Date();
        return taskRepository.findOverdueTasks(username, currentDate);
    }

    @Override
    public List<Task> getTasksSortedByDueDate(Sort sort) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return taskRepository.findByUserUsername(username, sort);
    }

    @Override
    public List<Task> getTasksByStatus(String status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return taskRepository.findByUserUsernameAndStatus(username, status);
    }

    @Override
    public List<Task> getTasksByCategory(Long categoryId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return taskRepository.findByUserUsernameAndCategoryId(username, categoryId);
    }

    @Override
    public List<Task> getTasksByFilters(String status, Long categoryId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return taskRepository.findByFilters(username, status, categoryId);
    }
    @Override
    public List<Task> getTasksWithFiltersAndSorting(String status, Long categoryId, Sort sort) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if ((status == null || status.isEmpty()) && categoryId == null) {
            return taskRepository.findByUserUsername(username, sort);
        } else if (status == null || status.isEmpty()) {
            return taskRepository.findByUserUsernameAndCategoryId(username, categoryId, sort);
        } else if (categoryId == null) {
            return taskRepository.findByUserUsernameAndStatus(username, status, sort);
        } else {
            return taskRepository.findByUserUsernameAndStatusAndCategoryId(username, status, categoryId, sort);
        }
    }
}
