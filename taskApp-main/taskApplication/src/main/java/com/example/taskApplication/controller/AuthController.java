package com.example.taskApplication.controller;

import com.example.taskApplication.models.User;
import com.example.taskApplication.security.PasswordGenerator;
import com.example.taskApplication.security.PasswordResetService;
import com.example.taskApplication.services.Impl.EmailServiceImpl;
import com.example.taskApplication.services.Impl.FileStorageServiceImpl;
import com.example.taskApplication.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/")
public class AuthController {

    private final UserService userService;
    @Autowired
    private PasswordResetService passwordResetService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private FileStorageServiceImpl fileStorageService;

    public AuthController(UserService userService, PasswordResetService passwordResetService, PasswordEncoder passwordEncoder, EmailServiceImpl emailService, FileStorageServiceImpl fileStorageService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.fileStorageService = fileStorageService;
    }

    private final String uploadDir = "uploads";

    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename = timestamp + "_" + originalFilename;

        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return filePath.toString();
    }
    @GetMapping("signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/signup";
    }

    @PostMapping("signup")
    public String signup(@Valid @ModelAttribute("user") User user,
                         BindingResult result,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/signup";
        }

        String randomPassword = PasswordGenerator.generateRandomPassword();
        try {
            emailService.sendEmail(user.getEmail(), "Password", randomPassword);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send email. Please try again.");
            return "auth/signup";
        }

        if (file != null && !file.isEmpty()) {
            try {
                String photoUrl = fileStorageService.saveFile(file);
                user.setPhoto(photoUrl);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload photo. Please try again.");
                return "redirect:/signup";
            }
        }

        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setRole(User.Role.USER);
        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("success", "Signup successful! Please log in.");
        return "redirect:/signin";
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
