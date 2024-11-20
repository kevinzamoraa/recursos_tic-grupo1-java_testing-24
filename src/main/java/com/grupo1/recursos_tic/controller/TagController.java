package com.grupo1.recursos_tic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

    @RestController
    @RequestMapping("/tags")
    public class TagController {

        @Autowired
        private TagRepository tagRepository;

        @GetMapping
        public List<Tag> getAllTags() {
            return tagRepository.findAll();
        }

        @GetMapping("/{id}")
        public ResponseEntity<Tag> getTagById(@PathVariable Long id) {
            Optional<Tag> tag = tagRepository.findById(id);
            if (tag.isPresent()) {
                return ResponseEntity.ok(tag.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @PostMapping
        public Tag createTag(@RequestBody Tag tag) {
            return tagRepository.save(tag);
        }

        @PutMapping("/{id}")
        public ResponseEntity<Tag> updateTag(@PathVariable Long id, @RequestBody Tag tagDetails) {
            Optional<Tag> tagOptional = tagRepository.findById(id);
            if (tagOptional.isPresent()) {
                Tag tag = tagOptional.get();
                tag.setName(tagDetails.getName());
                tag.setImageUrl(tagDetails.getImageUrl());
                tag.setDescription(tagDetails.getDescription());
                tag.setResources(tagDetails.getResources());
                return ResponseEntity.ok(tagRepository.save(tag));
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
            if (tagRepository.existsById(id)) {
                tagRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }
}
