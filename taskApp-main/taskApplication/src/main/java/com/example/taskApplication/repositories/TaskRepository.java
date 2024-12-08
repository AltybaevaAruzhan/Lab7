package com.example.taskApplication.repositories;

import com.example.taskApplication.models.Task;
import com.example.taskApplication.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedToUsername(String username, Sort sort);
    List<Task> findByAssignedToUsername(String username);

    Page<Task> findByAssignedToId(Long userId, Pageable pageable);
    List<Task> findByCreatedBy(User createdBy);

    List<Task> findByAssignedTo(User assignedTo);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.username = :username " +
            "AND (:category IS NULL OR t.category.name = :category) " +
            "AND (:status IS NULL OR t.status = :status)")
    List<Task> findByAssignedToUsernameAndCategoryAndStatus(@Param("username") String username,
                                                            @Param("category") String category,
                                                            @Param("status") String status);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.username = :username " +
            "AND t.dueDate < :currentDate")

    List<Task> findAllByAssignedToUsernameOrderByDueDateAsc(String username);

    List<Task> findByAssignedToUsernameAndStatus(String username, String status);
    List<Task> findByAssignedToUsernameAndStatus(String username, String status, Sort sort);
    List<Task> findByAssignedToUsernameAndCategoryId(String username, Long categoryId, Sort sort);
    List<Task> findByAssignedToUsernameAndCategoryId(String username, Long categoryId);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.username = :username " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
            "ORDER BY t.dueDate ASC")
    List<Task> findByFilters(
            @Param("username") String username,
            @Param("status") String status,
            @Param("categoryId") Long categoryId
    );

    List<Task> findByAssignedToUsernameAndStatusAndCategoryId(String username, String status, Long categoryId, Sort sort);
    List<Task> findByAssignedToUsernameAndStatusAndCategoryId(String username, String status, Long categoryId);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.username = :username")
    Page<Task> findByAssignedToUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.username = :username " +
            "AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Task> searchTasks(@Param("username") String username,
                           @Param("search") String search,
                           Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.username = :username " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:categoryId IS NULL OR t.category.id = :categoryId)")
    List<Task> findByFilters(@Param("username") String username,
                             @Param("status") String status,
                             @Param("categoryId") Long categoryId,
                             Sort sort);

    @Query("SELECT t FROM Task t WHERE t.category.name = :category AND t.status = :status")
    List<Task> findByCategoryAndStatus(@Param("category") String category, @Param("status") String status);
    @Query("SELECT t FROM Task t WHERE t.assignedTo.username = :username AND t.dueDate < :currentDate")
    List<Task> findOverdueTasks(@Param("username") String username, @Param("currentDate") Date currentDate);

}
