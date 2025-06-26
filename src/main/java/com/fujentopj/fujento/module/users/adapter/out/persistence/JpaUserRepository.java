package com.fujentopj.fujento.module.users.adapter.out.persistence;

import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;
import com.fujentopj.fujento.module.users.mapper.UserPersistenceMapper;
import com.fujentopj.fujento.module.users.port.out.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository springRepo;
    private final UserPersistenceMapper mapper;

    public JpaUserRepository(SpringDataUserRepository springRepo, UserPersistenceMapper mapper) {
        this.springRepo = springRepo;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toJpaEntity(user);
        UserJpaEntity saved = springRepo.save(entity);
        return mapper.toDomain(saved);
    }


    @Override
    public boolean existsByNickname(Nickname nickname) {
        return false;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return false;
    }

    @Override
    public void delete(User user) {

    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springRepo.findByEmail(email.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return springRepo.findById(id.value())
                .map(mapper::toDomain);
    }
}
