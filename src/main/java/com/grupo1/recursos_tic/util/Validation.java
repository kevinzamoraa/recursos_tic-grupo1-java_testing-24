package com.grupo1.recursos_tic.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Validation {

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

}
