package com.fujentopj.fujento.module.users.domain.model.enums;

public enum Role {
    /** Utente registrato che partecipa agli eventi*/
    PLAYER,
    /** Allenatore, può gestire allenamenti e performance*/
    COACH,
    /** Gestore di evetni, squadre e altri utenti */
    MANAGER,
    /** Amministratore del sistema, ha accesso completo a tutte le funzionalità */
    ADMIN
}
