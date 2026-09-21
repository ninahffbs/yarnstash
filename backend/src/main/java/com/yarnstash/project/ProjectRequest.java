package com.yarnstash.project;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectRequest (
        @NotBlank(message = "name is required")
        @Size(max = 120, message = "name must be at most 120 characters")
        String name,

        ProjectStatus status,

        @DecimalMin(value = "0.5", message = "hookSize must be at least 0.5")
        @DecimalMax(value = "30.0", message = "hookSize must be at most 30.0")
        @Digits(integer = 2, fraction = 2, message = "hookSize allows at most 2 decimal places")
        BigDecimal hookSize,

        @Size(max = 5000, message = "notes must be at most 5000 characters")
        String notes,

        @PastOrPresent(message = "start date cannot be in the future")
        LocalDate startedOn,

        @PastOrPresent(message = "finish date cannot be in the future")
        LocalDate finishedOn) {
    @AssertTrue(message = "finish date cannot be before start date")
    public boolean isDateOrderValid() {
        return startedOn == null || finishedOn == null || !finishedOn.isBefore(startedOn);
    }
    public Project toEntity() { return new Project(null, name, status, hookSize, notes, startedOn, finishedOn); }
}

