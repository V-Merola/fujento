package com.fujentopj.fujento.module.users.mapper;

import com.fujentopj.fujento.module.users.adapter.out.persistence.UserJpaEntity;
import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.HashedPassword;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {
    public UserJpaEntity toJpaEntity(User user){
        UserJpaEntity entity = new UserJpaEntity(
                user.getId().value(),
                user.getEmail().getValue(),
                user.getPassword().getValue(),
                user.getNickname().getValue(),
                user.getRole(),
                user.getStatus()
        );
        // NON toccare entity.setVersion(...) — lascia a Hibernate
        return entity;
    }
    public User toDomain(UserJpaEntity entity) {
        return User.rehydrate(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail()),
                HashedPassword.of(entity.getPassword()),
                Nickname.of(entity.getNickname()),
                entity.getRole(),
                entity.getStatus()
        );
    }
}
