package com.fujentopj.fujento.module.users.domain.model.enums;

public enum UserStatus {
    /** Profilo attivo e funzionante*/
    ACTIVE,
    /** Profilo disattivato, ma non cancellato (es da ADMIN o Utente stesso*/
    DISABLED,
    /** Profilo bannato, per violazione del sitema non può più accedere al sistema*/
    BANNED,

    INACTIVE,
    SUSPENDED,

    DELETED; // Profilo cancellato, non può più accedere al sistema
}
