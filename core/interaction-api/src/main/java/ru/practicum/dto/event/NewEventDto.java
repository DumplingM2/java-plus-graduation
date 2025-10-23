package ru.practicum.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.dto.event.validation.EventDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000, message = "Annotation should be between 20 and 2000 characters long")
    private String annotation;

    @NotNull
    @Positive
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000, message = "Description should be between 20 and 7000 characters long")
    private String description;

    @NotNull
    @EventDateTime
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid = false;

    @PositiveOrZero
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120, message = "Title should be between 3 and 120 characters long")
    private String title;
}

