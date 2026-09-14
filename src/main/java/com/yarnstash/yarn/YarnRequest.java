package com.yarnstash.yarn;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record YarnRequest(

        @NotBlank(message = "brand is required")
        @Size(max = 120, message = "brand must be at most 120 characters")
        String brand,

        @NotBlank(message = "colorway is required")
        @Size(max = 120, message = "colorway must be at most 120 characters")
        String colorway,

        @Size(max = 200, message = "fiber must be at most 200 characters")
        String fiber,

        @NotNull(message = "weight is required")
        YarnWeight weight,

        @Min(value = 0, message = "skeins cannot be negative")
        int skeins,

        @Min(value = 1, message = "yardsPerSkein must be at least 1")
        int yardsPerSkein) {
    public Yarn toEntity() {
        return new Yarn(null, brand, colorway, fiber, weight, skeins, yardsPerSkein);
    }
}
