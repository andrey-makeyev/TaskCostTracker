package com.taskcosttracker.application.controller;

import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.service.TaskService;
import com.taskcosttracker.application.validator.DateRangeForm;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;

@Validated
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/new")
    public String showNewTaskForm(Model model) {
        logger.info("Displaying new task form");
        model.addAttribute("task", new Task());
        return "newTaskForm";
    }

    @PostMapping("/new-task")
    public String createNewTask(@ModelAttribute("task") @Valid Task task, BindingResult bindingResult, Model model) {

        logger.info("Creating new task");
        if (task.getDescription() == null || task.getDescription().isEmpty()) {
            bindingResult.rejectValue("description", "required", "Description is required");
            return "newTaskForm";
        } else if (bindingResult.hasErrors()) {
            return "newTaskForm";
        } else {
            try {
                taskService.createTask(task.getDescription());
                logger.info("New task created successfully");
            } catch (Exception e) {
                Throwable rootCause = ExceptionUtils.getRootCause(e);
                String errorMessage = "Error while creating task";
                String message = (rootCause != null) ? rootCause.getMessage() : "Unknown error";
                logger.error(errorMessage + ": " + message);
                model.addAttribute("validationError", errorMessage);
                return "newTaskForm";
            }
            return "redirect:/";
        }
    }

    @GetMapping("/uncompleted")
    public String showIncompleteTasks(Model model) {
        Iterable<Task> incompleteTasks = taskService.getIncompleteTasks();
        model.addAttribute("tasks", incompleteTasks);
        return "uncompletedTaskList";
    }

    @GetMapping("/completed")
    public String showCompletedTaskList(Model model) {
        LocalDate defaultStartDate = LocalDate.now().withDayOfYear(1);
        LocalDate defaultEndDate = LocalDate.now();

        model.addAttribute("dateRangeForm", new DateRangeForm());
        model.addAttribute("startDate", defaultStartDate);
        model.addAttribute("endDate", defaultEndDate);
        return "completedTaskList";
    }

    @PostMapping("/completed-in-range")
    public String showCompletedTaskListInRange(@Valid @ModelAttribute("dateRangeForm") DateRangeForm dateRangeForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "completedTaskList";
        }

        LocalDate startDate = dateRangeForm.getStartDate();
        LocalDate endDate = dateRangeForm.getEndDate();
        Iterable<Task> completedTasks = taskService.getCompletedTasksInRange(startDate, endDate);
        model.addAttribute("completedTasks", completedTasks);

        return "completedTaskList";
    }

    @GetMapping("/total")
    public String showTotalCostOfCompletedTasks(Model model) {
        LocalDate defaultStartDate = LocalDate.now().withDayOfYear(1);
        LocalDate defaultEndDate = LocalDate.now();

        model.addAttribute("dateRangeForm", new DateRangeForm());
        model.addAttribute("startDate", defaultStartDate);
        model.addAttribute("endDate", defaultEndDate);

        return "totalCostOfCompletedTaskList";
    }

    @PostMapping("/total-cost-of-completed")
    public String showTotalCostOfCompletedTasksInRange(@RequestParam("startDate") String startDateStr,
                                                       @RequestParam("endDate") String endDateStr,
                                                       Model model) {
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        double totalCost = taskService.getTotalCostOfCompletedTasksInRange(startDate, endDate).doubleValue();
        model.addAttribute("totalCost", totalCost);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "totalCostOfCompletedTaskList";
    }

}