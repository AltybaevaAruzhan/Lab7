package com.example.taskApplication.controller;

import com.example.taskApplication.models.Task;
import com.example.taskApplication.services.Impl.TaskServiceImpl;
import com.example.taskApplication.models.User;
import com.example.taskApplication.models.Category;
import com.example.taskApplication.repositories.CategoryRepository;
import com.example.taskApplication.repositories.TaskRepository;
import com.example.taskApplication.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TaskServiceImpl taskService;
    @Autowired
    private TaskServiceImpl taskServiceImpl;

    @GetMapping
    public String getAllTasks(Model model) {
        User user = getCurrentUser();
        List<Task> tasks = taskRepository.findByUserUsername(user.getUsername());
        model.addAttribute("tasks", tasks);
        return "list";
    }
    @GetMapping("/{id}")
    public String getDetailOfTasks(@PathVariable long id, Model model) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Task Id: " + id));
        model.addAttribute("task", task);
        return "details"; // Ensure the template file is named "detail.html"
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Task task = new Task();
        task.setCategory(new Category());
        model.addAttribute("task", task);
        model.addAttribute("categories", categoryRepository.findAll());
        return "create";
    }


    @PostMapping
    public String createTask(@Valid @ModelAttribute("task") Task task) {
        User user = getCurrentUser();
        task.setUser(user);
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        if (task.getCategory() == null) {
            task.setCategory(new Category()); // Prevent null category
        }
        model.addAttribute("task", task);
        model.addAttribute("categories", categoryRepository.findAll());
        return "edit";
    }



    @PostMapping("/{id}")
    public String updateTask(@PathVariable Long id, @Valid @ModelAttribute("task") Task taskDetails) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        task.setCategory(taskDetails.getCategory());
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }

    private User getCurrentUser() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByUsername(username).orElseThrow();
    }
    @GetMapping("/sorted")
    public List<Task> getAllTasksSortedByDueDate() {
        return taskServiceImpl.findOverdueTasks();
    }
}
