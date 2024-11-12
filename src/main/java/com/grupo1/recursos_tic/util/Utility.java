package com.grupo1.recursos_tic.util;

import com.grupo1.recursos_tic.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@UtilityClass
public class Utility {

    /**
     * Comprueba si una cadena está vacía
     *
     * @param str String
     * @return boolean
     */
    public static boolean stringIsEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Comprueba si un número no es un número entero positivo
     *
     * @param num Long
     * @return boolean
     */
    public static boolean invalidIntPosNumber(Long num) {
        return num == null || num < 0L;
    }

    /**
     * Comprueba si esta abierta alguna sesión de usuario.
     * @return true si está abierta la sesión / false en caso contrario
     */
    public static Boolean isAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && !(authentication instanceof AnonymousAuthenticationToken));
    }

    /**
     * Devuelve el usuario de la sesión
     * @return El opcional de usuario autenticado, alternativamente, el opcional de usuario vacío.
     */
    public static Optional<User> userAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuth()) return Optional.ofNullable((User) authentication.getPrincipal());
        return Optional.empty();
    }

}
