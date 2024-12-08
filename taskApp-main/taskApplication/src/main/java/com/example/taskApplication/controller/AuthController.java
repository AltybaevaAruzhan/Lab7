package com.example.taskApplication.controller;

import com.example.taskApplication.models.User;
import com.example.taskApplication.security.PasswordResetService;
import com.example.taskApplication.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class AuthController {

    private final UserService userService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/signup";
    }

    @PostMapping("signup")
    public String signup(@Valid @ModelAttribute("user") User user,
                         BindingResult result,
                         @RequestParam("confirmPassword") String confirmPassword,
                         Model model) {
        if (result.hasErrors()) {
            return "auth/signup";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "auth/signup";
        }
        user.setRole(User.Role.USER);
        userService.saveUser(user);
        return "redirect:/signin?success";
    }

    @GetMapping("signin")
    public String signin() {
        return "auth/login";
    }

    @GetMapping("reset-password")
    public String resetPasswordForm() {
        return "auth/reset-password";
    }

    @PostMapping("reset-password")
    public String resetPassword(@RequestParam("email") String email, Model model) {
        try {
            String newPassword = passwordResetService.resetPassword(email);
            model.addAttribute("message", "A new password has been sent to your email.");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", "Email not found");
        }
        return "auth/reset-password";
    }
}
