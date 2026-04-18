package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Request DTO for goal operations")
public class GoalBasedRequestDto extends BaseRequestDto {
    @NotBlank(message = "Condition is required")
    @Schema(description = "Goal condition", example = "Run at least 3 times a week")
    private String condition;

    @NotNull(message = "Habit ID is required")
    @Positive(message = "Habit ID must be positive")
    @Schema(description = "Related habit id", example = "10")
    private Long habitId;

    public GoalBasedRequestDto(String name, String condition, Long habitId) {
        super(name);
        this.condition = condition;
        this.habitId = habitId;
    }

    @Override
    @NotBlank(message = "Goal name is required")
    public String getName() {
        return super.getName();
    }
}
