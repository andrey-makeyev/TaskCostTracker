package com.taskcosttracker.application.controller;

import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
    }

    @Test
    void createNewTaskTest() {

        task.setDescription("Test task");
        BindingResult bindingResult = new BeanPropertyBindingResult(task, "task");
        Model model = new ConcurrentModel();
        String viewName = taskController.createNewTask(task, bindingResult, model);

        assertEquals("redirect:/", viewName);
        verify(taskService, times(1)).createTask("Test task");
    }

    @Test
    void createNewTask_InvalidDescriptionTest() {

        task.setDescription(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(task, "task");
        Model model = new ConcurrentModel();
        String viewName = taskController.createNewTask(task, bindingResult, model);

        assertEquals("newTaskForm", viewName);
        assertEquals(1, bindingResult.getErrorCount());
        verify(taskService, never()).createTask(anyString());
    }
}