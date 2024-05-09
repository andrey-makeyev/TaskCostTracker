package com.taskcosttracker.application.service;

import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.model.TaskOperation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TaskService {

    Task createTask(String description);

    BigDecimal getTotalCostOfCompletedTasksInRange(LocalDate startDate, LocalDate endDate);

    Iterable<Task> getIncompleteTasks();

    Iterable<Task> getCompletedTasksInRange(LocalDate startDate, LocalDate endDate);

    Iterable<TaskOperation> getAllTaskOperations();

    Task getTaskById(Long taskId);

    Iterable<Task> getAllTasks();

    List<Long> findAllTaskIds();

}