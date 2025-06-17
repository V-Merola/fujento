/*
package com.fujentopj.fujento.module.users.adapter.out.persistence;

@Entity
@Table(name = "users")
public class UserJpaEntity {
    @Id
    private UUID id;

    private String email;
    private String password;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // ➕ Aggiunto: gestione versioning
    @Version
    private Long version;

    // ➕ Opzionale: multi-tenancy support
    private String tenantId;

    // ➕ Soft delete (boolean flag)
    private boolean deleted = false;

    // costruttori, getter/setter...
}
*/