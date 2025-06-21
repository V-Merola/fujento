package com.fujentopj.fujento.module.users.domain.support;

import com.fujentopj.fujento.module.users.domain.event.DomainEvent;
import com.fujentopj.fujento.module.users.domain.event.marker.ModifiedByAware;
import com.fujentopj.fujento.module.users.domain.event.marker.ReasonAware;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.Optional;

public class DomainEventLogger {

    private DomainEventLogger() {}

    public static void logTransition(Logger log, DomainEvent event, String context) {
        String userId = event.aggregateId().value().toString();
        String occurredAt = event.occurredAt().toString();

        String modifiedBy = (event instanceof ModifiedByAware m)
                ? m.modifiedBy().value().toString()
                : "-";

        String reason = (event instanceof ReasonAware r)
                ? r.reason().orElse("-")
                : "-";

        log.info("[{}] Utente {} - evento {} - by: {} - reason: {} - at: {}",
                context,
                userId,
                event.getClass().getSimpleName(),
                modifiedBy,
                reason,
                occurredAt
        );
    }

}
