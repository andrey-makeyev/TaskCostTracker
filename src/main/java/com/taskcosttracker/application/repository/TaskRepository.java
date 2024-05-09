package com.taskcosttracker.application.repository;

import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t.id FROM Task t")
    List<Long> findAllTaskIds();

    List<Task> findByStatusIn(TaskStatus... statuses);

    List<Task> findByStatusAndCompletionDateBetween(TaskStatus status, LocalDate startDate, LocalDate endDate);

}
