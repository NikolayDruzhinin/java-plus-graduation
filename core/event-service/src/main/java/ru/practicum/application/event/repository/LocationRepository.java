package ru.practicum.application.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.application.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
