package fr.insa.ms.historyservice.repository;

import fr.insa.ms.historyservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
