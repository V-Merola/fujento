package com.fujentopj.fujento.module.users.infrastructure.monitoring;

import com.fujentopj.fujento.module.users.domain.event.DomainEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
@Component
public class InMemoryEventStore {

    private final List<DomainEvent> events = new CopyOnWriteArrayList<>();

    @EventListener
    public void capture(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> getAll() {
        return List.copyOf(events);
    }

    public void clear() {
        events.clear();
    }
}
