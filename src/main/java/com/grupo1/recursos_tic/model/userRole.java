package com.grupo1.recursos_tic.model;

import lombok.RequiredArgsConstructor;

/**
 * Roles de usuario
 */
@RequiredArgsConstructor
public enum userRole {

    AUTHOR("autor"),
    READER("reader"),
    ADMIN("admin");

    private final String userRole;

    @Override
    public String toString() { return userRole; }
}
