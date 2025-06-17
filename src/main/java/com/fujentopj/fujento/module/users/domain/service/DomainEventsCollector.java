package com.fujentopj.fujento.module.users.domain.service;

import com.fujentopj.fujento.module.users.domain.event.DomainEvent;
import com.fujentopj.fujento.module.users.domain.model.aggregate.User;

import java.util.List;

public interface DomainEventsCollector {
    List<DomainEvent> collectFrom(User root);
}
