package ru.practicum.dto.compilations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}

