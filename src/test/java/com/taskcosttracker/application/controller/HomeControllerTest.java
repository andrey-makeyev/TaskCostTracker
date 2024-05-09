package com.taskcosttracker.application.controller;

import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.model.TaskOperation;
import com.taskcosttracker.application.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private HomeController homeController;

    @Test
    void showHomePageTest() {
        Model model = mock(Model.class);
        Iterable<Task> mockTasks = Collections.emptyList();
        Iterable<TaskOperation> mockOperations = Collections.emptyList();

        when(taskService.getAllTasks()).thenReturn(mockTasks);
        when(taskService.getAllTaskOperations()).thenReturn(mockOperations);

        String viewName = homeController.showHomePage(model);

        assertEquals("home", viewName);
        verify(taskService, times(1)).getAllTasks();
        verify(taskService, times(1)).getAllTaskOperations();
        verify(model, times(1)).addAttribute("tasks", mockTasks);
        verify(model, times(1)).addAttribute("operations", mockOperations);
    }
}