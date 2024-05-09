package com.taskcosttracker.application.service;

import com.taskcosttracker.application.exception.TaskNotFoundException;
import com.taskcosttracker.application.model.OperationStatus;
import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.model.TaskOperation;
import com.taskcosttracker.application.model.TaskStatus;
import com.taskcosttracker.application.repository.TaskOperationRepository;
import com.taskcosttracker.application.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskOperationServiceImplTest {

    @Mock
    private TaskOperationRepository taskOperationRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskOperationServiceImpl taskOperationService;

    @Test
    void addTaskOperationTest() {
        Task task = new Task();
        task.setId(1L);
        task.setOperations(new ArrayList<>());

        TaskOperation operation = new TaskOperation();
        operation.setDescription("Test operation");
        operation.setTask(task);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskOperationRepository.save(any(TaskOperation.class))).thenReturn(operation);

        TaskOperation addedOperation = taskOperationService.addTaskOperation(1L, "Test operation", 5, BigDecimal.ZERO);

        assertEquals(operation, addedOperation);
        assertEquals(1, task.getOperations().size());
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskOperationRepository, times(1)).save(any(TaskOperation.class));
    }

    @Test
    void markOperationCompletedTest() {

        long operationId = 1L;
        int plannedQuantity = 10;
        BigDecimal price = BigDecimal.valueOf(5);
        int actualQuantity = 5;

        Task task = new Task();
        task.setId(1L);
        task.setCost(BigDecimal.valueOf(50));
        task.setStatus(TaskStatus.IN_PROGRESS);

        TaskOperation operation = new TaskOperation();
        operation.setId(operationId);
        operation.setTask(task);
        operation.setPlannedQuantity(plannedQuantity);
        operation.setPrice(price);
        operation.setStatus(OperationStatus.PROJECT);

        List<TaskOperation> taskOperations = new ArrayList<>();
        taskOperations.add(operation);
        task.setOperations(taskOperations);

        when(taskOperationRepository.findById(operationId)).thenReturn(Optional.of(operation));

        taskOperationService.markOperationCompleted(operationId, actualQuantity);

        assertEquals(OperationStatus.COMPLETED, operation.getStatus());
        assertEquals(BigDecimal.valueOf(actualQuantity).multiply(price), operation.getCost());
        assertEquals(BigDecimal.valueOf(25), task.getCost());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        verify(taskOperationRepository, times(1)).save(operation);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void addTaskOperation_TaskNotFoundTest() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> {
            taskOperationService.addTaskOperation(1L, "Test operation", 5, BigDecimal.ZERO);
        });
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskOperationRepository, never()).save(any(TaskOperation.class));
    }
}
