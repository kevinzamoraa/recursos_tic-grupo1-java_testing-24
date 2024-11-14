package com.grupo1.recursos_tic.model;

import lombok.Getter;

/**
 * Roles de usuario
 */

@Getter
public enum UserRole {

    AUTHOR("Autor"),
    READER("Lector"),
    ADMIN("Administrador");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static UserRole fromName(String name) {
        for (UserRole role : values()) {
            if (role.name.equals(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching role found for name: " + name);
    }

}
