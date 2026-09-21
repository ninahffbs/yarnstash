package com.yarnstash.project;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectResponse(Long id, String name, ProjectStatus status, BigDecimal hookSize, String notes, LocalDate startedOn, LocalDate finishedOn) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(project.getId(), project.getName(), project.getStatus(), project.getHookSize(), project.getNotes(), project.getStartedOn(), project.getFinishedOn());
    }
}
