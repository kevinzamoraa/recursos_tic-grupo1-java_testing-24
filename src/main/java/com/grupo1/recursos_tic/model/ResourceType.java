package com.grupo1.recursos_tic.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ResourceType {

    BOOK("Libro"),
    VIDEO("Video"),
    WEB("Página web");

    private final String reosurceType;

    @Override
    public String toString() { return reosurceType; }
}
