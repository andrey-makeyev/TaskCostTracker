package com.taskcosttracker.application.service;

import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.model.TaskStatus;
import com.taskcosttracker.application.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void createTaskTest() {
        Task task = new Task();
        task.setDescription("Test task");

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(task.getDescription());
        createdTask.setStatus(TaskStatus.PROJECT);

        assertEquals(task.getDescription(), createdTask.getDescription());
        assertEquals(TaskStatus.PROJECT, createdTask.getStatus());
        assertEquals(BigDecimal.ZERO, createdTask.getCost());
        assertEquals(task, createdTask);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getIncompleteTasksTest() {

        Task task1 = new Task();
        task1.setId(1L);
        task1.setStatus(TaskStatus.PROJECT);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findByStatusIn(TaskStatus.PROJECT, TaskStatus.IN_PROGRESS))
                .thenReturn(Arrays.asList(task1, task2));

        Iterable<Task> incompleteTasks = taskService.getIncompleteTasks();

        assertEquals(Arrays.asList(task1, task2), incompleteTasks);
    }

    @Test
    void getCompletedTasksInRangeTest() {

        LocalDate startDate = LocalDate.of(2024, 5, 1);
        LocalDate endDate = LocalDate.of(2024, 5, 10);

        Task task1 = new Task();
        task1.setId(1L);
        task1.setStatus(TaskStatus.COMPLETED);
        task1.setCompletionDate(LocalDate.of(2024, 5, 5));
        task1.setCost(BigDecimal.TEN);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setStatus(TaskStatus.COMPLETED);
        task2.setCompletionDate(LocalDate.of(2024, 5, 8));
        task2.setCost(BigDecimal.ONE);

        when(taskRepository.findByStatusAndCompletionDateBetween(TaskStatus.COMPLETED, startDate, endDate))
                .thenReturn(Arrays.asList(task1, task2));

        Iterable<Task> completedTasksInRange = taskService.getCompletedTasksInRange(startDate, endDate);

        List<Task> expectedTasks = Arrays.asList(task1, task2);
        assertEquals(expectedTasks, completedTasksInRange);
    }

    @Test
    void getTotalCostOfCompletedTasksInRangeTest() {

        LocalDate startDate = LocalDate.of(2024, 5, 1);
        LocalDate endDate = LocalDate.of(2024, 5, 10);

        Task task1 = new Task();
        task1.setId(1L);
        task1.setStatus(TaskStatus.COMPLETED);
        task1.setCompletionDate(LocalDate.of(2024, 5, 5));
        task1.setCost(BigDecimal.TEN);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setStatus(TaskStatus.COMPLETED);
        task2.setCompletionDate(LocalDate.of(2024, 5, 8));
        task2.setCost(BigDecimal.ONE);

        when(taskRepository.findByStatusAndCompletionDateBetween(TaskStatus.COMPLETED, startDate, endDate))
                .thenReturn(Arrays.asList(task1, task2));

        BigDecimal totalCost = taskService.getTotalCostOfCompletedTasksInRange(startDate, endDate);

        BigDecimal expectedTotalCost = BigDecimal.valueOf(11);
        assertEquals(expectedTotalCost, totalCost);
    }
}