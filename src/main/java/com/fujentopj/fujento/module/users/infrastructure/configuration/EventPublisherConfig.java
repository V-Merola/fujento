package com.fujentopj.fujento.module.users.infrastructure.configuration;

import com.fujentopj.fujento.module.users.adapter.out.event.v1.SpringDomainEventPublisher;
import com.fujentopj.fujento.module.users.port.out.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventPublisherConfig {

    @Bean
    public DomainEventPublisher domainEventPublisher(ApplicationEventPublisher springPublisher) {
            return new SpringDomainEventPublisher(springPublisher);
    }
}
