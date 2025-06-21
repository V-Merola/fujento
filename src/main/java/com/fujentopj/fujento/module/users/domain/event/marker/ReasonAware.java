package com.fujentopj.fujento.module.users.domain.event.marker;

import java.util.Optional;

public interface ReasonAware {
    Optional<String> reason();
}
