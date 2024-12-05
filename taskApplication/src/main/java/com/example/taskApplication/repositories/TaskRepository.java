package com.example.taskApplication.repositories;

import com.example.taskApplication.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserUsername(String username);

    @Query("SELECT t FROM Task t WHERE t.user.username = :username " +
            "AND (:category IS NULL OR t.category.name = :category) " +
            "AND (:status IS NULL OR t.status = :status)")
    List<Task> findByUserUsernameAndCategoryAndStatus(@Param("username") String username,
                                                      @Param("category") String category,
                                                      @Param("status") String status);

    @Query("SELECT t FROM Task t WHERE t.user.username = :username " +
            "AND t.dueDate < :currentDate")
    List<Task> findOverdueTasks(@Param("username") String username, @Param("currentDate") Date currentDate);

}
