package com.example.taskApplication.controller;

import com.example.taskApplication.models.PasswordUpdateForm;
import com.example.taskApplication.models.Task;
import com.example.taskApplication.models.User;
import com.example.taskApplication.repositories.TaskRepository;
import com.example.taskApplication.security.CustomUserDetails;
import com.example.taskApplication.services.Impl.FileStorageServiceImpl;
import com.example.taskApplication.services.Impl.TaskServiceImpl;
import com.example.taskApplication.services.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class TaskController {

    private final TaskServiceImpl taskService;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceImpl userService;
    private final FileStorageServiceImpl fileStorageService;

    @Autowired
    public TaskController(TaskServiceImpl taskService, TaskRepository taskRepository, PasswordEncoder passwordEncoder, UserServiceImpl userService, FileStorageServiceImpl fileStorageService) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/tasks")
    public String getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model,
            Principal principal
    ) {
        String currentUsername = principal.getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));

        Page<Task> taskPage = taskService.getTasksForAssignedUser(currentUsername, pageable);

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("totalItems", taskPage.getTotalElements());
        return "user/list";
    }

    @GetMapping("/task/{id}")
    public String getTaskDetails(@PathVariable long id, Model model) {
        Task task = taskService.findTaskById(id);
        model.addAttribute("task", task);
        return "user/details";
    }
    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user/profile";
    }
    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute User updatedUser, Principal principal, RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(principal.getName());

        currentUser.setUsername(updatedUser.getUsername());
        currentUser.setEmail(updatedUser.getEmail());

        userService.saveUserPreservingPassword(currentUser);

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails updatedUserDetails = new CustomUserDetails(currentUser);
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUserDetails, currentAuth.getCredentials(), updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");

        return "redirect:/user/profile";
    }

    @PostMapping("/profile/change-password")
    public String updatePassword(@ModelAttribute("passwordForm") PasswordUpdateForm passwordForm,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(principal.getName());

        if (!passwordEncoder.matches(passwordForm.getCurrentPassword(), currentUser.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/user/profile";
        }

        if (!passwordForm.getNewPassword().equals(passwordForm.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "New password and confirmation do not match.");
            return "redirect:/user/profile";
        }


        currentUser.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        userService.saveUser(currentUser);


        redirectAttributes.addFlashAttribute("success", "Password updated successfully. Please log in again.");
        return "redirect:/signin";
    }

    @PostMapping("/profile/upload-photo")
    public String uploadPhoto(@RequestParam("photo") MultipartFile file,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());

            String photoUrl = fileStorageService.saveFile(file);
            user.setPhoto(photoUrl);

            userService.saveUserPreservingPassword(user);

            redirectAttributes.addFlashAttribute("success", "Profile photo updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload photo.");
        }

        return "redirect:/user/profile";
    }
}
