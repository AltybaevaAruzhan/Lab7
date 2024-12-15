package com.example.taskApplication.controller;

import com.example.taskApplication.models.User;
import com.example.taskApplication.services.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") User user, @RequestParam("confirmPassword") String confirmPassword) {
        if (!user.getPassword().equals(confirmPassword)) {
            return "redirect:/signup?error=passwordMismatch";
        }

        if (userService.isUsernameTaken(user.getUsername())) {
            return "redirect:/signup?error=usernameTaken";
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        userService.saveUser(user);

        return "redirect:/signin";
    }


    @GetMapping("/signup")
    public String register(Model m)
    {
        m.addAttribute("title","REGISTER");
        return "signup";
    }

    @GetMapping("/signin")
    public String showLoginForm() {
        return "login";
    }

}
