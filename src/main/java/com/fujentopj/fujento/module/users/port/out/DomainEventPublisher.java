package com.fujentopj.fujento.module.users.port.out;

import com.fujentopj.fujento.module.users.domain.event.DomainEvent;

import java.util.List;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
}
