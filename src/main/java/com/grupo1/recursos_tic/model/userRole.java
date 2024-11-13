package com.grupo1.recursos_tic.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Roles de usuario
 */

@Getter
public enum userRole {

    AUTHOR("Autor"),
    READER("Lector"),
    ADMIN("Administrador");

    private final String displayValue;

    private userRole(String displayValue) {
        this.displayValue = displayValue;
    }

}
