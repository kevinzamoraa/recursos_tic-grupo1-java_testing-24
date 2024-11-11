package com.grupo1.recursos_tic.model;

import java.util.Map;
import java.util.HashMap;

import jakarta.persistence.*;
import lombok.*;

    @Entity
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString

    public class Tag {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private String imageUrl;
        private String description;
        @ManyToOne private Map<Long, Resource> resources = new HashMap<>();

        //Constructor
        public Tag() {}
        public Tag(String name, String imageUrl, String description) {
            this.name = name;
            this.imageUrl = imageUrl;
            this.description = description;
        }

        // Getters and setters
        public Long getId() {
            return id;
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

        public Map<Long, Resource> getResources() {
            return resources;
        }

        public void setResources(Map<Long, Resource> resources) {
            this.resources = resources;
        }

    }