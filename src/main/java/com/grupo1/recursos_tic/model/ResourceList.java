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
public class ResourceList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner;

    private String name;

    private String description;

    @ManyToMany
    @ToString.Exclude
    private Set<Resource> resources = new HashSet<>();

    public void addResource(Resource resource) {
        if (resource == null) return;
        resources.add(resource);
    }

    public void removeResource(Resource resource) {
        if (this.resources != null) this.resources.remove(resource);
        // Referencia inversa
        if (resource != null && resource.getLists() != null)
            resource.getLists().remove(this);
    }

}
