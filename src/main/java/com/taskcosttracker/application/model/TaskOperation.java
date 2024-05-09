package com.taskcosttracker.application.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Data
public class TaskOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "task_id")
    private Task task;

    @NotBlank
    private String description;

    @NotNull
    private Integer plannedQuantity;

    private Integer actualQuantity;

    @NotNull
    private BigDecimal price;

    private BigDecimal cost;

    @Enumerated(EnumType.STRING)
    private OperationStatus status;

    @Override
    public String toString() {
        return "TaskOperation{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", plannedQuantity=" + plannedQuantity +
                ", actualQuantity=" + actualQuantity +
                ", price=" + price +
                ", cost=" + cost +
                ", status=" + status +
                '}';
    }
}