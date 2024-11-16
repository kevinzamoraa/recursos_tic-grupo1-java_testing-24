package com.grupo1.recursos_tic.service;

import com.grupo1.recursos_tic.model.Resource;
import com.grupo1.recursos_tic.model.ResourceList;
import com.grupo1.recursos_tic.repository.ResourceListsRepo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResourceListsService {

    private ResourceListsRepo resourceListsRepository;

    public List<ResourceList> findAll(Long Id) {
        return resourceListsRepository.findAllByOwner_Id(Id);
    }

    public boolean existsById(Long Id) {
        return resourceListsRepository.existsById(Id);
    }

    public Long count(Long Id) {
        return resourceListsRepository.countByOwner_Id(Id);
    }

    public long count() {
        return resourceListsRepository.count();
    }

    public Optional<ResourceList> findById(long id) {
        return resourceListsRepository.findById(id);
    }

    public ResourceList save(ResourceList resourcelist) {
        return resourceListsRepository.save(resourcelist);
    }

    public void saveAll(List<ResourceList> resourcelists) {
        resourceListsRepository.saveAll(resourcelists);
    }

    public void deleteById(long id) {
        resourceListsRepository.deleteById(id);
    }

    public void deleteAll(Long Id) {
        resourceListsRepository.deleteAllByOwner_Id(Id);
    }

}
