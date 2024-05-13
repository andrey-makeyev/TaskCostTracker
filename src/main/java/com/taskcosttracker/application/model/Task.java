package com.taskcosttracker.application.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<TaskOperation> operations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                '}';
    }
}