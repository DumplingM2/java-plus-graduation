package ru.practicum.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchEventDto {
    @Size(min = 20, max = 2000, message = "Annotation should be between 20 and 2000 characters long")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Description should be between 20 and 7000 characters long")
    private String description;

    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Title should be between 3 and 120 characters long")
    private String title;

    private String stateAction;
}
