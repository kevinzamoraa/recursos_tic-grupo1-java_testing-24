package com.grupo1.recursos_tic.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String url;

    private String description;

    // author ¿(id) -> tipo Usuario (rol autor) ManyToOne || nombre?

    private ResourceType type;

    @ElementCollection
    private Set<Tag> tags = new HashSet<>();

    @Column(columnDefinition = "boolean default true")
    private Boolean active;

}
