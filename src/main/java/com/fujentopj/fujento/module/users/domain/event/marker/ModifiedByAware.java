package com.fujentopj.fujento.module.users.domain.event.marker;

import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

public interface ModifiedByAware {
    UserId modifiedBy();
}
