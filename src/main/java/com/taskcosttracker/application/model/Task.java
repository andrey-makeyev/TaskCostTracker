package com.taskcosttracker.application.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Task {

    public Task() {
        this.cost = BigDecimal.ZERO;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String description;

    private BigDecimal cost;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDate completionDate;

    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER)
    private List<TaskOperation> operations;

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", cost=" + cost +
                ", completionDate=" + completionDate +
                '}';
    }
}