package com.grupo1.recursos_tic.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.mapping.Map;
import java.util.HashMap;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imageUrl;

    private String description;


    @ElementCollection
    @CollectionTable(name = "tag_resources", joinColumns = @JoinColumn(name = "tag_id"))
    @MapKeyColumn(name = "resource_id")
    @Column(name = "resource_name")
    private Map<Integer, String> resources = new HashMap<>();

    // Getters y setters
        public Long getId() {return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<Integer, String> getResources() {
            return resources;
        }

        public void setResources(Map<Integer, String> resources) {
            this.resources = resources;
        }
    }

}
