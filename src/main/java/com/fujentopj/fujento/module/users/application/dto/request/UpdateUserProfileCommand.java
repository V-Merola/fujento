package com.fujentopj.fujento.module.users.application.dto.request;


import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.valueObject.SportPreference;

import java.util.Set;

/**
 * Comando interno usato per aggiornare il profilo dell'utente.
 * Rappresenta l'intento dell'utente di modificare nickname, bio, immagine o preferenze sportive.
 * Viene passato dal layer application al dominio.
 */
public record UpdateUserProfileCommand(
        Nickname nickname,
        String bio,
        String imageUrl
) {
}
