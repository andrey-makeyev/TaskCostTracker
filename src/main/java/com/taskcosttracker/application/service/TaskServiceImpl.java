package com.taskcosttracker.application.service;

import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.model.TaskStatus;
import com.taskcosttracker.application.repository.TaskOperationRepository;
import com.taskcosttracker.application.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final TaskOperationRepository operationRepository;

    @Override
    public Task createTask(String description) {
        logger.info("Creating task with description: {}", description);
        Task task = new Task();
        task.setDescription(description);
        task.setStatus(TaskStatus.PROJECT);
        task.setCost(BigDecimal.ZERO);
        return taskRepository.save(task);
    }

    @Override
    public Iterable<Task> getIncompleteTasks() {
        return taskRepository.findByStatusIn(TaskStatus.PROJECT, TaskStatus.IN_PROGRESS);
    }

    @Override
    public Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }


    @Override
    public Iterable<Task> getCompletedTasksInRange(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findByStatusAndCompletionDateBetween(TaskStatus.COMPLETED, startDate, endDate);
    }

    @Override
    public BigDecimal getTotalCostOfCompletedTasksInRange(LocalDate startDate, LocalDate endDate) {
        List<Task> completedTasks = (List<Task>) getCompletedTasksInRange(startDate, endDate);
        return completedTasks.stream()
                .map(Task::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<Long> findAllTaskIds() {
        return taskRepository.findAllTaskIds();
    }

}
