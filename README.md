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
![Снимок экрана 2024-12-18 052440](https://github.com/user-attachments/assets/f607f070-0e30-415d-ba7e-f1896e945e24)
![Снимок экрана 2024-12-18 052457](https://github.com/user-attachments/assets/e9b68553-2c3b-4ca0-91ff-def1917bfdb1)
### **Admin Page**
![Снимок экрана 2024-12-18 052520](https://github.com/user-attachments/assets/c67a0414-df25-4eb9-bb02-a644220ecc64)
![Снимок экрана 2024-12-18 052529](https://github.com/user-attachments/assets/70d4dd9d-6c88-441e-9da8-e41f7e556320)
![Снимок экрана 2024-12-18 052537](https://github.com/user-attachments/assets/70e34ed3-a356-41f3-9580-834b465fb133)
![Снимок экрана 2024-12-18 052544](https://github.com/user-attachments/assets/065e7a8a-d278-47a6-a5b9-e99133054c32)
![Снимок экрана 2024-12-18 053426](https://github.com/user-attachments/assets/e23cfdeb-15fa-4b86-a43a-5cca6163d0ab)
### **User Page**
![Снимок экрана 2024-12-18 052611](https://github.com/user-attachments/assets/9881ed8a-5eb9-46e0-b7ae-184a14184396)
![Снимок экрана 2024-12-18 053321](https://github.com/user-attachments/assets/a49a8765-d583-4e45-8037-3d07427b4cdd)

