package fr.insa.ms.historyservice.controller;

import fr.insa.ms.historyservice.model.Event;
import fr.insa.ms.historyservice.repository.EventRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/history")
public class HistoryController {

    private final EventRepository eventRepository;

    public HistoryController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping("/save")
    public Event saveEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @GetMapping("/all")
    public Iterable<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}
