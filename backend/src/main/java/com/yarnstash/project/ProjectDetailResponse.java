package com.yarnstash.project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProjectDetailResponse(Long id, String name, ProjectStatus status, BigDecimal hookSize, String notes, LocalDate startedOn, LocalDate finishedOn, int totalYardsUsed, List<AllocationResponse> allocations) {
    public static ProjectDetailResponse from(Project project) {
        List<AllocationResponse> allocations = project.getAllocations().stream()
                .map(AllocationResponse::from)
                .toList();

        int totalYardsUsed = allocations.stream()
                .mapToInt(AllocationResponse::yardsUsed)
                .sum();

        return new ProjectDetailResponse(
                project.getId(),
                project.getName(),
                project.getStatus(),
                project.getHookSize(),
                project.getNotes(),
                project.getStartedOn(),
                project.getFinishedOn(),
                totalYardsUsed,
                allocations);
    }
}
