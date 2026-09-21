package com.yarnstash.project;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AllocationRequest(
        @NotNull(message = "yarnId is required")
        Long yarnId,

        @Min(value = 1, message = "yardsUsed must be at least 1")
        int yardsUsed
) { }
