package com.example.taskApplication.services.Impl;

import com.example.taskApplication.models.Category;
import com.example.taskApplication.models.Task;
import com.example.taskApplication.models.User;
import com.example.taskApplication.repositories.CategoryRepository;
import com.example.taskApplication.repositories.TaskRepository;
import com.example.taskApplication.repositories.UserRepository;
import com.example.taskApplication.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository,
                           CategoryRepository categoryRepository, JavaMailSender mailSender) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.mailSender = mailSender;
    }
    @Override
    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }
    @Override
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task saveTask(Task task) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        task.setCreatedBy(currentUser); // User who created the task
        return taskRepository.save(task);
    }

    @Override
    public Task saveTaskWithUsers(Task task, User createdBy, User assignedTo) {
        task.setCreatedBy(createdBy);
        task.setAssignedTo(assignedTo);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getTasksForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return taskRepository.findByAssignedToUsername(username);
    }

    @Override
    public Task findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found."));
    }

    @Override
    public void updateTask(Long id, Task updatedTask) {
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setCategory(updatedTask.getCategory());
        existingTask.setAssignedTo(updatedTask.getAssignedTo());
        taskRepository.save(existingTask);
    }


    @Override
    public void notifyUser(Long taskId, String message) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Task ID: " + taskId));
        User assignedUser = task.getAssignedTo();
        if (assignedUser == null) {
            throw new IllegalStateException("Task does not have an assigned user.");
        }
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(assignedUser.getEmail());
        mailMessage.setSubject("Task Notification");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
    @Override
    public Page<Task> getTasksForAssignedUser(String username, Pageable pageable) {
        return taskRepository.findByAssignedToUsername(username, pageable);
    }

    @Override
    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }


    @Override
    public List<Task> findTasksByCategoryAndStatus(String category, String status) {
        return taskRepository.findByCategoryAndStatus(category, status);
    }

    @Override
    public List<Task> findOverdueTasks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Date currentDate = new Date();
        return taskRepository.findOverdueTasks(username, currentDate);
    }


    @Override
    public List<Task> findByUser(User user) {
        return taskRepository.findByAssignedTo(user);
    }

    @Override
    public Page<Task> getTasksForAdmin(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public void sendTaskNotificationToUser(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Task Notification");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    @Override
    public List<Task> getTasksWithFiltersAndSorting(String status, Long categoryId, Sort sort) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return taskRepository.findByFilters(username, status, categoryId, sort);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Page<Task> getPaginatedTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }
}
