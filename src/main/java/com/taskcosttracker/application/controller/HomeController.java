package com.taskcosttracker.application.controller;

import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.model.TaskOperation;
import com.taskcosttracker.application.service.TaskOperationService;
import com.taskcosttracker.application.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final TaskService taskService;
    private final TaskOperationService taskOperationService;

    @Autowired
    public HomeController(TaskService taskService, TaskOperationService taskOperationService) {
        this.taskService = taskService;
        this.taskOperationService = taskOperationService;
    }

    @GetMapping
    public String showHomePage(Model model) {

        logger.info("Displaying home page");
        Iterable<Task> tasks = taskService.getAllTasks();
        Iterable<TaskOperation> operations = taskOperationService.getAllTaskOperations();

        model.addAttribute("tasks", tasks);
        model.addAttribute("operations", operations);
        return "home";
    }

}