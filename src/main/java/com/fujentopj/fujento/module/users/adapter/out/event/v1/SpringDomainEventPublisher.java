package com.fujentopj.fujento.module.users.adapter.out.event.v1;

import com.fujentopj.fujento.module.users.domain.event.DomainEvent;
import com.fujentopj.fujento.module.users.port.out.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

/**
 * Adapter che pubblica eventi usando il meccanismo Spring (ApplicationEventPublisher).
 * Se userai Kafka o Outbox in futuro, qui potrai creare KafkaDomainEventPublisher
 * o simile, senza toccare il dominio o l’application.
 */
public class SpringDomainEventPublisher implements DomainEventPublisher {
   private final ApplicationEventPublisher springPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        springPublisher.publishEvent(event);
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}
