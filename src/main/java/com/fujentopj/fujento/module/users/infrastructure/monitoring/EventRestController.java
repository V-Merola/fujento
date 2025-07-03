package com.fujentopj.fujento.module.users.infrastructure.monitoring;

import com.fujentopj.fujento.module.users.domain.event.DomainEvent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/dev/events")
public class EventRestController {

    private final InMemoryEventStore store;

    public EventRestController(InMemoryEventStore store) {
        this.store = store;
    }

    @GetMapping
    public List<DomainEvent> allEvents() {
        return store.getAll();
    }

    @DeleteMapping
    public void clearEvents() {
        store.clear();
    }
}
