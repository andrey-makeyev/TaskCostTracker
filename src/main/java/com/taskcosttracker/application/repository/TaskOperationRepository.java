package com.taskcosttracker.application.repository;

import com.taskcosttracker.application.model.OperationStatus;
import com.taskcosttracker.application.model.TaskOperation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskOperationRepository extends JpaRepository<TaskOperation, Long> {

    @Query("SELECT op FROM TaskOperation op WHERE op.status != ?1")
    List<TaskOperation> findByStatusNot(OperationStatus status);

    List<TaskOperation> findAll(Sort sort);
}
