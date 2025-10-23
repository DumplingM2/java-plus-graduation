package ru.practicum.explore.compilations.model;

import jakarta.persistence.*;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore.event.model.Event;

import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "title", nullable = false, length = 50)
    String title;

    @Column(name = "pinned", nullable = false)
    Boolean pinned = false;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    Set<Event> events;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compilation)) return false;
        return id != null && id.equals(((Compilation) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

