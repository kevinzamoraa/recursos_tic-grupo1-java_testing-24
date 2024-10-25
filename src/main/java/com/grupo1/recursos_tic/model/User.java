package com.grupo1.recursos_tic.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String imageUrl;
    private String password;
    private roleOptions role;
    public enum roleOptions {
        author,
        reader
    }

}
