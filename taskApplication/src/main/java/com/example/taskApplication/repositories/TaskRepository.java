package com.example.taskApplication.repositories;

import com.example.taskApplication.models.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserUsername(String username, Sort sort);
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
    List<Task> findAllByUserUsernameOrderByDueDateAsc(String username);

    List<Task> findByUserUsernameAndStatus(String username, String status);
    List<Task> findByUserUsernameAndStatus(String username, String status, Sort sort);
    List<Task> findByUserUsernameAndCategoryId(String username, Long categoryId, Sort sort);
    List<Task> findByUserUsernameAndCategoryId(String username, Long categoryId);

    @Query("SELECT t FROM Task t WHERE t.user.username = :username " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
            "ORDER BY t.dueDate ASC")
    List<Task> findByFilters(
            @Param("username") String username,
            @Param("status") String status,
            @Param("categoryId") Long categoryId
    );
    List<Task> findByUserUsernameAndStatusAndCategoryId(String username, String status, Long categoryId, Sort sort);
    List<Task> findByUserUsernameAndStatusAndCategoryId(String username, String status, Long categoryId);
}
