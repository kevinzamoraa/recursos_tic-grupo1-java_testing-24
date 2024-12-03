package com.grupo1.recursos_tic.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ErrorController {

//    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
//    @ExceptionHandler(Exception.class)
//    public String handleException(Exception ex, Model model) {
//        model.addAttribute("message", ex.getMessage());
//        return "error";
//    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(
            IllegalArgumentException ex, Model model,
            HttpServletResponse response) {
        //response.setStatus(HttpServletRequest.SC_BAD_REQUEST);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    // Capturar el estátus que se devuelve en la excepción
    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatus(
            ResponseStatusException ex, Model model,
            HttpServletResponse response) {
        response.setStatus(ex.getStatusCode().value());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }


    // Nuevos métodos por cada error aquí, si se desea.

}
