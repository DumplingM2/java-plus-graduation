package ru.practicum.explore.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.global.dto.Statuses;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation", nullable = false)
    String annotation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "event_date", nullable = true)
    LocalDateTime eventDate;

    @Column(name = "initiator_id")
    Long initiatorId;

    @Column(name = "paid", nullable = true)
    Boolean paid;

    @Column(name = "title", nullable = true)
    String title;

    @Transient
    Integer confirmedRequests = 0;

    @Transient
    Long views = 0L;

    @Column(name = "description", nullable = false)
    String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = true)
    Location location;

    @Column(name = "participant_limit", nullable = true)
    Integer participantLimit;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Column(name = "created_On", nullable = true)
    LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "published_On", nullable = true)
    LocalDateTime publishedOn;

    @Column(name = "state", nullable = true)
    String state = Statuses.PENDING.name();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        return id != null && id.equals(((Event) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

