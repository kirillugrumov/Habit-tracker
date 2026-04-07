package com.example.habittracker.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NamedEntityRequestDto extends BaseRequestDto {
    private String description;

    public NamedEntityRequestDto(String name, String description) {
        super(name);
        this.description = description;
    }
}