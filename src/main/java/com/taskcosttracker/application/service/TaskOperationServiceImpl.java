package com.taskcosttracker.application.service;

import com.taskcosttracker.application.exception.TaskNotFoundException;
import com.taskcosttracker.application.model.OperationStatus;
import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.model.TaskOperation;
import com.taskcosttracker.application.model.TaskStatus;
import com.taskcosttracker.application.repository.TaskOperationRepository;
import com.taskcosttracker.application.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskOperationServiceImpl implements TaskOperationService {

    private static final Logger logger = LoggerFactory.getLogger(TaskOperationServiceImpl.class);

    private final TaskOperationRepository taskOperationRepository;
    private final TaskRepository taskRepository;

    @Override
    public List<TaskOperation> getAllTaskOperations() {
        Sort sort = Sort.by(Sort.Direction.ASC, "taskId");
        return taskOperationRepository.findAll(sort);
    }

    @Override
    public List<TaskOperation> getUnfinishedOperations() {
        return taskOperationRepository.findByStatusNot(OperationStatus.COMPLETED);
    }

    @Override
    public TaskOperation addTaskOperation(Long taskId, String description, Integer plannedQuantity, BigDecimal price) {
        logger.info("Adding new task operation for task id: {}, description: {}, planned quantity: {}, price: {}", taskId, description, plannedQuantity, price);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        if (task.getCost() == null) {
            throw new IllegalStateException("Task cost is null");
        }

        List<TaskOperation> taskOperations = task.getOperations();

        TaskOperation operation = new TaskOperation();
        operation.setTask(task);
        operation.setDescription(description);
        operation.setPlannedQuantity(plannedQuantity);
        operation.setPrice(price);
        operation.setStatus(OperationStatus.PROJECT);
        operation.setCost(BigDecimal.valueOf(plannedQuantity).multiply(price));

        taskOperations.add(operation);
        task.setOperations(taskOperations);
        BigDecimal totalCost = taskOperations.stream()
                .map(TaskOperation::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        task.setCost(totalCost);
        if (task.getStatus() == TaskStatus.PROJECT) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
        taskRepository.save(task);

        return taskOperationRepository.save(operation);
    }

    @Override
    public void markOperationCompleted(Long operationId, Integer actualQuantity) {
        Optional<TaskOperation> operationOptional = taskOperationRepository.findById(operationId);
        if (operationOptional.isPresent()) {
            TaskOperation operation = operationOptional.get();
            operation.setStatus(OperationStatus.COMPLETED);
            operation.setActualQuantity(actualQuantity);
            operation.setCost(BigDecimal.valueOf(actualQuantity).multiply(operation.getPrice()));
            Task task = operation.getTask();
            task.setCost(task.getCost().subtract(BigDecimal.valueOf(operation.getPlannedQuantity() - actualQuantity).multiply(operation.getPrice())));

            boolean allOperationsCompleted = task.getOperations().stream()
                    .allMatch(op -> op.getStatus() == OperationStatus.COMPLETED);

            if (allOperationsCompleted) {
                task.setStatus(TaskStatus.COMPLETED);
                task.setCompletionDate(LocalDate.now());
            } else {
                boolean anyOperationCompleted = task.getOperations().stream()
                        .anyMatch(op -> op.getStatus() == OperationStatus.COMPLETED);
                if (anyOperationCompleted && task.getStatus() != TaskStatus.COMPLETED) {
                    task.setStatus(TaskStatus.IN_PROGRESS);
                }
            }

            taskOperationRepository.save(operation);
            taskRepository.save(task);
        } else {
            throw new RuntimeException("Task operation with id " + operationId + " not found.");
        }
    }

}