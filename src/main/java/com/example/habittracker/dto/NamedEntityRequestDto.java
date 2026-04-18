package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Request DTO for named entities with description")
public class NamedEntityRequestDto extends BaseRequestDto {
    @Schema(description = "Optional description", example = "Activities related to wellness")
    @Size(max = 500, message = "Description too long")
    private String description;

    public NamedEntityRequestDto(String name, String description) {
        super(name);
        this.description = description;
    }
}
