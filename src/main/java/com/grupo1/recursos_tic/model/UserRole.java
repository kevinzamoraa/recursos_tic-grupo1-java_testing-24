package com.grupo1.recursos_tic.model;

import lombok.RequiredArgsConstructor;

/**
 * Roles de usuario
 */
@RequiredArgsConstructor
public enum UserRole {

    AUTHOR("Autor"),
    READER("Lector"),
    ADMIN("Administrador");

    private final String userRole;

    @Override
    public String toString() { return userRole; }
}
