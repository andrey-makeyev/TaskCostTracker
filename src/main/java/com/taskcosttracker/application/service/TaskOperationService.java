package com.taskcosttracker.application.service;

import com.taskcosttracker.application.model.TaskOperation;

import java.math.BigDecimal;
import java.util.List;

public interface TaskOperationService {

    TaskOperation addTaskOperation(Long taskId, String description, Integer plannedQuantity, BigDecimal price);

    void markOperationCompleted(Long operationId, Integer actualQuantity);

    List<TaskOperation> getUnfinishedOperations();

}