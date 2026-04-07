package com.example.habittracker.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GoalBasedRequestDto extends BaseRequestDto {
    private String condition;
    private Long habitId;

    public GoalBasedRequestDto(String name, String condition, Long habitId) {
        super(name);
        this.condition = condition;
        this.habitId = habitId;
    }
}