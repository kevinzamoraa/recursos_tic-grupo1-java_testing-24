package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.Resource;
import com.grupo1.recursos_tic.model.ResourceList;
import com.grupo1.recursos_tic.service.ResourceListsService;
import com.grupo1.recursos_tic.service.ResourceService;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.grupo1.recursos_tic.util.Utility.*;
import static com.grupo1.recursos_tic.util.Utility.userAuth;

@Controller
@AllArgsConstructor
public class ResourceListsController {

    private ResourceListsService resourceListsService;
    private ResourceService resourceService;

    private final String idMsg = "Falta el id o no es un entero positivo";
    private final String notIdMsg = "La lista de recursos no existe";
    private final String dataMsg = "Los datos recibidos no son válidos";
    private final String delMsg = "Error al borrar todas las listas de recursos";

    @GetMapping("resourcelists")
    public String findAll(Model model) {
        model.addAttribute("resourcelists", resourceListsService.findAllById(userAuth().get().getId()));
        return "resourcelists/list";
    }

    @GetMapping("resourcelists/{id}")
    public String findById(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        if (!resourceListsService.existsById(id))
            throw new NoSuchElementException(notIdMsg);

        ResourceList resourceList = resourceListsService.findById(id).get();
        if (!Objects.equals(resourceList.getOwner().getId(), userAuth().get().getId()))
            throw new NoSuchElementException(dataMsg);

        model.addAttribute("resourcelist", resourceList);
        model.addAttribute("resources", resourceList.getResources());

        return "resourcelists/detail";
    }

    @GetMapping("resourcelists/create")
    public String getFormToCreate(Model model) {
        ResourceList resourcelist = new ResourceList();
        resourcelist.setOwner(userAuth().get());
        model.addAttribute("resourcelist", resourcelist);
        return "resourcelists/form";
    }

    @GetMapping("resourcelists/update/{id}")
    public String getFormToUpdate(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        if (!resourceListsService.existsById(id))
            throw new NoSuchElementException(notIdMsg);

        ResourceList resourceList = resourceListsService.findById(id).get();
        if (!Objects.equals(resourceList.getOwner().getId(), userAuth().get().getId()))
            throw new NoSuchElementException(dataMsg);

        model.addAttribute("resourcelist", resourceList);
        return "resourcelists/form";
    }

    /**
     * Procesa la petición de la vista de la lista de recursos
     * @param model Modelo
     * @param id Identificador de la lista de recursos
     * @return Vista de la lista de petición
     */
    @GetMapping("resourcelists/add/{id}")
    public String addResources(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        if (!resourceListsService.existsById(id))
            throw new NoSuchElementException(notIdMsg);

        List<Resource> allResources = resourceService.findAll();

        //ResourceList resourceToUpdate = resourceListsService.findById_Eager(id).get();
        ResourceList resourceToUpdate = resourceListsService.findById(id).get();
        Set<Resource> resources = resourceToUpdate.getResources();

        model.addAttribute("resourceListObject", resourceToUpdate);
        model.addAttribute("allResources", allResources);
        model.addAttribute("resources", resources);
        model.addAttribute("listId", id);
        return "resourcelists/catalog";
    }

    /**
     * Agrega recursos a una lista de recursos
     * @param resourceListObject Lista de recursos seleccionada
     * @param listId Identificador de la lista de recursos
     * @return Vista de la lista de recursos
     */
    @PostMapping("/resourcelists/add")
    public String addList(@ModelAttribute ResourceList resourceListObject,
                          @RequestParam(required = false) Long listId) {

        //var existingResourceList = resourceListsService.findById_Eager(resourceListObject.getId()).get();
        ResourceList existingResourceList = resourceListsService.findById(resourceListObject.getId()).get();
        Set<Resource> newResources = resourceListObject.getResources();
        if (!newResources.isEmpty()) {
            existingResourceList.setResources(newResources);
            resourceListsService.save(existingResourceList);
        }

        return "redirect:/resourcelists/" + listId;
    }

    @GetMapping("resourcelists/remove/{listId}/{id}")
    public String removeResource(Model model, @PathVariable Long listId, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0 || invalidIntPosNumber(listId) || listId == 0)
            throw new NoSuchElementException(idMsg);

        if (!resourceService.existsById(id) || !resourceListsService.existsById(listId))
            throw new NoSuchElementException(notIdMsg);

        //if (!resourceService.existsById(id) || !resourceListsService.existsById(listId))
        //    throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ResourceList existingResourceList = resourceListsService.findById(listId).get();
        Resource resource = resourceService.findById(id).get();
        existingResourceList.removeResource(resource);
        resourceListsService.save(existingResourceList);

        return "redirect:/resourcelists/" + listId;
    }

    @GetMapping("resourcelists/remove/{listId}")
    public String removeResource(Model model, @PathVariable Long listId) {
        if (invalidIntPosNumber(listId) || listId == 0)
            throw new NoSuchElementException(idMsg);

        if (!resourceListsService.existsById(listId))
            throw new NoSuchElementException(notIdMsg);

        ResourceList existingResourceList = resourceListsService.findById(listId).get();
        existingResourceList.setResources(new HashSet<>());
        resourceListsService.save(existingResourceList);

        return "redirect:/resourcelists/" + listId;
    }

    @PostMapping("resourcelists")
    public String save(@ModelAttribute ResourceList resourcelist) {
        if (resourcelist == null) throw new NoSuchElementException(dataMsg);
        String error = formValidation(resourcelist);
        if (error != null) throw new NoSuchElementException(error);

        if (resourcelist.getId() == null) { // crear
            resourceListsService.save(resourcelist);
            return "redirect:/resourcelists/" + resourcelist.getId();
        } else { // editar
            return resourceListsService.findById(resourcelist.getId()).map(optResourceList -> {
                BeanUtils.copyProperties(resourcelist, optResourceList);
                resourceListsService.save(optResourceList);
                return "redirect:/resourcelists/" + optResourceList.getId();
            }).orElseThrow(() -> new NoSuchElementException(notIdMsg));
        }
    }

    @GetMapping("resourcelists/delete/{id}")
    public String deleteById(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        if (!resourceListsService.existsById(id))
            throw new NoSuchElementException(notIdMsg);

        ResourceList resourceList = resourceListsService.findById(id).get();
        if (!Objects.equals(resourceList.getOwner().getId(), userAuth().get().getId()))
            throw new NoSuchElementException(dataMsg);

        resourceListsService.deleteById(resourceList.getId());
        return "redirect:/resourcelists";
    }

    @GetMapping("resourcelists/delete")
    public String deleteAll(Model model) {
        Long id = userAuth().get().getId();
        resourceListsService.deleteAll(id);
        if (resourceListsService.count(id) != 0)
            throw new NoSuchElementException(delMsg);
        return "redirect:/resourcelists";
    }

    public static String formValidation(ResourceList resourceLists) {
        if (stringIsEmpty(resourceLists.getName())) return "Falta el nombre";
        return null;
    }

}