package com.fujentopj.fujento.module.users.domain.service;

/**
 * Rappresenta una regola di dominio valutabile che può essere utilizzata per verificare
 * condizioni specifiche e restituire un messaggio in caso di non soddisfacimento.
 * Questa interfaccia è utile per implementare
 * regole di business che devono essere verificate prima di eseguire operazioni sul dominio,
 * come la registrazione di un utente, la modifica di una password, o l'assegnazione di un ruolo.
 * Implementazioni concrete di questa interfaccia dovrebbero fornire
 * la logica per verificare le condizioni specifiche e restituire un messaggio in caso di non soddisfacimento.
 * * Esempi di implementazioni potrebbero includere:
 * - `EmailFormatRule`: verifica che l'email sia in un formato valido.
 * - `PasswordStrengthRule`: verifica che la password soddisfi i requisiti di complessità.
 * - `RoleAssignmentRule`: verifica che l'assegnazione di un ruolo sia valida per l'utente.
 * Queste regole possono essere combinate per formare complesse logiche di validazione e possono essere utilizzate
 * in contesti come la registrazione di utenti, modifica di profili, assegnazione di permessi,
 * e altre operazioni che richiedono verifiche di integrità e coerenza nel dominio.
 *
 */

public interface BusinessRule {
    boolean isSatisfied();
    String message();
}
