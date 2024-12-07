package com.example.taskApplication.controller;

import com.example.taskApplication.models.Task;
import com.example.taskApplication.models.User;
import com.example.taskApplication.repositories.TaskRepository;
import com.example.taskApplication.services.Impl.TaskServiceImpl;
import com.example.taskApplication.services.Impl.UserServiceImpl;
import com.example.taskApplication.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskServiceImpl taskService;
    private final TaskRepository taskRepository;
    private final UserServiceImpl userService; // Add this as a final field

    @Autowired
    public TaskController(TaskServiceImpl taskService, TaskRepository taskRepository, UserServiceImpl userService) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.userService = userService; // Initialize it in the constructor
    }

    @GetMapping
    public String listTasks(Model model, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName()); // Use userService to fetch the current user
        List<Task> tasks = taskService.findByUser(currentUser);
        model.addAttribute("tasks", tasks);
        return "task/list";
    }

//    @GetMapping
//    public String getAllTasks(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "dueDate") String sort,
//            @RequestParam(defaultValue = "asc") String direction,
//            Model model
//    ) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
//        Page<Task> taskPage = taskRepository.findAll(pageable);
//
//        model.addAttribute("tasks", taskPage.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", taskPage.getTotalPages());
//        model.addAttribute("totalItems", taskPage.getTotalElements());
//        return "task/list";
//    }

    @GetMapping("/{id}")
    public String getTaskDetails(@PathVariable long id, Model model) {
        Task task = taskService.findTaskById(id);
        model.addAttribute("task", task);
        return "task/details";
    }
}
