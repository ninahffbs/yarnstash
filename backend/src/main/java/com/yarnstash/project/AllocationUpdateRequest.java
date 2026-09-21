package com.yarnstash.project;

import jakarta.validation.constraints.Min;

public record AllocationUpdateRequest(
        @Min(value = 1, message = "yardsUsed must be at least 1")
        int yardsUsed
) { }
