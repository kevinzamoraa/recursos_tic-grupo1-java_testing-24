package com.grupo1.recursos_tic.service;

import com.grupo1.recursos_tic.model.Resource;
import com.grupo1.recursos_tic.repository.ResourceRepo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResourceService {

    private ResourceRepo resourceRepository;

    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    public boolean existsById(Long Id) {
        return resourceRepository.existsById(Id);
    }

    public long count() {
        return resourceRepository.count();
    }

    public Optional<Resource> findById(long id) {
        return resourceRepository.findById(id);
    }

    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    public void saveAll(List<Resource> resources) {
        resourceRepository.saveAll(resources);
    }

    public void deleteById(long id) {
        // TODO disasociar u otro
        resourceRepository.deleteById(id);
    }

    public void deleteAll() {
        // TODO disasociar u otro
        resourceRepository.deleteAll();
    }

}
