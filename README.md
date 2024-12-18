# Task Management Application

## Overview
This **Task Management Application** is a Spring Boot-based web application that allows users to manage tasks efficiently. It provides role-based authentication and authorization, enabling administrators and users to perform specific operations. Key features include task creation, assignment, categorization, file uploads, and password management.

---

## Features

### 1. **User Management**
   - User **registration** with email verification.
   - **Login** with role-based redirection:
     - **Admin**: `/admin`
     - **User**: `/user/tasks`
   - Password reset functionality via email.
   - Profile management:
     - Update details
     - Change password
     - Upload profile photo

### 2. **Task Management**
   - Create, edit, delete, and assign tasks to users.
   - Categorize tasks under specific **categories**.
   - **Notifications** sent via email for task updates.

### 3. **Role-Based Access**
   - **Admin**:
     - View and manage all tasks.
     - Manage user accounts (create, update, delete).
     - Manage task categories.
   - **User**:
     - Update profile information.

### 4. **Pagination**
   - Tasks and users are displayed with **pagination**  for improved user experience.

### 5. **File Uploads**
   - Upload profile photos for users.
   - Secure storage and retrieval of uploaded files.

---

## Technology Stack

- **Backend**: Spring Boot (Java)
- **Frontend**: Thymeleaf, HTML, CSS
- **Database**: PostgreSQL
- **Security**: Spring Security (BCrypt for password encryption)
- **Email**: JavaMail API for notifications and password resets
- **File Storage**: Local file system storage
- **Build Tool**: Maven

