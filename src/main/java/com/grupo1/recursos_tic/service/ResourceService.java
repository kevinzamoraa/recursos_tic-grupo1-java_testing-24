package com.grupo1.recursos_tic.service;

import com.grupo1.recursos_tic.model.Rating;
import com.grupo1.recursos_tic.model.Resource;
import com.grupo1.recursos_tic.repository.RatingRepo;
import com.grupo1.recursos_tic.repository.ResourceRepo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResourceService {

    private ResourceRepo resourceRepository;
    private RatingService ratingService;
    private ResourceListsService resourceListsService;

    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    public boolean existsById(Long Id) {
        return resourceRepository.existsById(Id);
    }

    public Optional<Resource> findById(long id) {
        return resourceRepository.findById(id);
    }

    public long count() {
        return resourceRepository.count();
    }

    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    public void saveAll(List<Resource> resources) {
        resourceRepository.saveAll(resources);
    }

    public void deleteById(long id) {
        resourceRepository.deleteById(id);
    }

    public void removeResourceWithDependencies(long id) {
        ratingService.deleteAllByResource_Id(id);
        removeResourceFromAllResourceLists(id);
        resourceRepository.deleteById(id);
    }

    public void removeResourceFromAllResourceLists(Long id) {
        Resource resource = findById(id).get();
        resource.removeFromAllLists();
        save(resource);
    }

    public void deleteAllByIdInBatch(List<Long> ids) {
        // TODO disasociar
        resourceRepository.deleteAllByIdInBatch(ids);
    }

    public void deleteAll() {
        // TODO disasociar
        resourceRepository.deleteAll();
    }

}
