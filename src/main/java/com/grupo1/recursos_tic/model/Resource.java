package com.grupo1.recursos_tic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    private String author;

    private ResourceType type;

    @ElementCollection
    private Set<EnumTag> tags = new HashSet<>();

    @ManyToMany(mappedBy = "resources")
    @ToString.Exclude
    @JsonIgnore // Rompe la relación circular entre ResourceList y Resource que daba error en Swagger
    private List<ResourceList> lists = new ArrayList<>();

    public void removeFromAllLists() {
        for (ResourceList list : new ArrayList<>(lists)) {
            list.removeResource(this);
        }
        lists.clear();
    }

}
