package com.taskcosttracker.application.controller;

import com.taskcosttracker.application.model.Task;
import com.taskcosttracker.application.model.TaskOperation;
import com.taskcosttracker.application.service.TaskOperationService;
import com.taskcosttracker.application.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Validated
@Controller
@RequestMapping("/operations")
public class TaskOperationController {

    private static final Logger logger = LoggerFactory.getLogger(TaskOperationController.class);

    private final TaskService taskService;
    private final TaskOperationService taskOperationService;

    @Autowired
    public TaskOperationController(TaskService taskService, TaskOperationService taskOperationService) {
        this.taskService = taskService;
        this.taskOperationService = taskOperationService;
    }

    @GetMapping("/new")
    public String showNewTaskOperationForm(Model model) {

        Iterable<Task> tasks = taskService.getIncompleteTasks();
        model.addAttribute("tasks", tasks);
        model.addAttribute("taskOperation", new TaskOperation());

        return "newTaskOperationForm";
    }

    @PostMapping("/new-operation")
    public String addTaskOperation(@RequestParam("taskId") Long taskId,
                                   @RequestParam("description") String description,
                                   @RequestParam("plannedQuantity") String plannedQuantityStr,
                                   @RequestParam("price") String priceStr,
                                   Model model) {
        logger.info("Adding task operation for task ID {} with planned quantity {} and description {}", taskId, plannedQuantityStr, description);

        Iterable<Task> tasks = taskService.getIncompleteTasks();
        model.addAttribute("tasks", tasks);

        if (description == null || description.isEmpty()) {
            model.addAttribute("descriptionError", "Description is required");

        }
        if (plannedQuantityStr == null || plannedQuantityStr.isEmpty()) {
            model.addAttribute("plannedQuantityError", "Planned quantity is required");
        }
        if (priceStr == null || priceStr.isEmpty()) {
            model.addAttribute("priceError", "Price is required");

        }

        try {
            Integer plannedQuantity = Integer.parseInt(plannedQuantityStr);
            BigDecimal price = new BigDecimal(priceStr);

            if (plannedQuantity < 0 || plannedQuantity != Math.floor(plannedQuantity) || String.valueOf(plannedQuantity).length() > 9) {
                model.addAttribute("plannedQuantityError", "Planned quantity must be a valid positive integer");
            }
            if (price.compareTo(BigDecimal.ZERO) < 0 || price.scale() > 2 || !price.toPlainString().matches("[0-9.]+")) {
                model.addAttribute("priceError", "Price must be a positive decimal number with up to two decimal places, separated by a dot");
            }

            if (model.containsAttribute("plannedQuantityError") || model.containsAttribute("priceError")) {
                return "newTaskOperationForm";
            }

            TaskOperation taskOperation = new TaskOperation();
            taskOperation.setId(taskId);
            taskOperation.setDescription(description);
            taskOperation.setPlannedQuantity(plannedQuantity);
            taskOperation.setPrice(price);
            taskOperationService.addTaskOperation(taskId, description, plannedQuantity, price);

            return "redirect:/";
        } catch (NumberFormatException e) {
            model.addAttribute("plannedQuantityError", "Planned quantity must be a valid number");
            model.addAttribute("priceError", "Price must be a valid number");
        } catch (Exception e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            String errorMessage = "Error while adding operation";
            String message = (rootCause != null) ? rootCause.getMessage() : "Unknown error";
            model.addAttribute("text-danger", errorMessage);
            logger.error(message);
        }

        return "newTaskOperationForm";
    }

    @GetMapping("/complete")
    public String showCompleteTaskOperationForm(Model model) {
        List<TaskOperation> unfinishedOperations = taskOperationService.getUnfinishedOperations();
        model.addAttribute("operations", unfinishedOperations);
        model.addAttribute("operation", new TaskOperation());
        return "completeTaskOperationForm";
    }

    @PostMapping("/complete-operation")
    public String completeTaskOperation(@RequestParam("operationId") Long operationId,
                                        @RequestParam("actualQuantity") String actualQuantityStr,
                                        Model model) {
        logger.info("Completing task operation with ID {} and actual quantity {}", operationId, actualQuantityStr);

        if (actualQuantityStr == null || actualQuantityStr.isEmpty()) {
            model.addAttribute("actualQuantityError", "Actual quantity is required");
        } else {
            try {
                Integer actualQuantity = Integer.parseInt(actualQuantityStr);
                if (actualQuantity <= 0) {
                    model.addAttribute("actualQuantityError", "Actual quantity must be a positive number");
                } else {
                    taskOperationService.markOperationCompleted(operationId, actualQuantity);
                    return "redirect:/";
                }
            } catch (NumberFormatException e) {
                model.addAttribute("actualQuantityError", "Actual quantity must be a valid number");
            } catch (Exception e) {
                Throwable rootCause = ExceptionUtils.getRootCause(e);
                String errorMessage = "Error while completing operation";
                String message = (rootCause != null) ? rootCause.getMessage() : "Unknown error";
                model.addAttribute("actualQuantityError", errorMessage);
                model.addAttribute("error", message);
            }
        }

        List<TaskOperation> unfinishedOperations = taskOperationService.getUnfinishedOperations();
        model.addAttribute("operations", unfinishedOperations);

        return "completeTaskOperationForm";
    }
}