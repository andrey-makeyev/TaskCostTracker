package com.taskcosttracker.application.controller;

import com.taskcosttracker.application.service.TaskOperationService;
import com.taskcosttracker.application.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskOperationControllerTest {

    @Mock
    private TaskOperationService taskOperationService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskOperationController taskOperationController;

    @Test
    void addTaskOperation_ValidDataTest() {
        Model model = new ConcurrentModel();
        String viewName = taskOperationController.addTaskOperation(1L, "Description", "5", "10", model);

        assertEquals("redirect:/", viewName);
        assertFalse(model.containsAttribute("taskOperation"));
        assertFalse(model.containsAttribute("description"));
        assertFalse(model.containsAttribute("plannedQuantityError"));
        assertFalse(model.containsAttribute("priceError"));
        verify(taskOperationService).addTaskOperation(1L, "Description", 5, BigDecimal.TEN);
        verifyNoMoreInteractions(taskOperationService);
    }

}