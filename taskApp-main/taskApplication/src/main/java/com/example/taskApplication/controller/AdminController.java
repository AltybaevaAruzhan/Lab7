package com.example.taskApplication.controller;

import com.example.taskApplication.models.Task;
import com.example.taskApplication.models.User;
import com.example.taskApplication.models.Category;
import com.example.taskApplication.services.Impl.CategoryServiceImpl;
import com.example.taskApplication.services.Impl.EmailServiceImpl;
import com.example.taskApplication.services.Impl.TaskServiceImpl;
import com.example.taskApplication.services.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private CategoryServiceImpl categoryService;
    @GetMapping()
    public String getAdminDashboard(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String viewAllUsers(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<User> users = userService.getPaginatedUsers(pageable);
        model.addAttribute("users", users.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        return "admin/users";
    }
    @GetMapping("/users/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/createUser";
    }
    @PostMapping("/users/create")
    public String createUsers(@ModelAttribute("user") User user){
        userService.saveUser(user);
        return "redirect:/admin/users";
    }
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/tasks")
    public String viewAllTasks(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Task> tasks = taskService.getPaginatedTasks(pageable);

        // Check and handle null createdBy for tasks
        tasks.getContent().forEach(task -> {
            if (task.getCreatedBy() == null) {
                User fallbackUser = new User(); // Default constructor
                fallbackUser.setUsername("Aruzhan"); // Set username explicitly
                task.setCreatedBy(fallbackUser);
            }
        });

        model.addAttribute("tasks", tasks.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tasks.getTotalPages());
        return "admin/tasks";
    }

    //    @GetMapping("/user/{id}/tasks")
//    public String viewUserTasks(@PathVariable Long id,
//                                @RequestParam(defaultValue = "0") int page,
//                                Model model) {
//        Pageable pageable = PageRequest.of(page, 10);
//        Page<Task> tasks = taskService.getTasksByUserId(id, pageable);
//        User user = userService.getUserById(id).orElse(null);
//
//        if (user == null) {
//            return "redirect:/admin/users"; // Redirect if user is not found
//        }
//
//        model.addAttribute("tasks", tasks.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", tasks.getTotalPages());
//        model.addAttribute("user", user);
//
//        return "admin/userTasks";
//    }
    @GetMapping("/tasks/create")
    public String showCreateTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("categories", taskService.getAllCategories());
        return "admin/createTask";
    }

    @PostMapping("/tasks/create")
    public String createTask(
            @ModelAttribute("task") Task task,
            @RequestParam("assignedToUserId") Long assignedToUserId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            if (principal == null || principal.getName() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to create a task.");
                return "redirect:/signin";
            }

            // Fetch the user by username
            String username = principal.getName();
            System.out.println("Authenticated Username: " + username);

            User createdBy = userService.getUserByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

            User assignedTo = userService.getUserById(assignedToUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid assigned user ID: " + assignedToUserId));
            task.setCreatedBy(createdBy); // This is critical to prevent the NULL error
            task.setAssignedTo(assignedTo);
            taskService.saveTaskWithUsers(task, createdBy, assignedTo);
            redirectAttributes.addFlashAttribute("successMessage", "Task created successfully!");

            return "redirect:/admin/tasks";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create task: " + e.getMessage());
            return "redirect:/admin/tasks/create";
        }
    }







    @GetMapping("/categories")
    public String viewAllCategories(Model model) {
        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String addCategory(@ModelAttribute("category") Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/categories";
    }



    @GetMapping("/tasks/{taskId}/notify")
    public String showNotificationPage(@PathVariable Long taskId, Model model) {
        Task task = taskService.findTaskById(taskId);
        if (task == null) {
            System.out.println("Task not found for ID: " + taskId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        // Add the specific task to the model
        model.addAttribute("task", task);

        // Add the assigned user or a default user to the model
        model.addAttribute("assignedUser", task.getAssignedTo() != null ? task.getAssignedTo() : new User());

        return "admin/notifications"; // Ensure this template exists in the correct directory
    }




    @PostMapping("/tasks/{taskId}/notify")
    public String sendNotification(@PathVariable Long taskId,
                                   @RequestParam String email,
                                   @RequestParam String message,
                                   RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Email cannot be blank.");
            return "redirect:/admin/tasks";
        }
        if (message == null || message.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Message cannot be blank.");
            return "redirect:/admin/tasks";
        }

        try {
            emailService.sendEmail(email, "Task Notification", message);
            redirectAttributes.addFlashAttribute("success", "Notification sent successfully to " + email);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send notification. Please try again.");
        }

        return "redirect:/admin/tasks";
    }


    @GetMapping("/tasks/{id}/edit")
    public String showEditTaskForm(@PathVariable Long id, Model model) {
        Task task = taskService.findTaskById(id);
        List<User> users = userService.getAllUsers();
        List<Category> categories = categoryService.findAllCategories();

        model.addAttribute("task", task);
        model.addAttribute("users", users);
        model.addAttribute("categories", categories);
        return "admin/editTask";
    }

    @PostMapping("/tasks/{id}/edit")
    public String editTask(@PathVariable Long id, @ModelAttribute("task") Task task,
                           @RequestParam("assignedToId") Long assignedToUserId) {
        User assignedTo = userService.getUserById(assignedToUserId).orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
        task.setAssignedTo(assignedTo);
        taskService.updateTask(id, task);
        return "redirect:/admin/tasks";
    }
    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id); // Implement this method in the service
        return "redirect:/admin/tasks";
    }

}
