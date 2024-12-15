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
import org.springframework.data.domain.Sort;
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
    public String getAllTasks(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            Model model
    ) {
        Sort sortingOrder = Sort.unsorted();
        if ("dueDate".equals(sort)) {
            sortingOrder = Sort.by(Sort.Direction.fromString(direction != null ? direction : "asc"), "dueDate");
        }

        List<Task> tasks = taskService.getTasksWithFiltersAndSorting(status, categoryId, sortingOrder);
        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("status", status);
        model.addAttribute("categoryId", categoryId);
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


    // Display tasks filtered by status
    @GetMapping("/filter/status")
    public String getTasksByStatus(@RequestParam String status, Model model) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        model.addAttribute("tasks", tasks);
        return "list";
    }

    // Display tasks filtered by category
    @GetMapping("/filter/category")
    public String getTasksByCategory(@RequestParam Long categoryId, Model model) {
        List<Task> tasks = taskService.getTasksByCategory(categoryId);
        model.addAttribute("tasks", tasks);
        return "list";
    }

    // Combined filtering by status and category
    @GetMapping("/filter")
    public String getTasksByFilters(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Logic to handle filtering
        List<Task> tasks;
        if ((status == null || status.isEmpty()) && categoryId == null) {
            // Return all tasks if no filters are applied
            tasks = taskRepository.findByUserUsername(username);
        } else if (status == null || status.isEmpty()) {
            // Filter by category only
            tasks = taskRepository.findByUserUsernameAndCategoryId(username, categoryId);
        } else if (categoryId == null) {
            // Filter by status only
            tasks = taskRepository.findByUserUsernameAndStatus(username, status);
        } else {
            // Filter by both status and category
            tasks = taskRepository.findByUserUsernameAndStatusAndCategoryId(username, status, categoryId);
        }

        // Add data to the model
        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("status", status); // Preserve selected filters
        model.addAttribute("categoryId", categoryId); // Preserve selected filters
        return "list";
    }
}
