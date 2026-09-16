package com.yarnstash.project;

public record YarnUsageResponse(
        Long projectId,
        String projectName,
        ProjectStatus status,
        int yardsUsed
) {
    public static YarnUsageResponse from(ProjectYarn allocation) {
        Project project = allocation.getProject();
        return new YarnUsageResponse(project.getId(), project.getName(), project.getStatus(), allocation.getYardsUsed());
    }
}
